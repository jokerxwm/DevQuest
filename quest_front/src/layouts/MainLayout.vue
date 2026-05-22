<template>
  <div class="layout">
    <header class="header">
      <div class="container header-content">
        <div class="logo" @click="router.push('/')">
          <span class="logo-icon">?</span>
          <span class="logo-text">DevQuest</span>
        </div>
        <nav class="nav">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/questions" class="nav-item">问答</router-link>
          <router-link to="/hot" class="nav-item">热榜</router-link>
          <router-link to="/tags" class="nav-item">标签</router-link>
          <router-link v-if="userStore.isLoggedIn()" to="/ai" class="nav-item">AI助手</router-link>
        </nav>
        <div class="search-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索问题..."
            :prefix-icon="Search"
            clearable
            @keydown.enter="handleSearch"
          />
        </div>
        <div class="header-right">
          <template v-if="userStore.isLoggedIn()">
            <el-button type="primary" @click="router.push('/ask')">提问</el-button>
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
              <el-button :icon="Bell" circle @click="router.push('/notifications')" />
            </el-badge>
            <el-dropdown trigger="click">
              <div class="user-avatar">
                <el-avatar :size="32" :src="userStore.user?.avatar">
                  {{ userStore.user?.nickname?.charAt(0) }}
                </el-avatar>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push(`/user/${userStore.user?.id}`)">个人主页</el-dropdown-item>
                  <el-dropdown-item @click="router.push('/favorites')">我的收藏</el-dropdown-item>
                  <el-dropdown-item @click="router.push('/drafts')">我的草稿</el-dropdown-item>
                  <el-dropdown-item @click="router.push('/messages')">私信</el-dropdown-item>
                  <el-dropdown-item @click="router.push('/settings')">设置</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button @click="router.push('/login')">登录</el-button>
            <el-button type="primary" @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>
    <main class="main">
      <router-view />
    </main>
    <footer class="footer">
      <div class="container footer-content">
        <div class="footer-left">
          <span class="footer-logo">DevQuest</span>
          <span class="footer-desc">开发者技术问答社区</span>
        </div>
        <div class="footer-links">
          <router-link to="/">首页</router-link>
          <router-link to="/questions">问答</router-link>
          <router-link to="/tags">标签</router-link>
          <router-link to="/ai/daily">AI快讯</router-link>
        </div>
        <div class="footer-right">
          <p>© 2024 DevQuest. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bell, Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const unreadCount = ref(0)
const searchKeyword = ref('')

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

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  height: 60px;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 24px;
}

.logo-icon {
  font-size: 24px;
  font-weight: bold;
  color: #1e80ff;
  margin-right: 8px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #1e80ff;
}

.nav {
  display: flex;
  gap: 20px;
}

.nav-item {
  font-size: 15px;
  color: #666;
  cursor: pointer;
  padding: 8px 0;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
  white-space: nowrap;
}

.nav-item:hover,
.nav-item.router-link-active {
  color: #1e80ff;
  border-bottom-color: #1e80ff;
}

.search-bar {
  flex: 1;
  max-width: 400px;
  margin: 0 24px;
}

.header-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  cursor: pointer;
}

.notification-badge {
  margin-right: 8px;
}

.main {
  padding: 24px 0;
  min-height: calc(100vh - 60px - 120px);
}

.footer {
  background: #2c3e50;
  color: #bdc3c7;
  padding: 32px 0;
  margin-top: auto;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.footer-logo {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.footer-desc {
  font-size: 13px;
  color: #95a5a6;
}

.footer-links {
  display: flex;
  gap: 24px;
}

.footer-links a {
  color: #bdc3c7;
  font-size: 14px;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #fff;
}

.footer-right p {
  font-size: 13px;
  color: #7f8c8d;
}
</style>
