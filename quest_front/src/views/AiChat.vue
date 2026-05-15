<template>
  <div class="ai-chat container">
    <div class="chat-container card">
      <div class="chat-header">
        <h2>AI 助手</h2>
        <el-button text @click="clearChat">清空对话</el-button>
      </div>

      <div class="messages" ref="messagesRef">
        <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
          <div class="message-avatar">
            <el-avatar :size="36" v-if="msg.role === 'user'">{{ userStore.user?.nickname?.charAt(0) }}</el-avatar>
            <el-avatar :size="36" v-else style="background: #1e80ff">AI</el-avatar>
          </div>
          <div class="message-content">
            <div class="message-text">{{ msg.content }}</div>
            <div class="message-time">{{ formatTime(msg.createdAt) }}</div>
          </div>
        </div>
        <div v-if="loading" class="message assistant">
          <div class="message-avatar">
            <el-avatar :size="36" style="background: #1e80ff">AI</el-avatar>
          </div>
          <div class="message-content">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>

      <div class="input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="输入你的问题..."
          @keydown.enter.ctrl="sendMessage"
        />
        <div class="input-footer">
          <span class="tip">按 Ctrl + Enter 发送</span>
          <el-button type="primary" :loading="loading" @click="sendMessage">发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { chat } from '@/api/ai'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesRef = ref(null)

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString()
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return

  const userMessage = {
    role: 'user',
    content: inputMessage.value,
    createdAt: new Date()
  }
  messages.value.push(userMessage)
  inputMessage.value = ''
  scrollToBottom()

  loading.value = true
  try {
    const res = await chat({
      message: userMessage.content,
      conversationId: null
    })
    messages.value.push({
      role: 'assistant',
      content: res.data.content,
      createdAt: new Date()
    })
    scrollToBottom()
  } catch (error) {
    console.error('AI对话失败:', error)
  } finally {
    loading.value = false
  }
}

const clearChat = () => {
  messages.value = []
}
</script>

<style scoped>
.ai-chat {
  padding: 24px 0;
  max-width: 800px;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 150px);
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
  margin-bottom: 16px;
}

.chat-header h2 {
  font-size: 20px;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message.user {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 70%;
}

.message.user .message-content {
  text-align: right;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.message.user .message-text {
  background: #1e80ff;
  color: #fff;
  border-top-right-radius: 4px;
}

.message.assistant .message-text {
  background: #f5f5f5;
  color: #333;
  border-top-left-radius: 4px;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: #f5f5f5;
  border-radius: 12px;
  border-top-left-radius: 4px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #999;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 100% {
    opacity: 0.5;
    transform: scale(0.8);
  }
  50% {
    opacity: 1;
    transform: scale(1);
  }
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
