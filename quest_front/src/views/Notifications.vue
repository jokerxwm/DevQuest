<template>
  <div class="notifications container">
    <div class="card">
      <div class="header">
        <h2>消息通知</h2>
        <el-button text @click="markAllRead">全部已读</el-button>
      </div>

      <div v-loading="loading">
        <div v-for="notification in notifications" :key="notification.id" class="notification-item" :class="{ unread: !notification.read }" @click="handleClick(notification)">
          <div class="notification-icon">
            <el-icon v-if="notification.type === 'answer'" :size="20"><ChatDotRound /></el-icon>
            <el-icon v-else-if="notification.type === 'vote'" :size="20"><Star /></el-icon>
            <el-icon v-else-if="notification.type === 'accept'" :size="20"><CircleCheck /></el-icon>
            <el-icon v-else :size="20"><Bell /></el-icon>
          </div>
          <div class="notification-content">
            <p class="notification-text">{{ notification.content }}</p>
            <span class="notification-time">{{ formatTime(notification.createdAt) }}</span>
          </div>
        </div>

        <el-empty v-if="!loading && notifications.length === 0" description="暂无通知" />
      </div>

      <div v-if="total > 0" class="pagination">
        <el-pagination
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchNotifications"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ChatDotRound, Star, CircleCheck, Bell } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const notifications = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 2592000000) return `${Math.floor(diff / 86400000)}天前`
  return date.toLocaleDateString()
}

const fetchNotifications = async () => {
  loading.value = true
  try {
    const res = await request.get('/notifications/list', {
      params: { page: page.value, size: pageSize.value }
    })
    notifications.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取通知失败:', error)
  } finally {
    loading.value = false
  }
}

const markAllRead = async () => {
  try {
    await request.post('/notifications/read-all')
    ElMessage.success('已全部标记为已读')
    fetchNotifications()
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

const handleClick = async (notification) => {
  if (!notification.read) {
    try {
      await request.post(`/notifications/${notification.id}/read`)
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
  if (notification.questionId) {
    router.push(`/questions/${notification.questionId}`)
  }
}

onMounted(() => {
  fetchNotifications()
})
</script>

<style scoped>
.notifications {
  padding: 24px 0;
  max-width: 800px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.header h2 {
  font-size: 20px;
}

.notification-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.3s;
}

.notification-item:hover {
  background: #f9f9f9;
}

.notification-item.unread {
  background: #f0f7ff;
}

.notification-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
}

.notification-text {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.notification-time {
  font-size: 12px;
  color: #999;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
