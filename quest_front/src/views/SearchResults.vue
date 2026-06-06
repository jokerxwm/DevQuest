<template>
  <div class="sr-page">
    <section class="sr-hero">
      <div class="sr-hero-inner">
        <span class="section-eyebrow">Search Results</span>
        <h1 class="sr-title">搜索结果</h1>
        <div class="sr-search">
          <el-input v-model="keyword" placeholder="搜索问题、标签或用户..." size="large" clearable @keyup.enter="doSearch">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </div>
    </section>

    <section class="sr-body">
      <div class="sr-container">
        <div class="sr-info" v-if="keyword">
          <p>共找到 <strong>{{ total }}</strong> 个结果</p>
        </div>

        <div v-loading="loading" class="sr-list">
          <div v-for="item in searchResults" :key="item.id" class="q-card" @click="router.push(`/questions/${item.id}`)">
            <div class="q-stats">
              <div class="qs-item" :class="{ active: item.voteCount > 0 }">
                <span class="qs-val">{{ item.voteCount || 0 }}</span>
                <span class="qs-lbl">投票</span>
              </div>
              <div class="qs-item" :class="{ active: item.answerCount > 0 }">
                <span class="qs-val">{{ item.answerCount || 0 }}</span>
                <span class="qs-lbl">回答</span>
              </div>
            </div>
            <div class="q-content">
              <h3 class="q-title">{{ item.title }}</h3>
              <p class="q-excerpt">{{ item.content?.substring(0, 140) }}...</p>
              <div class="q-meta">
                <div class="q-tags">
                  <span v-for="tag in (item.tags || []).slice(0, 3)" :key="tag.id || tag" class="q-tag">{{ tag.name || tag }}</span>
                </div>
                <span class="qi-time">{{ formatTime(item.createdAt) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!loading && searchResults.length === 0 && keyword" class="empty-box">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="empty-icon"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
          <p>未找到相关结果</p>
        </div>

        <div v-if="total > 0" class="pagination">
          <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="doSearch" />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const searchResults = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 86400000) return `${Math.floor(Math.max(diff, 3600000) / 3600000)}小时前`
  if (diff < 2592000000) return `${Math.floor(diff / 86400000)}天前`
  return date.toLocaleDateString()
}

const doSearch = async () => {
  if (!keyword.value.trim()) return
  loading.value = true
  try {
    const res = await request.get('/questions/search', { params: { keyword: keyword.value, page: page.value, size: pageSize.value } })
    searchResults.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) { console.error(error) }
  finally { loading.value = false }
}

onMounted(() => { if (route.query.keyword) { keyword.value = route.query.keyword; doSearch() } })
</script>

<style scoped>
.sr-page { overflow: hidden; }
.sr-hero { background: var(--cream); padding: 100px 0 40px; border-bottom: 1px solid var(--border-light); }
.sr-hero-inner { max-width: 800px; margin: 0 auto; padding: 0 2rem; text-align: center; }
.section-eyebrow { display: block; font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.15em; color: var(--caramel); font-weight: 500; margin-bottom: 8px; }
.sr-title { font-family: 'Noto Serif SC', 'Playfair Display', serif; font-size: 2rem; font-weight: 400; color: var(--forest); margin-bottom: 20px; }
.sr-search { max-width: 500px; margin: 0 auto; }
.sr-body { padding: 40px 0 100px; }
.sr-container { max-width: 900px; margin: 0 auto; padding: 0 2rem; }
.sr-info { margin-bottom: 20px; font-size: 0.85rem; color: var(--forest-60); }
.sr-info strong { color: var(--caramel); }
.sr-list { display: flex; flex-direction: column; gap: 14px; }
.q-card { display: flex; gap: 20px; padding: 24px; background: #fff; border: 1px solid var(--forest-08); border-radius: 20px; cursor: pointer; transition: all 0.3s; }
.q-card:hover { transform: translateY(-3px); box-shadow: 0 16px 48px rgba(26,46,38,0.08); border-color: var(--caramel-light); }
.q-stats { display: flex; flex-direction: column; gap: 6px; min-width: 56px; }
.qs-item { text-align: center; padding: 6px 8px; border-radius: 10px; background: var(--cream-dark); }
.qs-item.active { background: rgba(184,138,89,0.1); }
.qs-val { display: block; font-family: 'Noto Serif SC', serif; font-size: 1rem; font-weight: 500; color: var(--forest); line-height: 1; margin-bottom: 3px; }
.qs-lbl { font-size: 0.65rem; text-transform: uppercase; letter-spacing: 0.08em; color: var(--forest-40); }
.q-content { flex: 1; min-width: 0; }
.q-title { font-family: 'Noto Serif SC', serif; font-size: 1.05rem; font-weight: 500; color: var(--forest); margin-bottom: 8px; transition: color 0.3s; }
.q-card:hover .q-title { color: var(--caramel); }
.q-excerpt { font-size: 0.85rem; color: var(--forest-60); line-height: 1.7; margin-bottom: 14px; font-weight: 300; }
.q-meta { display: flex; justify-content: space-between; align-items: center; }
.q-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.q-tag { display: inline-block; padding: 3px 12px; background: var(--cream); border-radius: 999px; font-size: 0.7rem; color: var(--forest-80); font-weight: 500; }
.q-tag:hover { background: var(--caramel); color: #fff; }
.qi-time { font-size: 0.75rem; color: var(--forest-40); }
.empty-box { text-align: center; padding: 60px 0; }
.empty-icon { color: var(--forest-20); margin-bottom: 14px; }
.empty-box p { font-size: 0.9rem; color: var(--forest-40); }
.pagination { margin-top: 32px; display: flex; justify-content: center; }
</style>
