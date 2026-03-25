import http from './http'

// 上传文件（拖拽上传）
export function uploadFile(folderId, file) {
  const formData = new FormData()
  formData.append('folderId', folderId)
  formData.append('file', file)
  return http.post('/storage/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
