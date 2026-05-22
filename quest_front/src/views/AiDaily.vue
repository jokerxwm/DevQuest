<template>
  <div class="ai-daily container">
    <div class="content-wrapper">
      <div class="main-content">
        <div class="daily-header card">
          <h2>AI 每日快讯</h2>
          <p class="subtitle">由 AI 为你精选的技术热点与社区动态</p>
        </div>

        <div v-loading="loading">
          <div v-for="(daily, index) in dailyList" :key="daily.id" class="daily-card card">
            <div class="daily-date">
              <span class="date-day">{{ formatDay(daily.date) }}</span>
              <span class="date-month">{{ formatMonth(daily.date) }}</span>
            </div>
            <div class="daily-content">
              <h3 class="daily-title">{{ daily.title }}</h3>
              <div class="daily-body">{{ daily.content }}</div>
              <div class="daily-tags">
                <el-tag v-for="tag in daily.tags" :key="tag" size="small" type="info">{{ tag }}</el-tag>
              </div>
              <div class="daily-footer">
                <span class="daily-time">{{ formatTime(daily.createdAt) }}</span>
                <div class="daily-actions">
                  <el-button text size="small" @click="handleShare(daily)">
                    <el-icon><Share /></el-icon> 分享
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-if="!loading && dailyList.length === 0" description="暂无快讯" />
      </div>

      <div class="sidebar">
        <div class="card sidebar-card">
          <h3>AI 快讯</h3>
          <p class="tip-text">每日快讯由 AI 自动分析社区热门问题和技术趋势生成，帮助你快速了解技术动态。</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Share } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const dailyList = ref([])
const loading = ref(false)

const formatDay = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).getDate()
}

const formatMonth = (dateStr) => {
  if (!dateStr) return ''
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  return months[new Date(dateStr).getMonth()]
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString()
}

const fetchDailyList = async () => {
  loading.value = true
  try {
    const res = await request.get('/ai/daily', { params: { page: 1, size: 20 } })
    dailyList.value = res.data.list || []
  } catch (error) {
    console.error('获取AI快讯失败:', error)
  } finally {
    loading.value = false
  }
}

const handleShare = (daily) => {
  const text = `${daily.title}\n${daily.content?.substring(0, 200)}...`
  if (navigator.clipboard) {
    navigator.clipboard.writeText(text)
    ElMessage.success('已复制到剪贴板')
  }
}

onMounted(() => {
  fetchDailyList()
})
</script>

<style scoped>
.ai-daily {
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

.daily-header {
  margin-bottom: 16px;
}

.daily-header h2 {
  font-size: 24px;
  margin-bottom: 8px;
}

.subtitle {
  color: #666;
  font-size: 14px;
}

.daily-card {
  display: flex;
  gap: 20px;
  transition: box-shadow 0.3s;
}

.daily-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.daily-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 60px;
  padding: 12px;
  background: linear-gradient(135deg, #1e80ff, #36d399);
  border-radius: 8px;
  color: #fff;
}

.date-day {
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
}

.date-month {
  font-size: 13px;
  margin-top: 4px;
}

.daily-content {
  flex: 1;
  min-width: 0;
}

.daily-title {
  font-size: 18px;
  color: #333;
  margin-bottom: 12px;
}

.daily-body {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 12px;
}

.daily-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.daily-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.daily-time {
  font-size: 12px;
  color: #999;
}

.daily-actions {
  display: flex;
  gap: 8px;
}

.sidebar-card h3 {
  font-size: 16px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.tip-text {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}
</style>
