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
        <h1 class="auth-heading">加入社区</h1>
        <p class="auth-desc">创建你的开发者账号，开始技术问答之旅</p>
        <div class="auth-features">
          <div class="af-item">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><path d="M8 14s1.5 2 4 2 4-2 4-2M9 9h.01M15 9h.01"/></svg>
            <span>提问获得精准解答</span>
          </div>
          <div class="af-item">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
            <span>分享你的技术经验</span>
          </div>
          <div class="af-item">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
            <span>建立技术影响力</span>
          </div>
        </div>
      </div>
      <div class="auth-right">
        <div class="auth-form-wrap">
          <h2 class="form-title">注册</h2>
          <p class="form-sub">创建你的 DevQuest 账号</p>
          <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleRegister">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="email">
              <el-input v-model="form.email" placeholder="邮箱" size="large" :prefix-icon="Message" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="密码（至少6位）" size="large" :prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" :prefix-icon="Lock" show-password />
            </el-form-item>
            <button type="button" class="auth-submit" :class="{ loading }" :disabled="loading" @click="handleRegister">
              {{ loading ? '注册中...' : '注册' }}
            </button>
          </el-form>
          <p class="auth-footer">
            已有账号？
            <router-link to="/login" class="auth-link">立即登录</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.register({
      username: form.username,
      email: form.email,
      password: form.password
    })
    ElMessage.success('注册成功')
    router.push('/')
  } catch (error) {
    console.error('注册失败:', error)
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
  min-height: 580px;
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
