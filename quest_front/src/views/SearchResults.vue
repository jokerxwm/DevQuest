<template>
  <div class="search-results container">
    <div class="content-wrapper">
      <div class="main-content">
        <div class="search-header card">
          <h2>搜索结果</h2>
          <p class="search-info">
            共找到 <strong>{{ total }}</strong> 个与 "<strong>{{ keyword }}</strong>" 相关的问题
          </p>
        </div>

        <div class="search-tabs">
          <el-radio-group v-model="searchType" @change="handleSearchTypeChange">
            <el-radio-button label="keyword">关键词搜索</el-radio-button>
            <el-radio-button label="semantic">语义搜索</el-radio-button>
          </el-radio-group>
        </div>

        <div v-loading="loading">
          <div v-for="question in questions" :key="question.id" class="question-item card" @click="router.push(`/questions/${question.id}`)">
            <div class="question-stats">
              <div class="stat" :class="{ 'stat-active': question.answerCount > 0 }">
                <span class="stat-value">{{ question.answerCount || 0 }}</span>
                <span class="stat-label">回答</span>
              </div>
              <div class="stat">
                <span class="stat-value">{{ question.voteCount || 0 }}</span>
                <span class="stat-label">投票</span>
              </div>
              <div class="stat">
                <span class="stat-value">{{ question.viewCount || 0 }}</span>
                <span class="stat-label">浏览</span>
              </div>
            </div>
            <div class="question-content">
              <h3 class="question-title" v-html="highlightKeyword(question.title)"></h3>
              <p class="question-summary" v-html="highlightKeyword(question.content?.substring(0, 200))"></p>
              <div class="question-meta">
                <div class="tags">
                  <el-tag v-for="tag in question.tags" :key="tag.id" size="small">{{ tag.name }}</el-tag>
                </div>
                <div class="author">
                  <el-avatar :size="20">{{ question.authorName?.charAt(0) }}</el-avatar>
                  <span>{{ question.authorName }}</span>
                  <span class="time">{{ formatTime(question.createdAt) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!loading && questions.length === 0" class="empty-state card">
          <el-empty description="未找到相关问题">
            <el-button type="primary" @click="router.push('/ask')">去提问</el-button>
          </el-empty>
        </div>

        <div v-if="total > 0" class="pagination">
          <el-pagination
            v-model:current-page="page"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchResults"
          />
        </div>
      </div>

      <div class="sidebar">
        <div class="card sidebar-card">
          <el-button type="primary" style="width: 100%" @click="router.push('/ask')">我要提问</el-button>
        </div>

        <div class="card sidebar-card">
          <h3>搜索提示</h3>
          <ul class="tips-list">
            <li>关键词搜索：匹配标题和内容中的关键词</li>
            <li>语义搜索：理解你的意图，找到含义相近的问题</li>
            <li>使用更具体的关键词可以获得更精确的结果</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchQuestions, searchSemantic } from '@/api/search'

const route = useRoute()
const router = useRouter()

const questions = ref([])
const loading = ref(false)
const keyword = ref('')
const searchType = ref('keyword')
const page = ref(1)
const pageSize = ref(10)
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

const highlightKeyword = (text) => {
  if (!text || !keyword.value) return text
  const regex = new RegExp(`(${keyword.value})`, 'gi')
  return text.replace(regex, '<span class="highlight">$1</span>')
}

const fetchResults = async () => {
  if (!keyword.value) return
  loading.value = true
  try {
    const params = {
      keyword: keyword.value,
      page: page.value,
      size: pageSize.value
    }
    const res = searchType.value === 'semantic'
      ? await searchSemantic(params)
      : await searchQuestions(params)
    questions.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('搜索失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearchTypeChange = () => {
  page.value = 1
  fetchResults()
}

watch(() => route.query.q, (newKeyword) => {
  if (newKeyword) {
    keyword.value = newKeyword
    page.value = 1
    fetchResults()
  }
}, { immediate: true })

onMounted(() => {
  if (route.query.q) {
    keyword.value = route.query.q
    fetchResults()
  }
})
</script>

<style scoped>
.search-results {
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

.search-header {
  margin-bottom: 16px;
}

.search-header h2 {
  font-size: 24px;
  margin-bottom: 8px;
}

.search-info {
  color: #666;
}

.search-tabs {
  margin-bottom: 16px;
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

.stat-active {
  background: #e6f7ff;
  border: 1px solid #91d5ff;
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

.question-title :deep(.highlight) {
  color: #1e80ff;
  background: #e6f7ff;
  padding: 0 2px;
  border-radius: 2px;
}

.question-summary {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  line-height: 1.5;
}

.question-summary :deep(.highlight) {
  color: #1e80ff;
  background: #e6f7ff;
  padding: 0 2px;
  border-radius: 2px;
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

.author {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
}

.time {
  color: #999;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
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

.tips-list {
  padding-left: 16px;
}

.tips-list li {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  line-height: 1.5;
}

.empty-state {
  padding: 40px;
}
</style>
