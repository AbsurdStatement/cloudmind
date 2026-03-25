package com.cloudmind.service.impl;

import com.cloudmind.config.VectorProperties;
import com.cloudmind.dto.vector.VectorIndexResponse;
import com.cloudmind.dto.vector.VectorSearchItem;
import com.cloudmind.exception.BusinessException;
import com.cloudmind.service.EmbeddingService;
import com.cloudmind.service.TextChunkService;
import com.cloudmind.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * pgvector 入库与检索实现
 */
@Service
@RequiredArgsConstructor
public class VectorStoreServiceImpl implements VectorStoreService {

    @Qualifier("pgVectorJdbcTemplate")
    private final JdbcTemplate pgVectorJdbcTemplate;
    private final TextChunkService textChunkService;
    private final EmbeddingService embeddingService;
    private final VectorProperties vectorProperties;

    @Override
    public VectorIndexResponse index(String documentId, String text) {
        if (documentId == null || documentId.isBlank()) {
            throw new BusinessException("documentId 不能为空");
        }

        List<String> chunks = textChunkService.split(text);

        // 同一个 document 重新入库前先清空
        pgVectorJdbcTemplate.update("DELETE FROM document_chunk WHERE document_id = ?", documentId);

        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            float[] vector = embeddingService.embed(chunk);
            String vectorLiteral = toVectorLiteral(vector);

            pgVectorJdbcTemplate.update(
                    "INSERT INTO document_chunk(document_id, chunk_index, chunk_text, embedding) VALUES (?, ?, ?, ?::vector)",
                    documentId,
                    i,
                    chunk,
                    vectorLiteral
            );
        }

        return new VectorIndexResponse(documentId, chunks.size());
    }

    @Override
    public List<VectorSearchItem> search(String query, Integer topK) {
        int limit = topK == null || topK <= 0 ? vectorProperties.getDefaultTopK() : topK;
        float[] queryVector = embeddingService.embed(query);
        String vectorLiteral = toVectorLiteral(queryVector);

        String sql = "SELECT id, document_id, chunk_index, chunk_text, 1 - (embedding <=> ?::vector) AS score " +
                "FROM document_chunk ORDER BY embedding <=> ?::vector LIMIT ?";

        return pgVectorJdbcTemplate.query(
                sql,
                (rs, rowNum) -> new VectorSearchItem(
                        rs.getLong("id"),
                        rs.getString("document_id"),
                        rs.getInt("chunk_index"),
                        rs.getString("chunk_text"),
                        rs.getDouble("score")
                ),
                vectorLiteral,
                vectorLiteral,
                limit
        );
    }

    private String toVectorLiteral(float[] vector) {
        if (vector == null || vector.length == 0) {
            throw new BusinessException("向量为空，无法入库");
        }
        return "[" + java.util.Arrays.stream(toDoubleArray(vector))
                .mapToObj(Double::toString)
                .collect(Collectors.joining(",")) + "]";
    }

    private double[] toDoubleArray(float[] source) {
        double[] target = new double[source.length];
        for (int i = 0; i < source.length; i++) {
            target[i] = source[i];
        }
        return target;
    }
}
