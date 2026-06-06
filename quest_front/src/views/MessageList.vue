<template>
  <div class="message-page">
    <div class="page-container">
      <div class="content-grid">
        <div class="main-panel">
          <div class="panel-header">
            <div class="header-left">
              <div class="header-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
              </div>
              <div>
                <h1 class="page-title">私信</h1>
                <p class="page-subtitle">与其他用户一对一沟通交流</p>
              </div>
            </div>
            <button class="btn-new-msg" @click="router.push('/messages/new')">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
              发起私信
            </button>
          </div>

          <div class="conversation-list" v-loading="loading">
            <div
              v-for="conversation in conversations"
              :key="conversation.userId"
              class="conversation-card"
              :class="{ unread: conversation.unreadCount > 0 }"
              @click="openConversation(conversation)"
            >
              <div class="conv-avatar-wrap">
                <div class="conv-avatar">
                  <img v-if="conversation.avatar" :src="conversation.avatar" :alt="conversation.nickname" />
                  <span v-else class="avatar-fallback">{{ conversation.nickname?.charAt(0) }}</span>
                </div>
                <span v-if="conversation.unreadCount > 0" class="unread-dot">
                  {{ conversation.unreadCount > 99 ? '99+' : conversation.unreadCount }}
                </span>
              </div>
              <div class="conv-body">
                <div class="conv-top">
                  <span class="conv-name">{{ conversation.nickname }}</span>
                  <span class="conv-time">{{ formatTime(conversation.lastMessageTime) }}</span>
                </div>
                <p class="conv-preview">{{ conversation.lastMessage || '暂无消息' }}</p>
              </div>
              <div class="conv-arrow">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
              </div>
            </div>
          </div>

          <div v-if="!loading && conversations.length === 0" class="empty-state">
            <div class="empty-icon">
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            </div>
            <p class="empty-title">暂无私信</p>
            <p class="empty-desc">发起一条私信，开始与其他用户交流吧</p>
            <button class="btn-new-msg" @click="router.push('/messages/new')">发起私信</button>
          </div>
        </div>

        <div class="side-panel">
          <div class="side-card">
            <h3 class="side-title">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
              私信说明
            </h3>
            <div class="tip-list">
              <div class="tip-item">
                <span class="tip-bullet"></span>
                <span>点击会话进入对话</span>
              </div>
              <div class="tip-item">
                <span class="tip-bullet"></span>
                <span>支持发送文本消息</span>
              </div>
              <div class="tip-item">
                <span class="tip-bullet"></span>
                <span>未读消息会有红点提示</span>
              </div>
              <div class="tip-item">
                <span class="tip-bullet"></span>
                <span>按 Ctrl + Enter 快捷发送</span>
              </div>
            </div>
          </div>

          <div class="side-card">
            <h3 class="side-title">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              安全提示
            </h3>
            <p class="side-desc">请注意保护个人隐私，不要在私信中透露敏感信息如密码、银行卡号等。</p>
          </div>
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
.message-page {
  min-height: 100vh;
  background: var(--cream);
  padding: 40px 0 80px;
}

.page-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 24px;
}

.content-grid {
  display: flex;
  gap: 32px;
}

.main-panel {
  flex: 1;
  min-width: 0;
}

.side-panel {
  width: 300px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: var(--forest);
  color: var(--caramel-light);
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-title {
  font-family: 'Playfair Display', 'Noto Serif SC', serif;
  font-size: 28px;
  color: var(--forest);
  margin: 0;
  line-height: 1.2;
}

.page-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 4px 0 0;
}

.btn-new-msg {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
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

.btn-new-msg:hover {
  background: var(--forest);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(26, 46, 38, 0.2);
}

.conversation-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(26, 46, 38, 0.06);
}

.conversation-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px 24px;
  cursor: pointer;
  transition: all 0.25s ease;
  border-bottom: 1px solid rgba(184, 138, 89, 0.08);
  position: relative;
}

.conversation-card:last-child {
  border-bottom: none;
}

.conversation-card:hover {
  background: var(--cream-dark);
}

.conversation-card.unread {
  background: rgba(184, 138, 89, 0.06);
}

.conversation-card.unread::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: var(--caramel);
  border-radius: 0 3px 3px 0;
}

.conv-avatar-wrap {
  position: relative;
  flex-shrink: 0;
}

.conv-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, var(--caramel-light), var(--caramel));
  display: flex;
  align-items: center;
  justify-content: center;
}

.conv-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-fallback {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  font-family: 'Playfair Display', serif;
}

.unread-dot {
  position: absolute;
  top: -2px;
  right: -2px;
  background: #e74c3c;
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  padding: 2px 7px;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
  border: 2px solid #fff;
}

.conv-body {
  flex: 1;
  min-width: 0;
}

.conv-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.conv-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--forest);
}

.conversation-card.unread .conv-name {
  color: var(--forest);
}

.conv-time {
  font-size: 12px;
  color: var(--text-muted);
  flex-shrink: 0;
}

.conv-preview {
  font-size: 14px;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin: 0;
}

.conversation-card.unread .conv-preview {
  color: var(--forest);
  font-weight: 500;
}

.conv-arrow {
  color: var(--text-muted);
  opacity: 0;
  transform: translateX(-4px);
  transition: all 0.25s ease;
  flex-shrink: 0;
}

.conversation-card:hover .conv-arrow {
  opacity: 1;
  transform: translateX(0);
}

.empty-state {
  text-align: center;
  padding: 80px 24px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 2px 12px rgba(26, 46, 38, 0.06);
}

.empty-icon {
  color: var(--caramel-light);
  opacity: 0.4;
  margin-bottom: 20px;
}

.empty-title {
  font-family: 'Playfair Display', 'Noto Serif SC', serif;
  font-size: 20px;
  color: var(--forest);
  margin: 0 0 8px;
}

.empty-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 24px;
}

.side-card {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(26, 46, 38, 0.06);
}

.side-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: 'Playfair Display', 'Noto Serif SC', serif;
  font-size: 17px;
  color: var(--forest);
  margin: 0 0 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(184, 138, 89, 0.12);
}

.side-title svg {
  color: var(--caramel);
  flex-shrink: 0;
}

.tip-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: var(--text-secondary);
}

.tip-bullet {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--caramel);
  flex-shrink: 0;
}

.side-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.7;
  margin: 0;
}

@media (max-width: 768px) {
  .content-grid {
    flex-direction: column;
  }

  .side-panel {
    width: 100%;
  }

  .panel-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .btn-new-msg {
    width: 100%;
    justify-content: center;
  }
}
</style>
