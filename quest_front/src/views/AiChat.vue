<template>
  <div class="ai-page">
    <section class="ai-hero">
      <div class="ai-hero-inner">
        <span class="section-eyebrow">AI Assistant</span>
        <h1 class="ai-title">AI 智能助手</h1>
        <p class="ai-desc">输入你的技术问题，AI 将为你提供精准解答</p>
      </div>
    </section>

    <section class="ai-body">
      <div class="ai-container">
        <div class="card-lux chat-card">
          <div class="chat-header">
            <div class="ch-left">
              <div class="ch-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/></svg>
              </div>
              <span class="ch-name">AI 助手</span>
            </div>
            <button class="ch-clear" @click="clearChat">清空对话</button>
          </div>

          <div class="messages" ref="messagesRef">
            <div v-for="(msg, index) in messages" :key="index" :class="['msg', msg.role]">
              <div class="msg-avatar" :class="msg.role">
                <span v-if="msg.role === 'user'">{{ (userStore.user?.nickname || 'U').charAt(0).toUpperCase() }}</span>
                <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/></svg>
              </div>
              <div class="msg-body">
                <div class="msg-text">{{ msg.content }}</div>
                <span class="msg-time">{{ formatTime(msg.createdAt) }}</span>
              </div>
            </div>

            <div v-if="loading" class="msg assistant">
              <div class="msg-avatar assistant">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/></svg>
              </div>
              <div class="msg-body">
                <div class="typing-dots"><span></span><span></span><span></span></div>
              </div>
            </div>
          </div>

          <div class="chat-input">
            <el-input v-model="inputMessage" type="textarea" :rows="3" placeholder="输入你的问题... (Ctrl + Enter 发送)" @keydown.enter.ctrl="sendMessage" />
            <div class="chat-footer">
              <span class="cf-tip">按 Ctrl + Enter 发送</span>
              <button class="btn-caramel" :disabled="loading" @click="sendMessage">
                {{ loading ? '思考中...' : '发送' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { chat } from '@/api/ai'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesRef = ref(null)

const formatTime = (t) => { if (!t) return ''; return new Date(t).toLocaleTimeString() }

const scrollToBottom = () => { nextTick(() => { if (messagesRef.value) messagesRef.value.scrollTop = messagesRef.value.scrollHeight }) }

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return
  messages.value.push({ role: 'user', content: inputMessage.value, createdAt: new Date() })
  inputMessage.value = ''
  scrollToBottom()
  loading.value = true
  try {
    const res = await chat({ message: messages.value[messages.value.length - 1].content, conversationId: null })
    messages.value.push({ role: 'assistant', content: res.data.content, createdAt: new Date() })
    scrollToBottom()
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const clearChat = () => { messages.value = [] }
</script>

<style scoped>
.ai-page { overflow: hidden; }
.ai-hero { background: var(--cream); padding: 100px 0 40px; text-align: center; border-bottom: 1px solid var(--border-light); }
.ai-hero-inner { max-width: 600px; margin: 0 auto; padding: 0 2rem; }
.section-eyebrow { display: block; font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.15em; color: var(--caramel); font-weight: 500; margin-bottom: 8px; }
.ai-title { font-family: 'Noto Serif SC', serif; font-size: 2rem; font-weight: 400; color: var(--forest); margin-bottom: 8px; }
.ai-desc { font-size: 0.9rem; color: var(--forest-60); font-weight: 300; }
.ai-body { padding: 40px 0 100px; }
.ai-container { max-width: 800px; margin: 0 auto; padding: 0 2rem; }
.card-lux { background: #fff; border: 1px solid var(--forest-08); border-radius: 20px; padding: 28px; }
.chat-card { display: flex; flex-direction: column; height: calc(100vh - 260px); min-height: 500px; }
.chat-header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 16px; border-bottom: 1px solid var(--forest-08); margin-bottom: 16px; }
.ch-left { display: flex; align-items: center; gap: 10px; }
.ch-icon { width: 36px; height: 36px; border-radius: 10px; background: var(--forest); display: flex; align-items: center; justify-content: center; }
.ch-icon svg { color: var(--caramel); }
.ch-name { font-family: 'Noto Serif SC', serif; font-size: 1rem; font-weight: 500; color: var(--forest); }
.ch-clear { background: none; border: none; font-size: 0.78rem; color: var(--forest-40); cursor: pointer; transition: color 0.2s; }
.ch-clear:hover { color: var(--caramel); }
.messages { flex: 1; overflow-y: auto; padding: 16px 0; }
.msg { display: flex; gap: 12px; margin-bottom: 20px; }
.msg.user { flex-direction: row-reverse; }
.msg-avatar { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; font-size: 0.8rem; font-weight: 600; }
.msg-avatar.user { background: var(--forest); color: var(--caramel); }
.msg-avatar.assistant { background: var(--caramel); color: #fff; }
.msg-body { max-width: 70%; }
.msg.user .msg-body { text-align: right; }
.msg-text { padding: 14px 18px; border-radius: 18px; font-size: 0.9rem; line-height: 1.7; word-break: break-word; }
.msg.user .msg-text { background: var(--forest); color: var(--cream); border-top-right-radius: 4px; }
.msg.assistant .msg-text { background: var(--cream-dark); color: var(--forest); border-top-left-radius: 4px; }
.msg-time { font-size: 0.7rem; color: var(--forest-30); margin-top: 4px; display: block; }
.typing-dots { display: flex; gap: 4px; padding: 14px 18px; background: var(--cream-dark); border-radius: 18px; border-top-left-radius: 4px; }
.typing-dots span { width: 8px; height: 8px; background: var(--forest-30); border-radius: 50%; animation: typing 1.4s infinite; }
.typing-dots span:nth-child(2) { animation-delay: 0.2s; }
.typing-dots span:nth-child(3) { animation-delay: 0.4s; }
@keyframes typing { 0%,100% { opacity: 0.4; transform: scale(0.8); } 50% { opacity: 1; transform: scale(1); } }
.chat-input { border-top: 1px solid var(--forest-08); padding-top: 16px; }
.chat-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; }
.cf-tip { font-size: 0.72rem; color: var(--forest-30); }
.btn-caramel { background: var(--caramel); color: #fff; border: none; padding: 10px 24px; border-radius: 999px; font-size: 0.8rem; font-weight: 500; cursor: pointer; transition: background 0.3s; }
.btn-caramel:hover { background: var(--caramel-light); }
.btn-caramel:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
