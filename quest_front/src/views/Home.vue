<template>
  <div class="home container">
    <div class="content-wrapper">
      <div class="main-content">
        <div class="welcome-banner card">
          <h1>欢迎来到 DevQuest</h1>
          <p>一个专为开发者打造的技术问答社区</p>
          <el-button type="primary" size="large" @click="router.push('/ask')">开始提问</el-button>
        </div>

        <div class="section">
          <div class="section-header">
            <h2>热门问题</h2>
            <el-button text @click="router.push('/questions')">查看全部</el-button>
          </div>
          <div v-loading="loading.hotQuestions">
            <div v-for="question in hotQuestions" :key="question.id" class="question-item card" @click="router.push(`/questions/${question.id}`)">
              <div class="question-stats">
                <div class="stat">
                  <span class="stat-value">{{ question.voteCount || 0 }}</span>
                  <span class="stat-label">投票</span>
                </div>
                <div class="stat">
                  <span class="stat-value">{{ question.answerCount || 0 }}</span>
                  <span class="stat-label">回答</span>
                </div>
                <div class="stat">
                  <span class="stat-value">{{ question.viewCount || 0 }}</span>
                  <span class="stat-label">浏览</span>
                </div>
              </div>
              <div class="question-content">
                <h3 class="question-title">{{ question.title }}</h3>
                <div class="question-meta">
                  <div class="tags">
                    <el-tag v-for="tag in question.tags" :key="tag.id" size="small">{{ tag.name }}</el-tag>
                  </div>
                  <span class="time">{{ formatTime(question.createdAt) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="sidebar">
        <div class="card sidebar-card">
          <h3>热门标签</h3>
          <div class="hot-tags" v-loading="loading.hotTags">
            <el-tag v-for="tag in hotTags" :key="tag.id" class="tag-item" @click="router.push(`/questions?tag=${tag.id}`)">
              {{ tag.name }}
            </el-tag>
          </div>
        </div>

        <div class="card sidebar-card">
          <h3>关于社区</h3>
          <p class="about-text">
            DevQuest 是一个面向开发者的技术问答平台，你可以在这里提问、回答问题，与其他开发者交流学习。
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHotQuestions } from '@/api/question'
import { getHotTags } from '@/api/tag'

const router = useRouter()

const hotQuestions = ref([])
const hotTags = ref([])
const loading = ref({
  hotQuestions: false,
  hotTags: false
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
  return date.toLocaleDateString()
}

const fetchHotQuestions = async () => {
  loading.value.hotQuestions = true
  try {
    const res = await getHotQuestions({ page: 1, size: 5 })
    hotQuestions.value = res.data.list || []
  } catch (error) {
    console.error('获取热门问题失败:', error)
  } finally {
    loading.value.hotQuestions = false
  }
}

const fetchHotTags = async () => {
  loading.value.hotTags = true
  try {
    const res = await getHotTags()
    hotTags.value = res.data || []
  } catch (error) {
    console.error('获取热门标签失败:', error)
  } finally {
    loading.value.hotTags = false
  }
}

onMounted(() => {
  fetchHotQuestions()
  fetchHotTags()
})
</script>

<style scoped>
.home {
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

.welcome-banner {
  background: linear-gradient(135deg, #1e80ff 0%, #36d399 100%);
  color: #fff;
  padding: 40px;
  margin-bottom: 24px;
}

.welcome-banner h1 {
  font-size: 28px;
  margin-bottom: 8px;
}

.welcome-banner p {
  font-size: 16px;
  margin-bottom: 20px;
  opacity: 0.9;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h2 {
  font-size: 20px;
  color: #333;
}

.question-item {
  display: flex;
  gap: 20px;
  cursor: pointer;
  transition: box-shadow 0.3s;
}

.question-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.question-stats {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 70px;
}

.stat {
  text-align: center;
  padding: 4px 8px;
  border-radius: 4px;
  background: #f5f5f5;
}

.stat-value {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.question-content {
  flex: 1;
  min-width: 0;
}

.question-title {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}

.question-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.time {
  font-size: 12px;
  color: #999;
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

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  cursor: pointer;
  transition: all 0.3s;
}

.tag-item:hover {
  background: #1e80ff;
  color: #fff;
}

.about-text {
  color: #666;
  line-height: 1.6;
}
</style>
