<template>
  <div class="tags-page container">
    <div class="page-header card">
      <h2>标签</h2>
      <p class="description">标签用于对问题进行分类，帮助你快速找到感兴趣的内容</p>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索标签..."
        :prefix-icon="Search"
        clearable
        class="search-input"
      />
    </div>

    <div v-loading="loading" class="tags-grid">
      <div v-for="tag in filteredTags" :key="tag.id" class="tag-card card" @click="router.push(`/tags/${tag.id}`)">
        <div class="tag-header">
          <el-tag size="large" effect="plain">{{ tag.name }}</el-tag>
          <span class="question-count">{{ tag.questionCount || 0 }} 个问题</span>
        </div>
        <p class="tag-description">{{ tag.description || '暂无描述' }}</p>
      </div>
    </div>

    <div v-if="!loading && filteredTags.length === 0" class="empty-state card">
      <el-empty description="未找到相关标签" />
    </div>

    <div v-if="total > 0" class="pagination">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchTags"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getTags } from '@/api/tag'

const router = useRouter()

const tags = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const page = ref(1)
const pageSize = ref(24)
const total = ref(0)

const filteredTags = computed(() => {
  if (!searchKeyword.value) return tags.value
  const keyword = searchKeyword.value.toLowerCase()
  return tags.value.filter(tag =>
    tag.name.toLowerCase().includes(keyword) ||
    (tag.description && tag.description.toLowerCase().includes(keyword))
  )
})

const fetchTags = async () => {
  loading.value = true
  try {
    const res = await getTags({ page: page.value, size: pageSize.value })
    tags.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取标签列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchTags()
})
</script>

<style scoped>
.tags-page {
  padding: 24px 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 8px;
}

.description {
  color: #666;
  margin-bottom: 16px;
}

.search-input {
  max-width: 400px;
}

.tags-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.tag-card {
  cursor: pointer;
  transition: box-shadow 0.3s;
}

.tag-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.tag-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.question-count {
  font-size: 13px;
  color: #999;
}

.tag-description {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
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
