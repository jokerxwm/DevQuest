<template>
  <div class="conversation-page">
    <div class="page-container">
      <div class="chat-card">
        <div class="chat-header">
          <button class="btn-back" @click="router.push('/messages')">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
            返回
          </button>
          <div class="chat-user">
            <div class="chat-avatar">
              <img v-if="targetUser.avatar" :src="targetUser.avatar" :alt="targetUser.nickname" />
              <span v-else class="avatar-fallback">{{ targetUser.nickname?.charAt(0) }}</span>
            </div>
            <div class="chat-user-info">
              <span class="chat-name">{{ targetUser.nickname || '用户' }}</span>
              <span class="chat-status">在线</span>
            </div>
          </div>
          <div class="chat-actions">
            <button class="action-btn" title="用户主页" @click="router.push(`/user/${route.params.userId}`)">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </button>
          </div>
        </div>

        <div class="messages-area" ref="messagesRef">
          <div v-if="loadingMore" class="load-more-indicator">
            <div class="spinner"></div>
            <span>加载历史消息...</span>
          </div>

          <template v-for="(msg, index) in messages" :key="msg.id">
            <div v-if="shouldShowTime(index, msg)" class="time-divider">
              <span class="time-label">{{ formatDividerTime(msg.createdAt) }}</span>
            </div>
            <div :class="['msg-row', msg.senderId === userStore.user?.id ? 'self' : 'other']">
              <div v-if="msg.senderId !== userStore.user?.id" class="msg-avatar">
                <div class="msg-avatar-circle">
                  <img v-if="targetUser.avatar" :src="targetUser.avatar" :alt="targetUser.nickname" />
                  <span v-else class="avatar-fallback-sm">{{ targetUser.nickname?.charAt(0) }}</span>
                </div>
              </div>
              <div class="msg-body">
                <div :class="['msg-bubble', msg.senderId === userStore.user?.id ? 'bubble-self' : 'bubble-other']">
                  {{ msg.content }}
                </div>
                <div v-if="msg.senderId === userStore.user?.id" class="msg-meta">
                  <span :class="['read-tag', msg.read ? 'is-read' : '']">{{ msg.read ? '已读' : '未读' }}</span>
                </div>
              </div>
              <div v-if="msg.senderId === userStore.user?.id" class="msg-avatar">
                <div class="msg-avatar-circle self-avatar">
                  <img v-if="userStore.user?.avatar" :src="userStore.user.avatar" :alt="userStore.user?.nickname" />
                  <span v-else class="avatar-fallback-sm">{{ userStore.user?.nickname?.charAt(0) }}</span>
                </div>
              </div>
            </div>
          </template>

          <div v-if="!loading && messages.length === 0" class="empty-chat">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            <p>暂无消息，发送第一条吧</p>
          </div>
        </div>

        <div class="input-bar">
          <div class="input-wrap">
            <textarea
              v-model="inputMessage"
              placeholder="输入消息... (Ctrl + Enter 发送)"
              rows="3"
              @keydown.enter.ctrl.exact.prevent="sendMessage"
              @keydown.enter.meta.exact.prevent="sendMessage"
            ></textarea>
          </div>
          <div class="input-footer">
            <span class="input-hint">按 Ctrl + Enter 发送</span>
            <button class="btn-send" :class="{ disabled: !inputMessage.trim() || sending }" @click="sendMessage">
              <span v-if="sending" class="send-spinner"></span>
              <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
              发送
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMessages, sendMessage as sendMessageApi, markAsRead, getUserInfo } from '@/api/message'
import { getUserInfo as fetchUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'

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

const formatDividerTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const isToday = date.toDateString() === now.toDateString()
  if (isToday) return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  return date.toLocaleString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
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

const handleScroll = () => {
  if (messagesRef.value && messagesRef.value.scrollTop < 50 && hasMore.value && !loadingMore.value) {
    page.value++
    fetchMessages(true)
  }
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
</script>

<style scoped>
.conversation-page {
  min-height: 100vh;
  background: var(--cream);
  padding: 40px 0 40px;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 24px;
}

.chat-card {
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 4px 24px rgba(26, 46, 38, 0.08);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 24px;
  border-bottom: 1px solid rgba(184, 138, 89, 0.1);
  background: linear-gradient(135deg, var(--forest), #2a4a3f);
  color: #fff;
}

.btn-back {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.12);
  border: none;
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.25s ease;
  font-family: 'Inter', 'Noto Sans SC', sans-serif;
}

.btn-back:hover {
  background: rgba(255, 255, 255, 0.22);
  color: #fff;
}

.chat-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, var(--caramel-light), var(--caramel));
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.chat-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-fallback {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  font-family: 'Playfair Display', serif;
}

.chat-user-info {
  display: flex;
  flex-direction: column;
}

.chat-name {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
}

.chat-status {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.chat-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.25s ease;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: var(--cream);
}

.messages-area::-webkit-scrollbar {
  width: 6px;
}

.messages-area::-webkit-scrollbar-track {
  background: transparent;
}

.messages-area::-webkit-scrollbar-thumb {
  background: rgba(184, 138, 89, 0.2);
  border-radius: 3px;
}

.load-more-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  color: var(--text-muted);
  font-size: 13px;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(184, 138, 89, 0.2);
  border-top-color: var(--caramel);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.time-divider {
  text-align: center;
  margin: 8px 0;
}

.time-label {
  display: inline-block;
  padding: 4px 14px;
  background: rgba(26, 46, 38, 0.06);
  border-radius: 50px;
  font-size: 12px;
  color: var(--text-muted);
}

.msg-row {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.msg-row.self {
  flex-direction: row-reverse;
}

.msg-avatar {
  flex-shrink: 0;
}

.msg-avatar-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, var(--caramel-light), var(--caramel));
  display: flex;
  align-items: center;
  justify-content: center;
}

.msg-avatar-circle.self-avatar {
  background: linear-gradient(135deg, var(--forest), #2a4a3f);
}

.msg-avatar-circle img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-fallback-sm {
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  font-family: 'Playfair Display', serif;
}

.msg-body {
  max-width: 65%;
  min-width: 0;
}

.msg-bubble {
  padding: 12px 16px;
  font-size: 14px;
  line-height: 1.65;
  word-break: break-word;
  position: relative;
}

.bubble-other {
  background: #fff;
  color: var(--forest);
  border-radius: 4px 18px 18px 18px;
  box-shadow: 0 1px 4px rgba(26, 46, 38, 0.06);
}

.bubble-self {
  background: linear-gradient(135deg, var(--forest), #2a4a3f);
  color: #fff;
  border-radius: 18px 4px 18px 18px;
}

.msg-meta {
  margin-top: 4px;
  text-align: right;
}

.read-tag {
  font-size: 11px;
  color: var(--text-muted);
}

.read-tag.is-read {
  color: var(--caramel);
}

.empty-chat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: var(--text-muted);
  opacity: 0.5;
}

.empty-chat p {
  font-size: 14px;
  margin: 0;
}

.input-bar {
  border-top: 1px solid rgba(184, 138, 89, 0.1);
  background: #fff;
  padding: 16px 24px;
}

.input-wrap textarea {
  width: 100%;
  border: 1.5px solid rgba(184, 138, 89, 0.2);
  border-radius: 14px;
  padding: 14px 16px;
  font-size: 14px;
  font-family: 'Inter', 'Noto Sans SC', sans-serif;
  color: var(--forest);
  background: var(--cream);
  resize: none;
  outline: none;
  transition: border-color 0.3s ease;
  line-height: 1.6;
}

.input-wrap textarea::placeholder {
  color: var(--text-muted);
}

.input-wrap textarea:focus {
  border-color: var(--caramel);
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.input-hint {
  font-size: 12px;
  color: var(--text-muted);
}

.btn-send {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  background: var(--caramel);
  color: #fff;
  border: none;
  border-radius: 50px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: 'Inter', 'Noto Sans SC', sans-serif;
}

.btn-send:hover {
  background: var(--forest);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(26, 46, 38, 0.2);
}

.btn-send.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.send-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@media (max-width: 768px) {
  .page-container {
    padding: 0 12px;
  }

  .chat-card {
    height: calc(100vh - 80px);
    border-radius: 18px;
  }

  .chat-header {
    padding: 14px 16px;
  }

  .messages-area {
    padding: 16px;
  }

  .input-bar {
    padding: 12px 16px;
  }

  .msg-body {
    max-width: 75%;
  }
}
</style>
