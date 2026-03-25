package com.cloudmind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudmind.config.VectorProperties;
import com.cloudmind.dto.ai.AiExtractResponse;
import com.cloudmind.dto.rag.RagFileResult;
import com.cloudmind.dto.vector.VectorSearchItem;
import com.cloudmind.entity.FileRecord;
import com.cloudmind.exception.BusinessException;
import com.cloudmind.mapper.FileRecordMapper;
import com.cloudmind.service.AiService;
import com.cloudmind.service.RagService;
import com.cloudmind.service.VectorStoreService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RAG 检索实现：向量召回 + 文件名混合检索
 */
@Service
@RequiredArgsConstructor
public class RagServiceImpl implements RagService {

    private final VectorStoreService vectorStoreService;
    private final FileRecordMapper fileRecordMapper;
    private final AiService aiService;
    private final VectorProperties vectorProperties;

    @Override
    public List<RagFileResult> query(Long userId, String question, Integer topK) {
        if (question == null || question.isBlank()) {
            throw new BusinessException("question 不能为空");
        }

        int k = topK == null || topK <= 0 ? vectorProperties.getDefaultTopK() : topK;
        int recallK = Math.max(k * 5, 20);

        // 1) 先向量召回相关 chunk
        List<VectorSearchItem> vectorHits = vectorStoreService.search(question, recallK);

        // 2) 聚合为文档级别（选最高相似 chunk，并拼接若干片段用于摘要标签）
        Map<String, DocScore> docScoreMap = new HashMap<>();
        for (VectorSearchItem hit : vectorHits) {
            DocScore score = docScoreMap.computeIfAbsent(hit.getDocumentId(), d -> new DocScore(d));
            score.maxVectorScore = Math.max(score.maxVectorScore, hit.getScore() == null ? 0D : hit.getScore());
            if (score.topChunk == null || score.topChunk.isBlank()) {
                score.topChunk = hit.getChunkText();
            }
            if (score.summaryMaterial.length() < 2000 && hit.getChunkText() != null) {
                score.summaryMaterial.append(hit.getChunkText()).append("\n\n");
            }
        }

        if (docScoreMap.isEmpty()) {
            return Collections.emptyList();
        }

        // 3) 文件名混合检索：命中名称给予加分
        List<FileRecord> userFiles = fileRecordMapper.selectList(new LambdaQueryWrapper<FileRecord>()
                .eq(FileRecord::getUserId, userId));
        Map<Long, FileRecord> fileById = userFiles.stream()
                .collect(Collectors.toMap(FileRecord::getId, f -> f, (a, b) -> a));

        Map<Long, Double> fileNameBoost = new HashMap<>();
        String lowerQ = question.toLowerCase(Locale.ROOT);
        for (FileRecord file : userFiles) {
            if (file.getFileName() != null && file.getFileName().toLowerCase(Locale.ROOT).contains(lowerQ)) {
                fileNameBoost.put(file.getId(), 1.0);
            }
        }

        // 4) 将 documentId 映射到 fileRecord，并计算融合分数
        List<RankedFile> ranked = new ArrayList<>();
        for (DocScore docScore : docScoreMap.values()) {
            FileRecord file = resolveFileRecord(docScore.documentId, fileById, userFiles);
            if (file == null) {
                continue;
            }
            double nameScore = fileNameBoost.getOrDefault(file.getId(), 0D);
            double finalScore = docScore.maxVectorScore * 0.75 + nameScore * 0.25;
            ranked.add(new RankedFile(file, finalScore, docScore.topChunk, docScore.summaryMaterial.toString()));
        }

        ranked.sort((a, b) -> Double.compare(b.score, a.score));

        // 5) 生成返回：最相关文件 + 摘要标签 + 相似度 + top chunk
        List<RagFileResult> result = new ArrayList<>();
        for (int i = 0; i < Math.min(k, ranked.size()); i++) {
            RankedFile item = ranked.get(i);
            String summary;
            List<String> tags;
            try {
                AiExtractResponse ai = aiService.extractSummaryAndTags(item.material);
                summary = ai.getSummary();
                tags = ai.getTags();
            } catch (Exception e) {
                summary = "自动摘要失败，可稍后重试";
                tags = List.of("解析失败", "可重试", "CloudMind");
            }

            result.add(new RagFileResult(
                    item.file.getId(),
                    item.file.getFileName(),
                    summary,
                    tags,
                    item.score,
                    item.topChunk
            ));
        }

        return result;
    }

    /**
     * documentId 支持三种映射：
     * 1) 纯数字：按 file_record.id
     * 2) objectKey 完全匹配
     * 3) fileName 完全匹配
     */
    private FileRecord resolveFileRecord(String documentId,
                                         Map<Long, FileRecord> fileById,
                                         List<FileRecord> userFiles) {
        if (documentId == null || documentId.isBlank()) {
            return null;
        }

        try {
            long id = Long.parseLong(documentId);
            return fileById.get(id);
        } catch (NumberFormatException ignore) {
            // 非数字继续匹配 objectKey/fileName
        }

        for (FileRecord file : userFiles) {
            if (documentId.equals(file.getObjectKey()) || documentId.equals(file.getFileName())) {
                return file;
            }
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    private static class RankedFile {
        private FileRecord file;
        private double score;
        private String topChunk;
        private String material;
    }

    private static class DocScore {
        private final String documentId;
        private double maxVectorScore = 0D;
        private String topChunk;
        private final StringBuilder summaryMaterial = new StringBuilder();

        private DocScore(String documentId) {
            this.documentId = documentId;
        }
    }
}
