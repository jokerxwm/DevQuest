<template>
  <div class="message-list container">
    <div class="content-wrapper">
      <div class="main-content card">
        <div class="page-header">
          <h2>私信</h2>
          <el-button type="primary" size="small" @click="router.push('/messages/new')">发起私信</el-button>
        </div>

        <div v-loading="loading">
          <div
            v-for="conversation in conversations"
            :key="conversation.userId"
            class="conversation-item"
            :class="{ unread: conversation.unreadCount > 0 }"
            @click="openConversation(conversation)"
          >
            <div class="conversation-avatar">
              <el-avatar :size="48" :src="conversation.avatar">
                {{ conversation.nickname?.charAt(0) }}
              </el-avatar>
              <span v-if="conversation.unreadCount > 0" class="unread-badge">
                {{ conversation.unreadCount > 99 ? '99+' : conversation.unreadCount }}
              </span>
            </div>
            <div class="conversation-content">
              <div class="conversation-header">
                <span class="nickname">{{ conversation.nickname }}</span>
                <span class="time">{{ formatTime(conversation.lastMessageTime) }}</span>
              </div>
              <p class="last-message">{{ conversation.lastMessage }}</p>
            </div>
          </div>
        </div>

        <el-empty v-if="!loading && conversations.length === 0" description="暂无私信">
          <el-button type="primary" @click="router.push('/messages/new')">发起私信</el-button>
        </el-empty>
      </div>

      <div class="sidebar">
        <div class="card sidebar-card">
          <h3>私信说明</h3>
          <p class="tip-text">私信功能用于与其他用户进行一对一沟通交流。</p>
          <ul class="tip-list">
            <li>点击会话进入对话</li>
            <li>支持发送文本消息</li>
            <li>未读消息会有红点提示</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getConversations } from '@/api/message'

const router = useRouter()

const conversations = ref([])
const loading = ref(false)

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

const fetchConversations = async () => {
  loading.value = true
  try {
    const res = await getConversations({ page: 1, size: 50 })
    conversations.value = res.data.list || []
  } catch (error) {
    console.error('获取会话列表失败:', error)
  } finally {
    loading.value = false
  }
}

const openConversation = (conversation) => {
  router.push(`/messages/${conversation.userId}`)
}

onMounted(() => {
  fetchConversations()
})
</script>

<style scoped>
.message-list {
  padding: 24px 0;
}

.content-wrapper {
  display: flex;
  gap: 24px;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.sidebar {
  width: 300px;
  flex-shrink: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.page-header h2 {
  font-size: 24px;
}

.conversation-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  cursor: pointer;
  transition: background 0.3s;
  border-bottom: 1px solid #f5f5f5;
}

.conversation-item:hover {
  background: #f9f9f9;
}

.conversation-item.unread {
  background: #f0f7ff;
}

.conversation-avatar {
  position: relative;
  flex-shrink: 0;
}

.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #f56c6c;
  color: #fff;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.conversation-content {
  flex: 1;
  min-width: 0;
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.nickname {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.time {
  font-size: 12px;
  color: #999;
}

.last-message {
  font-size: 14px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sidebar-card h3 {
  font-size: 16px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.tip-text {
  color: #666;
  font-size: 14px;
  margin-bottom: 12px;
  line-height: 1.6;
}

.tip-list {
  padding-left: 16px;
  color: #666;
  font-size: 14px;
  line-height: 2;
}
</style>
