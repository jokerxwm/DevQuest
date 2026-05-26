<template>
  <div class="qd-page">
    <section class="qd-hero">
      <div class="qd-hero-inner">
        <div class="qd-breadcrumb">
          <router-link to="/">首页</router-link>
          <span class="bc-sep">/</span>
          <router-link to="/questions">问答</router-link>
          <span class="bc-sep">/</span>
          <span class="bc-current">问题详情</span>
        </div>
      </div>
    </section>

    <section class="qd-body">
      <div class="qd-container">
        <div class="qd-grid">
          <div class="qd-main">
            <div v-loading="loading" class="card-lux">
              <div class="qd-top-bar">
                <span class="qd-status" :class="question.status === 1 ? 'solved' : 'open'">
                  {{ question.status === 1 ? '已解决' : '待解答' }}
                </span>
                <div class="qd-actions" v-if="userStore.isLoggedIn()">
                  <button v-if="isAuthor" class="act-btn" @click="router.push(`/questions/${question.id}/edit`)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                    编辑
                  </button>
                  <button class="act-btn" :class="{ active: isFavorited }" @click="handleFavorite">
                    <svg width="14" height="14" viewBox="0 0 24 24" :fill="isFavorited ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                    {{ isFavorited ? '已收藏' : '收藏' }}
                  </button>
                  <button class="act-btn" @click="showShare = true">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><path d="M8.59 13.51l6.83 3.98M15.41 6.51l-6.82 3.98"/></svg>
                    分享
                  </button>
                </div>
              </div>

              <h1 class="qd-title">{{ question.title }}</h1>

              <div class="qd-author-bar">
                <div class="qd-avatar">
                  <span>{{ (question.authorName || 'U').charAt(0).toUpperCase() }}</span>
                </div>
                <div class="qd-author-info">
                  <span class="qd-author-name">{{ question.authorName }}</span>
                  <span class="qd-time">{{ formatTime(question.createdAt) }}</span>
                </div>
                <div class="qd-stats">
                  <span class="qd-stat">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                    {{ question.viewCount || 0 }}
                  </span>
                  <span class="qd-stat">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                    {{ answers.length }}
                  </span>
                </div>
              </div>

              <div class="qd-content-area">
                <div class="qd-vote">
                  <button class="vote-btn" :class="{ on: userVote === 1 }" @click="handleVote('question', question.id, 1)">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 19V5M5 12l7-7 7 7"/></svg>
                  </button>
                  <span class="vote-num">{{ question.voteCount || 0 }}</span>
                  <button class="vote-btn" :class="{ on: userVote === -1 }" @click="handleVote('question', question.id, -1)">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12l7 7 7-7"/></svg>
                  </button>
                </div>
                <div class="qd-content">
                  <div class="qd-text">{{ question.content }}</div>
                  <div class="qd-tags" v-if="question.tags && question.tags.length > 0">
                    <span v-for="tag in question.tags" :key="tag.id" class="qd-tag">{{ tag.name }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="answers-section">
              <div class="ans-header">
                <span class="section-eyebrow">Answers</span>
                <h2 class="section-title">{{ answers.length }} 个回答</h2>
              </div>
              <div v-for="answer in answers" :key="answer.id" class="card-lux ans-card" :class="{ accepted: answer.accepted }">
                <div class="accepted-badge" v-if="answer.accepted">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6L9 17l-5-5"/></svg>
                  已采纳
                </div>
                <div class="ans-body">
                  <div class="qd-vote">
                    <button class="vote-btn" :class="{ on: answer.userVote === 1 }" @click="handleVote('answer', answer.id, 1)">
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 19V5M5 12l7-7 7 7"/></svg>
                    </button>
                    <span class="vote-num">{{ answer.voteCount || 0 }}</span>
                    <button class="vote-btn" :class="{ on: answer.userVote === -1 }" @click="handleVote('answer', answer.id, -1)">
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12l7 7 7-7"/></svg>
                    </button>
                    <button v-if="isAuthor && !answer.accepted" class="vote-btn accept-btn" @click="handleAccept(answer.id)" title="采纳此回答">
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6L9 17l-5-5"/></svg>
                    </button>
                  </div>
                  <div class="qd-content">
                    <div class="qd-text">{{ answer.content }}</div>
                    <div class="ans-meta">
                      <div class="ans-author">
                        <div class="qd-avatar small">
                          <span>{{ (answer.authorName || 'U').charAt(0).toUpperCase() }}</span>
                        </div>
                        <span class="qd-author-name">{{ answer.authorName }}</span>
                      </div>
                      <span class="qd-time">{{ formatTime(answer.createdAt) }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="!loading && answers.length === 0" class="card-lux empty-ans">
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="empty-icon"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                <p>还没有回答，来分享你的见解吧</p>
              </div>
            </div>

            <div v-if="userStore.isLoggedIn()" class="card-lux ans-form">
              <div class="ans-form-header">
                <span class="section-eyebrow">Your Answer</span>
                <h2 class="section-title">撰写回答</h2>
              </div>
              <textarea
                v-model="answerContent"
                class="ans-textarea"
                rows="6"
                placeholder="分享你的知识和经验..."
              ></textarea>
              <div class="ans-form-actions">
                <button class="btn-primary" :disabled="submitting" @click="submitAnswer">
                  {{ submitting ? '提交中...' : '提交回答' }}
                </button>
                <button class="btn-outline sm" @click="router.push(`/questions/${route.params.id}/answer`)">
                  全屏编辑
                </button>
              </div>
            </div>
          </div>

          <aside class="qd-side">
            <div class="card-lux side-card">
              <h3 class="side-title">问题信息</h3>
              <div class="side-divider"></div>
              <div class="info-row">
                <span class="info-label">提问时间</span>
                <span class="info-val">{{ formatTime(question.createdAt) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">最后修改</span>
                <span class="info-val">{{ formatTime(question.updatedAt) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">浏览次数</span>
                <span class="info-val accent">{{ question.viewCount || 0 }}</span>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </section>

    <ShareCard v-model="showShare" :title="question.title" :description="question.content" :question-id="question.id" />
    <ReportDialog v-model="showReport" target-type="question" :target-id="question.id" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getQuestionDetail } from '@/api/question'
import ShareCard from '@/components/ShareCard.vue'
import ReportDialog from '@/components/ReportDialog.vue'
import { getAnswers, createAnswer, acceptAnswer } from '@/api/answer'
import { vote, getUserVote } from '@/api/vote'
import { addFavorite, removeFavorite, isFavorited as checkFavorited } from '@/api/favorite'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const question = ref({})
const answers = ref([])
const loading = ref(false)
const submitting = ref(false)
const answerContent = ref('')
const userVote = ref(0)
const isFavorited = ref(false)
const showShare = ref(false)
const showReport = ref(false)

const isAuthor = computed(() => {
  return userStore.user?.id === question.value.authorId
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 2592000000) return `${Math.floor(diff / 86400000)}天前`
  return date.toLocaleString()
}

const fetchQuestion = async () => {
  loading.value = true
  try {
    const res = await getQuestionDetail(route.params.id)
    question.value = res.data
    if (userStore.isLoggedIn()) {
      fetchUserVote('question', question.value.id)
      checkFavoriteStatus()
    }
  } catch (error) {
    console.error('获取问题详情失败:', error)
  } finally {
    loading.value = false
  }
}

const checkFavoriteStatus = async () => {
  try {
    const res = await checkFavorited(route.params.id)
    isFavorited.value = res.data?.isFavorited || false
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

const handleFavorite = async () => {
  try {
    if (isFavorited.value) {
      await removeFavorite(route.params.id)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(route.params.id)
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
  }
}

const fetchAnswers = async () => {
  try {
    const res = await getAnswers(route.params.id, { page: 1, size: 50 })
    answers.value = res.data.list || []
  } catch (error) {
    console.error('获取回答列表失败:', error)
  }
}

const targetTypeMap = { question: 1, answer: 2 }

const fetchUserVote = async (targetType, targetId) => {
  try {
    const res = await getUserVote(targetId, targetTypeMap[targetType] || 1)
    userVote.value = res.data?.vote || 0
  } catch (error) {
    console.error('获取用户投票失败:', error)
  }
}

const handleVote = async (targetType, targetId, value) => {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }
  try {
    await vote({ targetType: targetTypeMap[targetType] || 1, targetId, voteType: value })
    if (targetType === 'question') {
      question.value.voteCount = (question.value.voteCount || 0) + value
      userVote.value = value
    } else {
      const answer = answers.value.find(a => a.id === targetId)
      if (answer) {
        answer.voteCount = (answer.voteCount || 0) + value
        answer.userVote = value
      }
    }
  } catch (error) {
    console.error('投票失败:', error)
  }
}

const handleAccept = async (answerId) => {
  try {
    await acceptAnswer(route.params.id, answerId)
    ElMessage.success('已采纳回答')
    fetchAnswers()
  } catch (error) {
    console.error('采纳回答失败:', error)
  }
}

const submitAnswer = async () => {
  if (!answerContent.value.trim()) {
    ElMessage.warning('请输入回答内容')
    return
  }
  submitting.value = true
  try {
    await createAnswer(route.params.id, { content: answerContent.value })
    ElMessage.success('回答提交成功')
    answerContent.value = ''
    fetchAnswers()
  } catch (error) {
    console.error('提交回答失败:', error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchQuestion()
  fetchAnswers()
})
</script>

<style scoped>
.qd-page {
  overflow: hidden;
}

.qd-hero {
  background: var(--forest);
  padding: 100px 0 32px;
}

.qd-hero-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.qd-breadcrumb {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.8rem;
}

.qd-breadcrumb a {
  color: var(--cream-60);
  text-decoration: none;
  transition: color 0.2s;
}

.qd-breadcrumb a:hover {
  color: var(--caramel);
}

.bc-sep {
  color: var(--cream-30);
}

.bc-current {
  color: var(--cream);
  font-weight: 500;
}

.qd-body {
  padding: 48px 0 100px;
}

.qd-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.qd-grid {
  display: flex;
  gap: 36px;
}

.qd-main {
  flex: 1;
  min-width: 0;
}

.qd-side {
  width: 300px;
  flex-shrink: 0;
}

.card-lux {
  background: #fff;
  border: 1px solid var(--forest-08);
  border-radius: 20px;
  padding: 32px;
}

.qd-top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.qd-status {
  display: inline-flex;
  align-items: center;
  padding: 5px 16px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 500;
  letter-spacing: 0.05em;
}

.qd-status.open {
  background: rgba(184, 138, 89, 0.1);
  color: var(--caramel);
}

.qd-status.solved {
  background: rgba(46, 204, 113, 0.1);
  color: #2ecc71;
}

.qd-actions {
  display: flex;
  gap: 4px;
}

.act-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 6px 14px;
  border: none;
  background: var(--cream);
  border-radius: 999px;
  font-size: 0.75rem;
  color: var(--forest-60);
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.act-btn:hover, .act-btn.active {
  background: var(--caramel);
  color: #fff;
}

.qd-title {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1.6rem;
  font-weight: 500;
  color: var(--forest);
  line-height: 1.35;
  margin-bottom: 24px;
  letter-spacing: -0.01em;
}

.qd-author-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 24px;
  margin-bottom: 28px;
  border-bottom: 1px solid var(--forest-08);
}

.qd-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--forest);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--caramel);
  flex-shrink: 0;
}

.qd-avatar.small {
  width: 28px;
  height: 28px;
  font-size: 0.7rem;
}

.qd-author-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.qd-author-name {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--forest);
}

.qd-time {
  font-size: 0.75rem;
  color: var(--forest-40);
}

.qd-stats {
  display: flex;
  gap: 14px;
}

.qd-stat {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.8rem;
  color: var(--forest-40);
}

.qd-content-area {
  display: flex;
  gap: 24px;
}

.qd-vote {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  min-width: 40px;
}

.vote-btn {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  border: 1.5px solid var(--forest-10);
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--forest-40);
}

.vote-btn:hover, .vote-btn.on {
  border-color: var(--caramel);
  color: var(--caramel);
  background: rgba(184, 138, 89, 0.06);
}

.accept-btn:hover {
  border-color: #2ecc71;
  color: #2ecc71;
  background: rgba(46, 204, 113, 0.06);
}

.vote-num {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1.15rem;
  font-weight: 500;
  color: var(--forest);
}

.qd-content {
  flex: 1;
  min-width: 0;
}

.qd-text {
  font-size: 0.95rem;
  line-height: 1.9;
  color: var(--forest-80);
  white-space: pre-wrap;
  margin-bottom: 20px;
}

.qd-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.qd-tag {
  display: inline-block;
  padding: 4px 14px;
  background: var(--cream-dark);
  border-radius: 999px;
  font-size: 0.75rem;
  color: var(--forest-80);
  font-weight: 500;
  transition: all 0.2s;
  cursor: pointer;
}

.qd-tag:hover {
  background: var(--caramel);
  color: #fff;
}

.answers-section {
  margin-top: 48px;
}

.ans-header {
  margin-bottom: 24px;
}

.section-eyebrow {
  display: block;
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 0.15em;
  color: var(--caramel);
  font-weight: 500;
  margin-bottom: 6px;
}

.section-title {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1.5rem;
  font-weight: 400;
  color: var(--forest);
}

.ans-card {
  margin-bottom: 16px;
  position: relative;
  transition: all 0.3s;
}

.ans-card:hover {
  box-shadow: 0 12px 40px rgba(26, 46, 38, 0.06);
}

.ans-card.accepted {
  border-color: #2ecc71;
  border-width: 1px;
  border-left-width: 4px;
}

.accepted-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  background: rgba(46, 204, 113, 0.08);
  color: #2ecc71;
  font-size: 0.75rem;
  font-weight: 600;
  padding: 5px 14px;
  border-radius: 999px;
  margin-bottom: 16px;
}

.ans-body {
  display: flex;
  gap: 24px;
}

.ans-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--forest-08);
}

.ans-author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.empty-ans {
  text-align: center;
  padding: 60px 32px;
}

.empty-icon {
  color: var(--forest-20);
  margin-bottom: 14px;
}

.empty-ans p {
  font-size: 0.9rem;
  color: var(--forest-40);
}

.ans-form {
  margin-top: 48px;
}

.ans-form-header {
  margin-bottom: 20px;
}

.ans-textarea {
  width: 100%;
  border: 1.5px solid var(--forest-10);
  border-radius: 16px;
  padding: 16px 20px;
  font-size: 0.9rem;
  line-height: 1.8;
  color: var(--forest);
  background: var(--cream);
  resize: vertical;
  font-family: inherit;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.ans-textarea:focus {
  outline: none;
  border-color: var(--caramel);
}

.ans-textarea::placeholder {
  color: var(--forest-30);
}

.ans-form-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: var(--caramel);
  color: #fff;
  border: none;
  padding: 12px 28px;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 500;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-primary:hover {
  background: var(--caramel-light);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-outline.sm {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: transparent;
  color: var(--forest);
  border: 1.5px solid var(--forest-20);
  padding: 12px 24px;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-outline.sm:hover {
  border-color: var(--caramel);
  color: var(--caramel);
}

.side-card {
  position: sticky;
  top: 100px;
}

.side-title {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1.1rem;
  font-weight: 500;
  color: var(--forest);
}

.side-divider {
  width: 32px;
  height: 1.5px;
  background: var(--caramel);
  margin: 12px 0 18px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid var(--forest-08);
  font-size: 0.85rem;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  color: var(--forest-50);
}

.info-val {
  color: var(--forest);
  font-weight: 500;
}

.info-val.accent {
  color: var(--caramel);
  font-weight: 600;
}

@media (max-width: 1024px) {
  .qd-grid { flex-direction: column; }
  .qd-side { width: 100%; }
  .side-card { position: static; }
}

@media (max-width: 640px) {
  .qd-content-area { flex-direction: column; }
  .qd-vote { flex-direction: row; }
  .ans-body { flex-direction: column; }
  .ans-form-actions { flex-direction: column; }
  .qd-top-bar { flex-direction: column; gap: 12px; align-items: flex-start; }
  .qd-author-bar { flex-wrap: wrap; }
}
</style>
