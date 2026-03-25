package com.cloudmind.service.impl;

import com.cloudmind.config.ParserProperties;
import com.cloudmind.dto.parser.ParseTextResponse;
import com.cloudmind.exception.BusinessException;
import com.cloudmind.service.ParserService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 文档解析服务实现：支持 pdf/doc/docx/txt
 */
@Service
@RequiredArgsConstructor
public class ParserServiceImpl implements ParserService {

    private final ParserProperties parserProperties;

    @Override
    public ParseTextResponse parseText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        validateFileSize(file);

        String fileName = file.getOriginalFilename();
        String ext = getExtension(fileName);

        String text;
        try {
            text = switch (ext) {
                case "pdf" -> parsePdf(file);
                case "doc" -> parseDoc(file);
                case "docx" -> parseDocx(file);
                case "txt" -> parseTxt(file);
                default -> throw new BusinessException("不支持的文件类型，仅支持 pdf/doc/docx/txt");
            };
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("文档解析失败: " + e.getMessage());
        }

        return new ParseTextResponse(
                fileName == null ? "unknown" : fileName,
                ext,
                text.length(),
                text
        );
    }

    /**
     * PDF 提取纯文本，并尽量保留段落结构
     */
    private String parsePdf(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String raw = stripper.getText(document);
            return normalizeParagraphs(raw);
        }
    }

    /**
     * DOC 提取纯文本，并保留段落结构
     */
    private String parseDoc(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             HWPFDocument document = new HWPFDocument(inputStream);
             WordExtractor extractor = new WordExtractor(document)) {
            String[] paragraphs = extractor.getParagraphText();
            List<String> cleaned = new ArrayList<>();
            for (String paragraph : paragraphs) {
                String p = paragraph == null ? "" : paragraph.replace("\r", "").trim();
                if (!p.isEmpty()) {
                    cleaned.add(p);
                }
            }
            return String.join("\n\n", cleaned);
        }
    }

    /**
     * DOCX 提取纯文本，并保留段落结构
     */
    private String parseDocx(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {
            List<String> paragraphs = new ArrayList<>();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String p = paragraph.getText() == null ? "" : paragraph.getText().trim();
                if (!p.isEmpty()) {
                    paragraphs.add(p);
                }
            }
            return String.join("\n\n", paragraphs);
        }
    }

    /**
     * TXT 提取纯文本，并保留空行分段
     */
    private String parseTxt(MultipartFile file) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        }
        return normalizeParagraphs(builder.toString());
    }

    private String normalizeParagraphs(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String normalized = raw.replace("\r\n", "\n").replace("\r", "\n");
        String[] blocks = normalized.split("\n{2,}");
        List<String> paragraphs = new ArrayList<>();
        for (String block : blocks) {
            String paragraph = block.trim();
            if (!paragraph.isEmpty()) {
                paragraphs.add(paragraph);
            }
        }
        return String.join("\n\n", paragraphs);
    }

    private void validateFileSize(MultipartFile file) {
        long maxBytes = parserProperties.getMaxSizeMb() * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new BusinessException("文件过大，最大支持 " + parserProperties.getMaxSizeMb() + "MB");
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new BusinessException("文件名缺少后缀，无法识别类型");
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
