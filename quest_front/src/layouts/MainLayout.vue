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
          <router-link v-if="userStore.isLoggedIn()" to="/ai" class="nav-item">AI助手</router-link>
        </nav>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bell } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const unreadCount = ref(0)

const handleLogout = async () => {
  await userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
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
  margin-right: 40px;
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
  gap: 24px;
}

.nav-item {
  font-size: 16px;
  color: #666;
  cursor: pointer;
  padding: 8px 0;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.nav-item:hover,
.nav-item.router-link-active {
  color: #1e80ff;
  border-bottom-color: #1e80ff;
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
}
</style>
