import http from './http'

// 用户注册
export function register(data) {
  return http.post('/auth/register', data)
}

// 用户登录
export function login(data) {
  return http.post('/auth/login', data)
}

// 获取当前登录用户
export function me() {
  return http.get('/me')
}
