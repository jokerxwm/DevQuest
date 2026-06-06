<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-left">
        <div class="brand">
          <svg width="40" height="48" viewBox="0 0 40 48" fill="none" xmlns="http://www.w3.org/2000/svg" class="brand-icon">
            <path d="M20 2L4 16V46H36V16L20 2Z" stroke="currentColor" stroke-width="1.5" />
            <path d="M12 46V24C12 19.5817 15.5817 16 20 16C24.4183 16 28 19.5817 28 24V46" stroke="currentColor" stroke-width="1.5" />
            <circle cx="20" cy="30" r="4" stroke="currentColor" stroke-width="1.5" />
            <path d="M16 36C16 33.7909 17.7909 32 20 32C22.2091 32 24 33.7909 24 36" stroke="currentColor" stroke-width="1.5" />
          </svg>
          <div class="brand-text">
            <span class="brand-name">DevQuest</span>
            <span class="brand-sub">Developer Q&A Community</span>
          </div>
        </div>
        <h1 class="auth-heading">欢迎回来</h1>
        <p class="auth-desc">登录你的账号，继续探索技术世界</p>
        <div class="auth-features">
          <div class="af-item">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="2" y="3" width="20" height="14" rx="2"/><path d="M8 21h8M12 17v4"/></svg>
            <span>海量技术问答</span>
          </div>
          <div class="af-item">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/></svg>
            <span>AI 智能回答</span>
          </div>
          <div class="af-item">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            <span>活跃开发者社区</span>
          </div>
        </div>
      </div>
      <div class="auth-right">
        <div class="auth-form-wrap">
          <h2 class="form-title">登录</h2>
          <p class="form-sub">请输入你的账号信息</p>
          <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="密码" size="large" :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
            </el-form-item>
            <button type="button" class="auth-submit" :class="{ loading }" :disabled="loading" @click="handleLogin">
              {{ loading ? '登录中...' : '登录' }}
            </button>
          </el-form>
          <p class="auth-footer">
            还没有账号？
            <router-link to="/register" class="auth-link">立即注册</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

const handleLogin = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login({ email: form.username, password: form.password })
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cream-dark);
  padding: 80px 20px 40px;
}

.auth-container {
  display: flex;
  background: #fff;
  border-radius: 24px;
  overflow: hidden;
  max-width: 900px;
  width: 100%;
  min-height: 540px;
  box-shadow: 0 24px 64px rgba(26, 46, 38, 0.08);
}

.auth-left {
  width: 45%;
  background: var(--forest);
  padding: 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.auth-left::after {
  content: '';
  position: absolute;
  top: -30%;
  right: -30%;
  width: 80%;
  height: 80%;
  background: radial-gradient(circle, rgba(184, 138, 89, 0.12) 0%, transparent 70%);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 40px;
  position: relative;
}

.brand-icon {
  color: var(--caramel);
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-name {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1.1rem;
  font-weight: 500;
  color: var(--cream);
  letter-spacing: 0.05em;
}

.brand-sub {
  font-size: 0.65rem;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--cream-50);
  margin-top: 2px;
}

.auth-heading {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 2rem;
  font-weight: 400;
  color: var(--cream);
  margin-bottom: 12px;
  line-height: 1.2;
  position: relative;
}

.auth-desc {
  font-size: 0.9rem;
  color: var(--cream-60);
  line-height: 1.7;
  margin-bottom: 36px;
  font-weight: 300;
  position: relative;
}

.auth-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: relative;
}

.af-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 0.85rem;
  color: var(--cream-80);
  font-weight: 400;
}

.af-item svg {
  color: var(--caramel);
  flex-shrink: 0;
}

.auth-right {
  flex: 1;
  padding: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.auth-form-wrap {
  width: 100%;
  max-width: 340px;
}

.form-title {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1.75rem;
  font-weight: 400;
  color: var(--forest);
  margin-bottom: 6px;
}

.form-sub {
  font-size: 0.85rem;
  color: var(--forest-50);
  margin-bottom: 32px;
  font-weight: 300;
}

.auth-submit {
  display: block;
  width: 100%;
  padding: 14px 0;
  background: var(--caramel);
  color: #fff;
  border: none;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 500;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.3s;
  margin-top: 8px;
}

.auth-submit:hover {
  background: var(--caramel-light);
}

.auth-submit.loading {
  opacity: 0.7;
  cursor: not-allowed;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 0.85rem;
  color: var(--forest-50);
}

.auth-link {
  color: var(--caramel);
  font-weight: 600;
  text-decoration: none;
  margin-left: 4px;
  transition: color 0.2s;
}

.auth-link:hover {
  color: var(--caramel-light);
}

@media (max-width: 768px) {
  .auth-left { display: none; }
  .auth-container { border-radius: 20px; }
  .auth-right { padding: 36px 28px; }
}
</style>
