<template>
  <div class="question-detail container">
    <div class="content-wrapper">
      <div class="main-content">
        <div v-loading="loading" class="question-card card">
          <h1 class="question-title">{{ question.title }}</h1>
          <div class="question-meta">
            <span>提问者：{{ question.authorName }}</span>
            <span>发布时间：{{ formatTime(question.createdAt) }}</span>
            <span>浏览：{{ question.viewCount || 0 }}次</span>
          </div>
          <div class="question-body">
            <div class="vote-section">
              <el-button :icon="ArrowUp" circle @click="handleVote('question', question.id, 1)" :type="userVote === 1 ? 'primary' : ''" />
              <span class="vote-count">{{ question.voteCount || 0 }}</span>
              <el-button :icon="ArrowDown" circle @click="handleVote('question', question.id, -1)" :type="userVote === -1 ? 'danger' : ''" />
            </div>
            <div class="content-section">
              <div class="content-text">{{ question.content }}</div>
              <div class="tags">
                <el-tag v-for="tag in question.tags" :key="tag.id">{{ tag.name }}</el-tag>
              </div>
            </div>
          </div>
        </div>

        <div class="answers-section">
          <h3>{{ answers.length }}个回答</h3>
          <div v-for="answer in answers" :key="answer.id" class="answer-card card" :class="{ 'accepted-answer': answer.accepted }">
            <div class="answer-body">
              <div class="vote-section">
                <el-button :icon="ArrowUp" circle @click="handleVote('answer', answer.id, 1)" :type="answer.userVote === 1 ? 'primary' : ''" />
                <span class="vote-count">{{ answer.voteCount || 0 }}</span>
                <el-button :icon="ArrowDown" circle @click="handleVote('answer', answer.id, -1)" :type="answer.userVote === -1 ? 'danger' : ''" />
                <el-button v-if="isAuthor && !answer.accepted" :icon="Check" circle @click="handleAccept(answer.id)" title="采纳此回答" />
                <el-icon v-if="answer.accepted" class="accepted-icon" color="#67c23a" :size="24"><Check /></el-icon>
              </div>
              <div class="content-section">
                <div class="content-text">{{ answer.content }}</div>
                <div class="answer-meta">
                  <span>回答者：{{ answer.authorName }}</span>
                  <span>{{ formatTime(answer.createdAt) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="userStore.isLoggedIn()" class="answer-form card">
          <h3>撰写回答</h3>
          <el-input v-model="answerContent" type="textarea" :rows="6" placeholder="请输入你的回答..." />
          <el-button type="primary" :loading="submitting" @click="submitAnswer" style="margin-top: 16px">提交回答</el-button>
        </div>
      </div>

      <div class="sidebar">
        <div class="card sidebar-card">
          <h3>问题信息</h3>
          <div class="info-item">
            <span class="label">提问时间</span>
            <span class="value">{{ formatTime(question.createdAt) }}</span>
          </div>
          <div class="info-item">
            <span class="label">最后修改</span>
            <span class="value">{{ formatTime(question.updatedAt) }}</span>
          </div>
          <div class="info-item">
            <span class="label">浏览次数</span>
            <span class="value">{{ question.viewCount || 0 }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowUp, ArrowDown, Check } from '@element-plus/icons-vue'
import { getQuestionDetail } from '@/api/question'
import { getAnswers, createAnswer, acceptAnswer } from '@/api/answer'
import { vote, getUserVote } from '@/api/vote'
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

const isAuthor = computed(() => {
  return userStore.user?.id === question.value.authorId
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString()
}

const fetchQuestion = async () => {
  loading.value = true
  try {
    const res = await getQuestionDetail(route.params.id)
    question.value = res.data
    if (userStore.isLoggedIn()) {
      fetchUserVote('question', question.value.id)
    }
  } catch (error) {
    console.error('获取问题详情失败:', error)
  } finally {
    loading.value = false
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

const fetchUserVote = async (targetType, targetId) => {
  try {
    const res = await getUserVote(targetId, targetType)
    userVote.value = res.data?.value || 0
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
    await vote({ targetType, targetId, value })
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
.question-detail {
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

.question-title {
  font-size: 24px;
  margin-bottom: 16px;
  color: #333;
}

.question-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.question-body,
.answer-body {
  display: flex;
  gap: 20px;
}

.vote-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  min-width: 40px;
}

.vote-count {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.content-section {
  flex: 1;
  min-width: 0;
}

.content-text {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
  margin-bottom: 16px;
  white-space: pre-wrap;
}

.tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.answers-section {
  margin-top: 24px;
}

.answers-section h3 {
  font-size: 18px;
  margin-bottom: 16px;
}

.answer-card {
  transition: box-shadow 0.3s;
}

.accepted-answer {
  border-left: 4px solid #67c23a;
}

.accepted-icon {
  margin-top: 8px;
}

.answer-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.answer-form {
  margin-top: 24px;
}

.answer-form h3 {
  font-size: 18px;
  margin-bottom: 16px;
}

.sidebar-card {
  margin-bottom: 16px;
}

.sidebar-card h3 {
  font-size: 16px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
}

.label {
  color: #999;
}

.value {
  color: #333;
}
</style>
