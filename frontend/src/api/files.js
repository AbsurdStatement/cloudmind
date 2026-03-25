import http from './http'

// 分页查询文件元数据
export function pageFiles(params) {
  return http.get('/files', { params })
}

// 删除文件元数据
export function deleteFile(fileId) {
  return http.delete(`/files/${fileId}`)
}
