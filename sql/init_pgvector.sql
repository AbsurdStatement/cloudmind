-- pgvector 初始化
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS document_chunk (
    id BIGSERIAL PRIMARY KEY,
    document_id VARCHAR(128) NOT NULL,
    chunk_index INT NOT NULL,
    chunk_text TEXT NOT NULL,
    embedding VECTOR(1024) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_document_chunk_doc ON document_chunk(document_id);
-- ivfflat 索引可显著加速检索（需根据数据规模调参 lists）
CREATE INDEX IF NOT EXISTS idx_document_chunk_embedding
ON document_chunk USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
