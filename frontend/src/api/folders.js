import http from './http'

// 查询目录树（按父节点分页查询后拼装）
export function pageFolders(params) {
  return http.get('/folders', { params })
}
