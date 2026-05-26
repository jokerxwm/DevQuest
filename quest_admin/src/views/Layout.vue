<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <h2>DevQuest</h2>
        <span>管理后台</span>
      </div>
      <el-menu :default-active="route.path" router background-color="#001529" text-color="#ffffffa6" active-text-color="#fff">
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb>
            <el-breadcrumb-item>{{ currentMenu?.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ adminInfo.nickname || '管理员' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, ArrowDown, Odometer, PriceTag, Document, ChatDotRound } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const menuItems = [
  { path: '/dashboard', title: '仪表盘', icon: 'Odometer' },
  { path: '/tags', title: '标签管理', icon: 'PriceTag' },
  { path: '/users', title: '用户管理', icon: 'User' },
  { path: '/questions', title: '问题管理', icon: 'Document' },
  { path: '/answers', title: '回答管理', icon: 'ChatDotRound' }
]

const currentMenu = computed(() => menuItems.find(item => item.path === route.path))

const adminInfo = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('admin_info') || '{}')
  } catch {
    return {}
  }
})

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_info')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.aside {
  background: #001529;
  overflow-y: auto;
}

.logo {
  height: 64px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
}

.logo h2 {
  color: #fff;
  font-size: 18px;
  margin: 0;
}

.logo span {
  color: #ffffffa6;
  font-size: 12px;
}

.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 0 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #333;
}

.main {
  background: #f0f2f5;
  padding: 20px;
}
</style>
