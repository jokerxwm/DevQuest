<template>
  <div class="tag-questions container">
    <div class="content-wrapper">
      <div class="main-content">
        <div class="page-header card">
          <div class="tag-info">
            <el-tag size="large" effect="plain">{{ tagInfo.name }}</el-tag>
            <span class="question-count">{{ total }} 个问题</span>
          </div>
          <p v-if="tagInfo.description" class="tag-description">{{ tagInfo.description }}</p>
        </div>

        <div class="list-header">
          <div class="filters">
            <el-select v-model="sort" placeholder="排序方式" @change="fetchQuestions">
              <el-option label="最新" value="newest" />
              <el-option label="最热" value="hot" />
              <el-option label="未回答" value="unanswered" />
            </el-select>
          </div>
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
              <h3 class="question-title">{{ question.title }}</h3>
              <p class="question-summary">{{ question.content?.substring(0, 150) }}...</p>
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
          <el-empty description="该标签下暂无问题" />
        </div>

        <div v-if="total > 0" class="pagination">
          <el-pagination
            v-model:current-page="page"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchQuestions"
          />
        </div>
      </div>

      <div class="sidebar">
        <div class="card sidebar-card">
          <el-button type="primary" style="width: 100%" @click="router.push('/ask')">我要提问</el-button>
        </div>

        <div class="card sidebar-card">
          <h3>相关标签</h3>
          <div class="hot-tags">
            <el-tag v-for="tag in relatedTags" :key="tag.id" class="tag-item" @click="router.push(`/tags/${tag.id}`)">
              {{ tag.name }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getQuestionList } from '@/api/question'
import { getTags } from '@/api/tag'

const route = useRoute()
const router = useRouter()

const tagInfo = ref({})
const questions = ref([])
const relatedTags = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const sort = ref('newest')

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

const fetchTagInfo = async () => {
  try {
    const res = await getTags({ page: 1, size: 100 })
    const tags = res.data.list || []
    tagInfo.value = tags.find(t => t.id == route.params.id) || { name: '未知标签' }
    relatedTags.value = tags.filter(t => t.id != route.params.id).slice(0, 10)
  } catch (error) {
    console.error('获取标签信息失败:', error)
  }
}

const fetchQuestions = async () => {
  loading.value = true
  try {
    const res = await getQuestionList({
      page: page.value,
      size: pageSize.value,
      sort: sort.value,
      tagId: route.params.id
    })
    questions.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取问题列表失败:', error)
  } finally {
    loading.value = false
  }
}

watch(() => route.params.id, () => {
  fetchTagInfo()
  fetchQuestions()
}, { immediate: true })

onMounted(() => {
  fetchTagInfo()
  fetchQuestions()
})
</script>

<style scoped>
.tag-questions {
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
  margin-bottom: 16px;
}

.tag-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.question-count {
  font-size: 14px;
  color: #666;
}

.tag-description {
  color: #666;
  line-height: 1.5;
}

.list-header {
  display: flex;
  justify-content: flex-end;
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

.question-summary {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  line-height: 1.5;
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

.empty-state {
  padding: 40px;
}
</style>
