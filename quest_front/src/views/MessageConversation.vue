<template>
  <div class="message-conversation container">
    <div class="conversation-card card">
      <div class="conversation-header">
        <el-button text @click="router.push('/messages')">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
        <div class="user-info">
          <el-avatar :size="32" :src="targetUser.avatar">
            {{ targetUser.nickname?.charAt(0) }}
          </el-avatar>
          <span class="nickname">{{ targetUser.nickname }}</span>
        </div>
        <div></div>
      </div>

      <div class="messages-area" ref="messagesRef">
        <div v-if="loadingMore" class="loading-more">
          <el-icon class="is-loading"><Loading /></el-icon>
          加载中...
        </div>
        <div v-for="(msg, index) in messages" :key="msg.id" :class="['message-item', msg.senderId === userStore.user?.id ? 'self' : 'other']">
          <div v-if="shouldShowTime(index, msg)" class="time-divider">
            {{ formatDividerTime(msg.createdAt) }}
          </div>
          <div class="message-bubble">
            <el-avatar v-if="msg.senderId !== userStore.user?.id" :size="36" :src="targetUser.avatar">
              {{ targetUser.nickname?.charAt(0) }}
            </el-avatar>
            <div class="bubble-content">
              <div class="bubble-text">{{ msg.content }}</div>
              <div v-if="msg.senderId === userStore.user?.id" class="read-status">
                {{ msg.read ? '已读' : '未读' }}
              </div>
            </div>
            <el-avatar v-if="msg.senderId === userStore.user?.id" :size="36" :src="userStore.user?.avatar">
              {{ userStore.user?.nickname?.charAt(0) }}
            </el-avatar>
          </div>
        </div>
        <el-empty v-if="!loading && messages.length === 0" description="暂无消息，发送第一条吧" />
      </div>

      <div class="input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="输入消息..."
          @keydown.enter.ctrl.exact="sendMessage"
          @keydown.enter.meta.exact="sendMessage"
        />
        <div class="input-footer">
          <span class="tip">按 Ctrl + Enter 发送</span>
          <el-button type="primary" :loading="sending" @click="sendMessage">发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Loading } from '@element-plus/icons-vue'
import { getMessages, sendMessage as sendMessageApi, markAsRead, getUserInfo } from '@/api/message'
import { getUserInfo as fetchUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const targetUser = ref({})
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const loadingMore = ref(false)
const sending = ref(false)
const messagesRef = ref(null)
const page = ref(1)
const hasMore = ref(true)
let pollTimer = null

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleTimeString()
}

const formatDividerTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const isToday = date.toDateString() === now.toDateString()
  if (isToday) return date.toLocaleTimeString()
  return date.toLocaleString()
}

const shouldShowTime = (index, msg) => {
  if (index === 0) return true
  const prev = messages.value[index - 1]
  return new Date(msg.createdAt) - new Date(prev.createdAt) > 300000
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

const fetchTargetUser = async () => {
  try {
    const res = await fetchUserInfo(route.params.userId)
    targetUser.value = res.data
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const fetchMessages = async (isLoadMore = false) => {
  if (isLoadMore) {
    loadingMore.value = true
  } else {
    loading.value = true
  }
  try {
    const res = await getMessages(route.params.userId, { page: page.value, size: 20 })
    const list = res.data.list || []
    if (isLoadMore) {
      messages.value = [...list.reverse(), ...messages.value]
    } else {
      messages.value = list.reverse()
      scrollToBottom()
    }
    hasMore.value = list.length === 20
  } catch (error) {
    console.error('获取消息失败:', error)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || sending.value) return
  sending.value = true
  try {
    await sendMessageApi(route.params.userId, { content: inputMessage.value })
    inputMessage.value = ''
    await fetchMessages()
    scrollToBottom()
  } catch (error) {
    console.error('发送消息失败:', error)
  } finally {
    sending.value = false
  }
}

const markRead = async () => {
  try {
    await markAsRead(route.params.userId)
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

const startPolling = () => {
  pollTimer = setInterval(() => {
    fetchMessages()
  }, 10000)
}

onMounted(async () => {
  await fetchTargetUser()
  await fetchMessages()
  markRead()
  startPolling()
  if (messagesRef.value) {
    messagesRef.value.addEventListener('scroll', handleScroll)
  }
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
  if (messagesRef.value) {
    messagesRef.value.removeEventListener('scroll', handleScroll)
  }
})

const handleScroll = () => {
  if (messagesRef.value && messagesRef.value.scrollTop < 50 && hasMore.value && !loadingMore.value) {
    page.value++
    fetchMessages(true)
  }
}
</script>

<style scoped>
.message-conversation {
  padding: 24px 0;
  max-width: 800px;
}

.conversation-card {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 150px);
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nickname {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
}

.loading-more {
  text-align: center;
  padding: 12px;
  color: #999;
  font-size: 13px;
}

.time-divider {
  text-align: center;
  margin: 16px 0;
  font-size: 12px;
  color: #999;
}

.message-item {
  margin-bottom: 16px;
}

.message-bubble {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.message-item.self .message-bubble {
  flex-direction: row-reverse;
}

.bubble-content {
  max-width: 60%;
}

.message-item.self .bubble-content {
  text-align: right;
}

.bubble-text {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.message-item.other .bubble-text {
  background: #f5f5f5;
  color: #333;
  border-top-left-radius: 4px;
}

.message-item.self .bubble-text {
  background: #1e80ff;
  color: #fff;
  border-top-right-radius: 4px;
}

.read-status {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}

.input-area {
  border-top: 1px solid #eee;
  padding-top: 16px;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.tip {
  font-size: 12px;
  color: #999;
}
</style>
