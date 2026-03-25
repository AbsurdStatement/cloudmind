<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <div class="title">CloudMind 登录</div>
      </template>

      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onLogin">登录</el-button>
          <el-button @click="onRegister">注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '../api/auth'

const router = useRouter()

const form = reactive({
  username: '',
  password: ''
})

// 登录
const onLogin = async () => {
  try {
    const { data } = await login(form)
    localStorage.setItem('token', data.data.token)
    ElMessage.success('登录成功')
    await router.push('/home')
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '登录失败')
  }
}

// 注册
const onRegister = async () => {
  try {
    await register(form)
    ElMessage.success('注册成功，请登录')
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '注册失败')
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
}

.login-card {
  width: 420px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  text-align: center;
}
</style>
