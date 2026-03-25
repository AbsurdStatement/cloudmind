import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginView },
  { path: '/home', component: HomeView }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 简单路由守卫：未登录不允许访问首页
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/home' && !token) {
    next('/login')
    return
  }
  next()
})

export default router
