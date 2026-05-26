<template>
  <div class="favorites container">
    <div class="card">
      <div class="page-header">
        <h2>我的收藏</h2>
      </div>

      <div v-loading="loading">
        <div v-for="item in favorites" :key="item.id" class="favorite-item" @click="router.push(`/questions/${item.questionId}`)">
          <div class="question-stats">
            <div class="stat">
              <span class="stat-value">{{ item.voteCount || 0 }}</span>
              <span class="stat-label">投票</span>
            </div>
            <div class="stat" :class="{ 'stat-active': item.answerCount > 0 }">
              <span class="stat-value">{{ item.answerCount || 0 }}</span>
              <span class="stat-label">回答</span>
            </div>
          </div>
          <div class="question-content">
            <h3 class="question-title">{{ item.title }}</h3>
            <p class="question-summary">{{ item.content?.substring(0, 150) }}...</p>
            <div class="question-meta">
              <div class="tags">
                <el-tag v-for="tag in item.tags" :key="tag.id" size="small">{{ tag.name }}</el-tag>
              </div>
              <div class="meta-right">
                <span class="time">收藏于 {{ formatTime(item.createdAt) }}</span>
                <el-button text type="danger" size="small" @click.stop="handleRemoveFavorite(item.questionId)">
                  取消收藏
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && favorites.length === 0" class="empty-state">
        <el-empty description="暂无收藏">
          <el-button type="primary" @click="router.push('/questions')">去浏览问题</el-button>
        </el-empty>
      </div>

      <div v-if="total > 0" class="pagination">
        <el-pagination
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchFavorites"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFavorites, removeFavorite } from '@/api/favorite'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const favorites = ref([])
const loading = ref(false)
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

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavorites({ page: page.value, size: pageSize.value })
    favorites.value = res.data.records || res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取收藏列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleRemoveFavorite = async (questionId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏该问题吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeFavorite(questionId)
    ElMessage.success('已取消收藏')
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
    }
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.favorites {
  padding: 24px 0;
  max-width: 900px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.page-header h2 {
  font-size: 24px;
}

.favorite-item {
  display: flex;
  gap: 20px;
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.3s;
}

.favorite-item:hover {
  background: #fafafa;
}

.question-stats {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 60px;
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

.meta-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.time {
  font-size: 12px;
  color: #999;
}

.empty-state {
  padding: 40px;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
