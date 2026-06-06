<template>
  <div class="tq-page">
    <section class="tq-hero">
      <div class="tq-hero-inner">
        <span class="section-eyebrow">Tagged Questions</span>
        <div class="tqh-row">
          <div>
            <h1 class="tq-title">{{ tagInfo.name || '标签' }}</h1>
            <p class="tq-desc" v-if="tagInfo.description">{{ tagInfo.description }}</p>
            <span class="tq-count">{{ total }} 个问题</span>
          </div>
          <button class="btn-caramel" @click="router.push('/ask')">我要提问</button>
        </div>
      </div>
    </section>

    <section class="tq-body">
      <div class="tq-container">
        <div class="tq-grid">
          <div class="tq-main">
            <div class="tq-toolbar">
              <div class="tq-tabs">
                <button class="tq-tab" :class="{ active: sort === 'newest' }" @click="sort = 'newest'; fetchQuestions()">最新</button>
                <button class="tq-tab" :class="{ active: sort === 'hot' }" @click="sort = 'hot'; fetchQuestions()">最热</button>
                <button class="tq-tab" :class="{ active: sort === 'unanswered' }" @click="sort = 'unanswered'; fetchQuestions()">未回答</button>
              </div>
            </div>

            <div v-loading="loading" class="question-list">
              <div v-for="q in questions" :key="q.id" class="q-card" @click="router.push(`/questions/${q.id}`)">
                <div class="q-stats">
                  <div class="qs-item"><span class="qs-val">{{ q.voteCount || 0 }}</span><span class="qs-lbl">投票</span></div>
                  <div class="qs-item" :class="{ active: q.answerCount > 0 }"><span class="qs-val">{{ q.answerCount || 0 }}</span><span class="qs-lbl">回答</span></div>
                </div>
                <div class="q-content">
                  <h3 class="q-title">{{ q.title }}</h3>
                  <p class="q-excerpt">{{ q.content?.substring(0, 120) }}...</p>
                  <div class="q-meta">
                    <div class="q-tags"><span v-for="t in (q.tags || []).slice(0, 3)" :key="t.id || t" class="q-tag">{{ t.name || t }}</span></div>
                    <span class="qi-time">{{ formatTime(q.createdAt) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="!loading && questions.length === 0" class="empty-box"><p>该标签下暂无问题</p></div>
            <div v-if="total > 0" class="pagination"><el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="fetchQuestions" /></div>
          </div>

          <aside class="tq-side">
            <div class="card-lux side-card">
              <h3 class="side-title">相关标签</h3>
              <div class="side-divider"></div>
              <div class="tag-cloud">
                <span v-for="t in relatedTags" :key="t.id" class="cloud-tag" @click="router.push(`/tags/${t.id}`)">{{ t.name }}</span>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </section>
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

const formatTime = (t) => { if (!t) return ''; const d = new Date(t); const diff = Date.now() - d; if (diff < 3600000) return `${Math.floor(Math.max(diff,60000)/60000)}分钟前`; if (diff < 86400000) return `${Math.floor(diff/3600000)}小时前`; if (diff < 2592000000) return `${Math.floor(diff/86400000)}天前`; return d.toLocaleDateString() }

const fetchTagInfo = async () => {
  try { const res = await getTags({ page: 1, size: 100 }); const tags = res.data.list || []; tagInfo.value = tags.find(t => t.id == route.params.id) || { name: '未知标签' }; relatedTags.value = tags.filter(t => t.id != route.params.id).slice(0, 10) }
  catch (e) { console.error(e) }
}

const fetchQuestions = async () => {
  loading.value = true
  try { const res = await getQuestionList({ page: page.value, size: pageSize.value, sort: sort.value, tagId: route.params.id }); questions.value = res.data.list || []; total.value = res.data.total || 0 }
  catch (e) { console.error(e) }
  finally { loading.value = false }
}

watch(() => route.params.id, () => { fetchTagInfo(); fetchQuestions() }, { immediate: true })
onMounted(() => { fetchTagInfo(); fetchQuestions() })
</script>

<style scoped>
.tq-page { overflow: hidden; }
.tq-hero { background: var(--cream); padding: 100px 0 40px; border-bottom: 1px solid var(--border-light); }
.tq-hero-inner { max-width: 1200px; margin: 0 auto; padding: 0 2rem; }
.section-eyebrow { display: block; font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.15em; color: var(--caramel); font-weight: 500; margin-bottom: 8px; }
.tqh-row { display: flex; justify-content: space-between; align-items: flex-end; }
.tq-title { font-family: 'Noto Serif SC', serif; font-size: 2rem; font-weight: 400; color: var(--forest); margin-bottom: 6px; }
.tq-desc { font-size: 0.9rem; color: var(--forest-60); font-weight: 300; margin-bottom: 4px; }
.tq-count { font-size: 0.8rem; color: var(--forest-40); }
.btn-caramel { background: var(--caramel); color: #fff; border: none; padding: 12px 28px; border-radius: 999px; font-size: 0.8rem; font-weight: 500; letter-spacing: 0.08em; text-transform: uppercase; cursor: pointer; transition: background 0.3s; }
.btn-caramel:hover { background: var(--caramel-light); }
.tq-body { padding: 40px 0 100px; }
.tq-container { max-width: 1200px; margin: 0 auto; padding: 0 2rem; }
.tq-grid { display: flex; gap: 36px; }
.tq-main { flex: 1; min-width: 0; }
.tq-side { width: 300px; flex-shrink: 0; }
.tq-toolbar { margin-bottom: 24px; }
.tq-tabs { display: flex; gap: 4px; background: #fff; border: 1px solid var(--forest-08); border-radius: 14px; padding: 4px; width: max-content; }
.tq-tab { padding: 8px 20px; border: none; background: transparent; border-radius: 10px; font-size: 0.8rem; font-weight: 500; color: var(--forest-60); cursor: pointer; transition: all 0.2s; }
.tq-tab.active { background: var(--forest); color: var(--cream); }
.question-list { display: flex; flex-direction: column; gap: 14px; }
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
.qi-time { font-size: 0.75rem; color: var(--forest-40); }
.empty-box { text-align: center; padding: 60px 0; }
.empty-box p { font-size: 0.9rem; color: var(--forest-40); }
.pagination { margin-top: 32px; display: flex; justify-content: center; }
.card-lux { background: #fff; border: 1px solid var(--forest-08); border-radius: 20px; padding: 28px; }
.side-card { position: sticky; top: 100px; }
.side-title { font-family: 'Noto Serif SC', serif; font-size: 1.1rem; font-weight: 500; color: var(--forest); }
.side-divider { width: 32px; height: 1.5px; background: var(--caramel); margin: 12px 0 18px; }
.tag-cloud { display: flex; flex-wrap: wrap; gap: 8px; }
.cloud-tag { display: inline-block; padding: 6px 16px; background: var(--cream); border: 1px solid var(--forest-08); border-radius: 999px; font-size: 0.78rem; color: var(--forest-80); cursor: pointer; transition: all 0.2s; font-weight: 500; }
.cloud-tag:hover { background: var(--caramel); color: #fff; border-color: var(--caramel); }
@media (max-width: 1024px) { .tq-grid { flex-direction: column; } .tq-side { width: 100%; } .side-card { position: static; } .tqh-row { flex-direction: column; gap: 16px; align-items: flex-start; } }
</style>
