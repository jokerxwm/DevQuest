<template>
  <div class="hot-questions container">
    <div class="content-wrapper">
      <div class="main-content">
        <div class="hot-header card">
          <h2>热榜</h2>
          <el-radio-group v-model="timeRange" @change="fetchHotQuestions">
            <el-radio-button label="day">日榜</el-radio-button>
            <el-radio-button label="week">周榜</el-radio-button>
            <el-radio-button label="month">月榜</el-radio-button>
          </el-radio-group>
        </div>

        <div v-loading="loading">
          <div
            v-for="(question, index) in hotQuestions"
            :key="question.id"
            class="hot-item card"
            :class="{ 'top-3': index < 3 }"
            @click="router.push(`/questions/${question.id}`)"
          >
            <div class="rank" :class="`rank-${index + 1}`">
              {{ index + 1 }}
            </div>
            <div class="hot-content">
              <h3 class="hot-title">{{ question.title }}</h3>
              <p class="hot-summary">{{ question.content?.substring(0, 150) }}...</p>
              <div class="hot-meta">
                <div class="tags">
                  <el-tag v-for="tag in question.tags" :key="tag.id" size="small">{{ tag.name }}</el-tag>
                </div>
                <div class="stats">
                  <span>{{ question.voteCount || 0 }} 投票</span>
                  <span>{{ question.answerCount || 0 }} 回答</span>
                  <span>{{ question.viewCount || 0 }} 浏览</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-if="!loading && hotQuestions.length === 0" description="暂无热榜数据" />
      </div>

      <div class="sidebar">
        <div class="card sidebar-card">
          <h3>热榜规则</h3>
          <p class="rule-text">
            热榜根据问题的投票数、回答数、浏览量等综合指标进行排名。
          </p>
          <ul class="rule-list">
            <li>日榜：最近24小时的热门问题</li>
            <li>周榜：最近7天的热门问题</li>
            <li>月榜：最近30天的热门问题</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHotQuestions } from '@/api/question'

const router = useRouter()

const hotQuestions = ref([])
const loading = ref(false)
const timeRange = ref('day')

const fetchHotQuestions = async () => {
  loading.value = true
  try {
    const res = await getHotQuestions({ range: timeRange.value, page: 1, size: 30 })
    hotQuestions.value = res.data.list || []
  } catch (error) {
    console.error('获取热榜失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchHotQuestions()
})
</script>

<style scoped>
.hot-questions {
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

.hot-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.hot-header h2 {
  font-size: 24px;
}

.hot-item {
  display: flex;
  gap: 20px;
  cursor: pointer;
  transition: box-shadow 0.3s;
}

.hot-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.hot-item.top-3 {
  border-left: 4px solid #1e80ff;
}

.rank {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  flex-shrink: 0;
  background: #f5f5f5;
  color: #999;
}

.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffaa00);
  color: #fff;
}

.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #a0a0a0);
  color: #fff;
}

.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #b5651d);
  color: #fff;
}

.hot-content {
  flex: 1;
  min-width: 0;
}

.hot-title {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}

.hot-summary {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  line-height: 1.5;
}

.hot-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
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

.rule-text {
  color: #666;
  font-size: 14px;
  margin-bottom: 12px;
  line-height: 1.6;
}

.rule-list {
  padding-left: 16px;
  color: #666;
  font-size: 14px;
  line-height: 2;
}
</style>
