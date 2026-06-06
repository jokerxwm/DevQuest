<template>
  <div class="layout">
    <div class="nav-bg-shape" v-if="!isMobileMenuOpen"></div>

    <nav class="header">
      <div class="container header-content">
        <div class="logo" @click="router.push('/')">
          <div class="logo-mark">
            <span>DQ</span>
          </div>
          <div class="logo-text-group">
            <span class="logo-text">DevQuest</span>
            <span class="logo-sub">Developer Q&A</span>
          </div>
        </div>

        <div class="nav-links">
          <router-link to="/" class="nav-item">
            <span>首页</span>
          </router-link>
          <router-link to="/questions" class="nav-item">
            <span>问答</span>
          </router-link>
          <router-link to="/hot" class="nav-item">
            <span>热榜</span>
          </router-link>
          <router-link to="/tags" class="nav-item">
            <span>标签</span>
          </router-link>
          <router-link v-if="userStore.isLoggedIn()" to="/ai" class="nav-item nav-ai">
            <span>AI 助手</span>
          </router-link>
        </div>

        <div class="search-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索问题、技术、标签..."
            :prefix-icon="Search"
            clearable
            @keydown.enter="handleSearch"
          />
        </div>

        <div class="header-actions">
          <template v-if="userStore.isLoggedIn()">
            <el-button type="primary" class="ask-btn" @click="router.push('/ask')">
              提问
            </el-button>
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="noti-badge">
              <button class="icon-btn" @click="router.push('/notifications')">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                  <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
                </svg>
              </button>
            </el-badge>
            <el-dropdown trigger="click">
              <div class="user-avatar">
                <span class="avatar-letter">{{ userStore.user?.nickname?.charAt(0) || userStore.user?.username?.charAt(0) || 'U' }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push(`/user/${userStore.user?.id}`)">个人主页</el-dropdown-item>
                  <el-dropdown-item @click="router.push('/favorites')">我的收藏</el-dropdown-item>
                  <el-dropdown-item @click="router.push('/drafts')">我的草稿</el-dropdown-item>
                  <el-dropdown-item @click="router.push('/settings')">设置</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="login-link">登录</router-link>
            <el-button type="primary" class="register-btn" @click="router.push('/register')">注册</el-button>
          </template>

          <button class="mobile-menu-btn" @click="isMobileMenuOpen = true">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <line x1="3" y1="6" x2="21" y2="6"></line>
              <line x1="3" y1="12" x2="21" y2="12"></line>
              <line x1="3" y1="18" x2="21" y2="18"></line>
            </svg>
          </button>
        </div>
      </div>
    </nav>

    <div class="mobile-overlay" :class="{ open: isMobileMenuOpen }">
      <button class="mobile-close" @click="isMobileMenuOpen = false">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <line x1="18" y1="6" x2="6" y2="18"></line>
          <line x1="6" y1="6" x2="18" y2="18"></line>
        </svg>
      </button>
      <div class="mobile-links">
        <router-link to="/" class="mobile-link" @click="isMobileMenuOpen = false">首页</router-link>
        <router-link to="/questions" class="mobile-link" @click="isMobileMenuOpen = false">问答</router-link>
        <router-link to="/hot" class="mobile-link" @click="isMobileMenuOpen = false">热榜</router-link>
        <router-link to="/tags" class="mobile-link" @click="isMobileMenuOpen = false">标签</router-link>
        <router-link v-if="userStore.isLoggedIn()" to="/ai" class="mobile-link" @click="isMobileMenuOpen = false">AI 助手</router-link>
        <router-link to="/ask" class="mobile-link mobile-cta" @click="isMobileMenuOpen = false">提问</router-link>
      </div>
    </div>

    <main class="main">
      <router-view />
    </main>

    <footer class="footer">
      <div class="container footer-content">
        <div class="footer-brand">
          <div class="footer-logo">
            <div class="footer-logo-mark">DQ</div>
            <div>
              <span class="footer-logo-text">DevQuest</span>
              <span class="footer-logo-sub">Developer Q&A</span>
            </div>
          </div>
          <p class="footer-desc">AI 驱动的开发者技术问答社区，让每一个技术问题都能找到答案。</p>
        </div>
        <div class="footer-links-group">
          <div class="footer-col">
            <h4>探索</h4>
            <router-link to="/">首页</router-link>
            <router-link to="/questions">问答</router-link>
            <router-link to="/hot">热榜</router-link>
            <router-link to="/tags">标签</router-link>
          </div>
          <div class="footer-col">
            <h4>功能</h4>
            <router-link to="/ai">AI 助手</router-link>
            <router-link to="/ai/daily">AI 快讯</router-link>
            <router-link to="/ask">提出问题</router-link>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <div class="container">
          <p>&copy; {{ new Date().getFullYear() }} DevQuest. Crafted with care for developers.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const unreadCount = ref(0)
const searchKeyword = ref('')
const isMobileMenuOpen = ref(false)

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { q: searchKeyword.value.trim() } })
  }
}

const fetchUnreadCount = async () => {
  if (!userStore.isLoggedIn()) return
  try {
    const res = await request.get('/notifications/unread-count')
    unreadCount.value = res.data || 0
  } catch (error) {
    console.error('获取未读通知数失败:', error)
  }
}

const handleLogout = async () => {
  await userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}

onMounted(() => {
  fetchUnreadCount()
})
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.nav-bg-shape {
  position: fixed;
  top: 0;
  left: 0;
  width: 260px;
  height: 220px;
  background: var(--forest);
  border-radius: 0 0 120px 0;
  z-index: 40;
  pointer-events: none;
}

.header {
  position: sticky;
  top: 0;
  z-index: 50;
  padding: 0 24px;
}

.header-content {
  display: flex;
  align-items: center;
  height: 80px;
  gap: 12px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  width: 220px;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
  z-index: 50;
}

.logo-mark {
  width: 40px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-mark span {
  font-size: 20px;
  font-weight: 800;
  color: var(--caramel);
  letter-spacing: -1px;
  font-family: 'Playfair Display', 'Noto Serif SC', serif;
}

.logo-text-group {
  display: flex;
  flex-direction: column;
}

.logo-text {
  font-size: 14px;
  font-weight: 600;
  color: var(--caramel);
  letter-spacing: 0.15em;
  text-transform: uppercase;
}

.logo-sub {
  font-size: 10px;
  color: var(--caramel-light);
  letter-spacing: 0.1em;
  text-transform: uppercase;
  margin-top: 2px;
}

.nav-links {
  display: flex;
  gap: 8px;
  margin-left: 16px;
}

.nav-item {
  font-size: 13px;
  color: var(--text-secondary);
  padding: 8px 16px;
  border-radius: 999px;
  transition: var(--transition);
  white-space: nowrap;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.nav-item:hover {
  color: var(--caramel);
  background: rgba(184, 138, 89, 0.06);
}

.nav-item.router-link-active {
  color: var(--caramel);
  font-weight: 600;
}

.nav-ai {
  position: relative;
}

.nav-ai::after {
  content: 'Beta';
  position: absolute;
  top: 2px;
  right: 4px;
  font-size: 8px;
  background: var(--caramel);
  color: #fff;
  padding: 1px 5px;
  border-radius: 4px;
  font-weight: 700;
  line-height: 1.2;
}

.search-bar {
  flex: 1;
  max-width: 380px;
  margin: 0 16px;
}

.search-bar :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid var(--border-light);
  box-shadow: none !important;
  border-radius: 999px !important;
  padding: 4px 16px;
  transition: var(--transition);
  backdrop-filter: blur(8px);
}

.search-bar :deep(.el-input__wrapper:hover) {
  background: #fff;
  border-color: var(--caramel-light);
}

.search-bar :deep(.el-input__wrapper.is-focus) {
  background: #fff;
  border-color: var(--caramel);
  box-shadow: 0 0 0 3px rgba(184, 138, 89, 0.08) !important;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  margin-left: auto;
}

.ask-btn {
  border-radius: 999px !important;
  padding: 10px 24px !important;
  font-weight: 600 !important;
  font-size: 13px !important;
  letter-spacing: 0.5px;
}

.icon-btn {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  border: 1px solid var(--border-light);
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  transition: var(--transition);
}

.icon-btn:hover {
  border-color: var(--caramel);
  color: var(--caramel);
  background: rgba(184, 138, 89, 0.04);
}

.noti-badge :deep(.el-badge__content) {
  background: var(--caramel);
  border: 2px solid var(--bg-page);
}

.user-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: var(--forest);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: var(--transition);
}

.user-avatar:hover {
  box-shadow: 0 0 0 3px rgba(184, 138, 89, 0.2);
  transform: scale(1.05);
}

.avatar-letter {
  color: var(--caramel);
  font-weight: 700;
  font-size: 15px;
  font-family: 'Playfair Display', serif;
}

.login-link {
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
  padding: 8px 16px;
  transition: var(--transition);
}

.login-link:hover {
  color: var(--caramel);
}

.register-btn {
  border-radius: 999px !important;
  font-weight: 600 !important;
  font-size: 13px !important;
}

.mobile-menu-btn {
  display: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 1px solid var(--border-light);
  background: #fff;
  align-items: center;
  justify-content: center;
  color: var(--text-primary);
}

.mobile-overlay {
  position: fixed;
  inset: 0;
  background: var(--forest);
  z-index: 100;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transform: translateX(100%);
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.mobile-overlay.open {
  transform: translateX(0);
}

.mobile-close {
  position: absolute;
  top: 32px;
  right: 24px;
  color: var(--cream);
  background: none;
  border: none;
  padding: 8px;
}

.mobile-links {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.mobile-link {
  font-family: 'Playfair Display', 'Noto Serif SC', serif;
  font-size: 28px;
  color: var(--cream);
  letter-spacing: -0.5px;
  transition: var(--transition);
}

.mobile-link:hover {
  color: var(--caramel);
}

.mobile-cta {
  margin-top: 24px;
  border: 1px solid var(--caramel);
  padding: 12px 32px;
  border-radius: 999px;
  font-size: 16px;
  font-family: 'Inter', sans-serif;
}

.main {
  flex: 1;
  min-height: calc(100vh - 80px);
}

.footer {
  background: var(--forest);
  color: var(--cream);
  padding: 64px 0 0;
  margin-top: 80px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 48px;
}

.footer-brand {
  max-width: 320px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.footer-logo-mark {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 800;
  color: var(--caramel);
  font-family: 'Playfair Display', serif;
}

.footer-logo-text {
  font-size: 16px;
  font-weight: 600;
  color: var(--cream);
  letter-spacing: 0.15em;
  text-transform: uppercase;
  display: block;
}

.footer-logo-sub {
  font-size: 10px;
  color: rgba(250, 248, 243, 0.5);
  letter-spacing: 0.1em;
  text-transform: uppercase;
  display: block;
  margin-top: 2px;
}

.footer-desc {
  font-size: 13px;
  color: rgba(250, 248, 243, 0.6);
  line-height: 1.8;
}

.footer-links-group {
  display: flex;
  gap: 64px;
}

.footer-col {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer-col h4 {
  font-size: 12px;
  font-weight: 600;
  color: var(--caramel);
  text-transform: uppercase;
  letter-spacing: 0.15em;
  margin-bottom: 4px;
}

.footer-col a {
  font-size: 13px;
  color: rgba(250, 248, 243, 0.6);
  transition: var(--transition);
}

.footer-col a:hover {
  color: var(--cream);
  padding-left: 4px;
}

.footer-bottom {
  padding: 24px 0;
  margin-top: 48px;
  border-top: 1px solid rgba(250, 248, 243, 0.08);
}

.footer-bottom p {
  font-size: 12px;
  color: rgba(250, 248, 243, 0.35);
  text-align: center;
}

@media (max-width: 1024px) {
  .nav-bg-shape {
    display: none;
  }
  .logo {
    width: auto;
    justify-content: flex-start;
  }
  .nav-links {
    display: none;
  }
  .search-bar {
    display: none;
  }
  .mobile-menu-btn {
    display: flex;
  }
  .login-link {
    display: none;
  }
}

@media (max-width: 768px) {
  .footer-links-group {
    gap: 40px;
  }
}
</style>
