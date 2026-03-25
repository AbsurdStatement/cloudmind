import http from './http'

// RAG 检索
export function ragQuery(data) {
  return http.post('/rag/query', data)
}
