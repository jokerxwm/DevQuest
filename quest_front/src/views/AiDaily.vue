<template>
  <div class="daily-page">
    <section class="daily-hero">
      <div class="daily-hero-inner">
        <span class="section-eyebrow">AI Daily</span>
        <div class="dh-row">
          <div>
            <h1 class="daily-title">AI 日报</h1>
            <p class="daily-desc">AI 生成的技术热点日报，每天更新</p>
          </div>
          <button class="btn-caramel" :disabled="generating" @click="generateDaily">
            {{ generating ? '生成中...' : '生成今日日报' }}
          </button>
        </div>
      </div>
    </section>

    <section class="daily-body">
      <div class="daily-container">
        <div v-loading="loading" class="daily-list">
          <div v-for="daily in dailies" :key="daily.id" class="daily-card">
            <div class="dc-date">
              <span class="dc-day">{{ new Date(daily.createdAt).getDate() }}</span>
              <span class="dc-month">{{ new Date(daily.createdAt).toLocaleString('default', { month: 'short' }) }}</span>
            </div>
            <div class="dc-body">
              <h3 class="dc-title">{{ daily.title }}</h3>
              <p class="dc-content">{{ daily.content }}</p>
              <div class="dc-tags" v-if="daily.tags">
                <span v-for="tag in daily.tags" :key="tag" class="dc-tag">{{ tag }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!loading && dailies.length === 0" class="empty-box">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="empty-icon">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2" /><line x1="16" y1="2" x2="16" y2="6" /><line x1="8" y1="2" x2="8" y2="6" /><line x1="3" y1="10" x2="21" y2="10" />
          </svg>
          <p>暂无日报</p>
          <button class="btn-caramel" @click="generateDaily">生成第一份日报</button>
        </div>

        <div v-if="total > 0" class="pagination">
          <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="fetchDailies" />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const dailies = ref([])
const loading = ref(false)
const generating = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchDailies = async () => {
  loading.value = true
  try { const res = await request.get('/ai/daily', { params: { page: page.value, size: pageSize.value } }); dailies.value = res.data.list || []; total.value = res.data.total || 0 }
  catch (e) { console.error(e) }
  finally { loading.value = false }
}

const generateDaily = async () => {
  generating.value = true
  try { await request.post('/ai/daily/generate'); ElMessage.success('日报生成成功'); fetchDailies() }
  catch (e) { console.error(e) }
  finally { generating.value = false }
}

onMounted(() => { fetchDailies() })
</script>

<style scoped>
.daily-page { overflow: hidden; }
.daily-hero { background: var(--cream); padding: 100px 0 40px; border-bottom: 1px solid var(--border-light); }
.daily-hero-inner { max-width: 900px; margin: 0 auto; padding: 0 2rem; }
.section-eyebrow { display: block; font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.15em; color: var(--caramel); font-weight: 500; margin-bottom: 8px; }
.dh-row { display: flex; justify-content: space-between; align-items: flex-end; }
.daily-title { font-family: 'Noto Serif SC', serif; font-size: 2rem; font-weight: 400; color: var(--forest); margin-bottom: 6px; }
.daily-desc { font-size: 0.9rem; color: var(--forest-60); font-weight: 300; }
.btn-caramel { background: var(--caramel); color: #fff; border: none; padding: 12px 28px; border-radius: 999px; font-size: 0.8rem; font-weight: 500; letter-spacing: 0.08em; text-transform: uppercase; cursor: pointer; transition: background 0.3s; }
.btn-caramel:hover { background: var(--caramel-light); }
.btn-caramel:disabled { opacity: 0.6; cursor: not-allowed; }
.daily-body { padding: 48px 0 100px; }
.daily-container { max-width: 900px; margin: 0 auto; padding: 0 2rem; }
.daily-list { display: flex; flex-direction: column; gap: 16px; }
.daily-card { display: flex; gap: 24px; padding: 24px; background: #fff; border: 1px solid var(--forest-08); border-radius: 20px; transition: all 0.3s; }
.daily-card:hover { transform: translateY(-2px); box-shadow: 0 12px 40px rgba(26,46,38,0.06); }
.dc-date { display: flex; flex-direction: column; align-items: center; min-width: 50px; padding: 8px; background: var(--cream-dark); border-radius: 12px; }
.dc-day { font-family: 'Noto Serif SC', serif; font-size: 1.5rem; font-weight: 500; color: var(--forest); line-height: 1; }
.dc-month { font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.08em; color: var(--forest-40); margin-top: 4px; }
.dc-body { flex: 1; min-width: 0; }
.dc-title { font-family: 'Noto Serif SC', serif; font-size: 1.1rem; font-weight: 500; color: var(--forest); margin-bottom: 8px; }
.dc-content { font-size: 0.85rem; color: var(--forest-60); line-height: 1.8; margin-bottom: 12px; }
.dc-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.dc-tag { display: inline-block; padding: 3px 12px; background: var(--cream); border-radius: 999px; font-size: 0.7rem; color: var(--forest-80); font-weight: 500; }
.empty-box { text-align: center; padding: 60px 0; }
.empty-icon { color: var(--forest-20); margin-bottom: 14px; }
.empty-box p { font-size: 0.9rem; color: var(--forest-40); margin-bottom: 20px; }
.pagination { margin-top: 32px; display: flex; justify-content: center; }
@media (max-width: 640px) { .dh-row { flex-direction: column; gap: 16px; align-items: flex-start; } .daily-card { flex-direction: column; } }
</style>
