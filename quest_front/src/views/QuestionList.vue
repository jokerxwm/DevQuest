<template>
  <div class="ql-page">
    <section class="ql-hero">
      <div class="ql-hero-inner">
        <span class="section-eyebrow">All Questions</span>
        <div class="qlh-row">
          <div>
            <h1 class="ql-title">问题列表</h1>
            <p class="ql-desc">浏览社区中的所有技术问题</p>
          </div>
          <button class="btn-caramel" @click="router.push('/ask')">
            我要提问
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
          </button>
        </div>
      </div>
    </section>

    <section class="ql-body">
      <div class="ql-container">
        <div class="ql-grid">
          <div class="ql-main">
            <div class="ql-toolbar">
              <div class="ql-tabs">
                <button class="ql-tab" :class="{ active: sort === 'newest' }" @click="sort = 'newest'; fetchQuestions()">最新</button>
                <button class="ql-tab" :class="{ active: sort === 'hot' }" @click="sort = 'hot'; fetchQuestions()">最热</button>
                <button class="ql-tab" :class="{ active: sort === 'unanswered' }" @click="sort = 'unanswered'; fetchQuestions()">未回答</button>
              </div>
              <span class="ql-count">共 {{ total }} 个问题</span>
            </div>

            <div v-loading="loading" class="question-list">
              <div
                v-for="question in questions"
                :key="question.id"
                class="q-card"
                @click="router.push(`/questions/${question.id}`)"
              >
                <div class="q-stats">
                  <div class="qs-item" :class="{ active: question.voteCount > 0 }">
                    <span class="qs-val">{{ question.voteCount || 0 }}</span>
                    <span class="qs-lbl">投票</span>
                  </div>
                  <div class="qs-item" :class="{ active: question.answerCount > 0 }">
                    <span class="qs-val">{{ question.answerCount || 0 }}</span>
                    <span class="qs-lbl">回答</span>
                  </div>
                  <div class="qs-item">
                    <span class="qs-val">{{ question.viewCount || 0 }}</span>
                    <span class="qs-lbl">浏览</span>
                  </div>
                </div>
                <div class="q-content">
                  <h3 class="q-title">{{ question.title }}</h3>
                  <p class="q-excerpt">{{ question.content?.substring(0, 140) }}...</p>
                  <div class="q-meta">
                    <div class="q-tags">
                      <span
                        v-for="tag in (question.tags || []).slice(0, 4)"
                        :key="tag.id || tag"
                        class="q-tag"
                      >
                        {{ tag.name || tag }}
                      </span>
                    </div>
                    <div class="q-info">
                      <div class="qi-author">
                        <div class="qi-avatar">{{ (question.authorName || 'U').charAt(0).toUpperCase() }}</div>
                        <span class="qi-name">{{ question.authorName }}</span>
                      </div>
                      <span class="qi-time">{{ formatTime(question.createdAt) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="!loading && questions.length === 0" class="empty-box">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="empty-icon">
                <circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/>
              </svg>
              <p>暂无问题</p>
              <button class="btn-caramel" @click="router.push('/ask')">成为第一个提问者</button>
            </div>

            <div v-if="total > 0" class="pagination">
              <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="fetchQuestions" />
            </div>
          </div>

          <aside class="ql-side">
            <div class="card-lux side-card">
              <h3 class="side-title">热门标签</h3>
              <div class="side-divider"></div>
              <div class="tag-cloud">
                <span
                  v-for="tag in hotTags"
                  :key="tag.id"
                  class="cloud-tag"
                  :class="{ selected: tagId == tag.id }"
                  @click="handleTagClick(tag.id)"
                >
                  {{ tag.name }}
                </span>
              </div>
              <button v-if="tagId" class="clear-filter" @click="tagId = null; page = 1; fetchQuestions()">
                清除筛选
              </button>
            </div>
          </aside>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getQuestionList } from '@/api/question'
import { getHotTags } from '@/api/tag'

const router = useRouter()
const route = useRoute()

const questions = ref([])
const hotTags = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const sort = ref('newest')
const tagId = ref(null)

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

const fetchQuestions = async () => {
  loading.value = true
  try {
    const res = await getQuestionList({ page: page.value, size: pageSize.value, sort: sort.value, tagId: tagId.value })
    questions.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) { console.error('获取问题列表失败:', error) }
  finally { loading.value = false }
}

const fetchHotTags = async () => {
  try { const res = await getHotTags(); hotTags.value = res.data || [] } catch (e) { console.error(e) }
}

const handleTagClick = (id) => {
  tagId.value = id
  page.value = 1
  fetchQuestions()
}

watch(() => route.query.tag, (newTag) => {
  if (newTag) { tagId.value = newTag; page.value = 1; fetchQuestions() }
}, { immediate: true })

onMounted(() => { fetchQuestions(); fetchHotTags() })
</script>

<style scoped>
.ql-page { overflow: hidden; }

.ql-hero {
  background: var(--cream);
  padding: 100px 0 40px;
  border-bottom: 1px solid var(--border-light);
}

.ql-hero-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.section-eyebrow {
  display: block;
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 0.15em;
  color: var(--caramel);
  font-weight: 500;
  margin-bottom: 8px;
}

.qlh-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.ql-title {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 2rem;
  font-weight: 400;
  color: var(--forest);
  margin-bottom: 6px;
}

.ql-desc {
  font-size: 0.9rem;
  color: var(--forest-60);
  font-weight: 300;
}

.btn-caramel {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: var(--caramel);
  color: #fff;
  border: none;
  padding: 12px 28px;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 500;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-caramel:hover {
  background: var(--caramel-light);
}

.ql-body {
  padding: 40px 0 100px;
}

.ql-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.ql-grid {
  display: flex;
  gap: 36px;
}

.ql-main {
  flex: 1;
  min-width: 0;
}

.ql-side {
  width: 300px;
  flex-shrink: 0;
}

.ql-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.ql-tabs {
  display: flex;
  gap: 4px;
  background: #fff;
  border: 1px solid var(--forest-08);
  border-radius: 14px;
  padding: 4px;
}

.ql-tab {
  padding: 8px 20px;
  border: none;
  background: transparent;
  border-radius: 10px;
  font-size: 0.8rem;
  font-weight: 500;
  color: var(--forest-60);
  cursor: pointer;
  transition: all 0.2s;
}

.ql-tab.active {
  background: var(--forest);
  color: var(--cream);
}

.ql-tab:hover:not(.active) {
  color: var(--caramel);
}

.ql-count {
  font-size: 0.78rem;
  color: var(--forest-40);
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.q-card {
  display: flex;
  gap: 20px;
  padding: 24px;
  background: #fff;
  border: 1px solid var(--forest-08);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.q-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 48px rgba(26, 46, 38, 0.08);
  border-color: var(--caramel-light);
}

.q-stats {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 60px;
}

.qs-item {
  text-align: center;
  padding: 6px 8px;
  border-radius: 10px;
  background: var(--cream-dark);
  transition: all 0.2s;
}

.qs-item.active {
  background: rgba(184, 138, 89, 0.1);
}

.qs-val {
  display: block;
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1rem;
  font-weight: 500;
  color: var(--forest);
  line-height: 1;
  margin-bottom: 3px;
}

.qs-lbl {
  font-size: 0.65rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--forest-40);
}

.q-content {
  flex: 1;
  min-width: 0;
}

.q-title {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1.05rem;
  font-weight: 500;
  color: var(--forest);
  margin-bottom: 8px;
  line-height: 1.5;
  transition: color 0.3s;
}

.q-card:hover .q-title {
  color: var(--caramel);
}

.q-excerpt {
  font-size: 0.85rem;
  color: var(--forest-60);
  line-height: 1.7;
  margin-bottom: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-weight: 300;
}

.q-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.q-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.q-tag {
  display: inline-block;
  padding: 3px 12px;
  background: var(--cream);
  border-radius: 999px;
  font-size: 0.7rem;
  color: var(--forest-80);
  font-weight: 500;
  transition: all 0.2s;
}

.q-tag:hover {
  background: var(--caramel);
  color: #fff;
}

.q-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.qi-author {
  display: flex;
  align-items: center;
  gap: 6px;
}

.qi-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--forest);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.65rem;
  font-weight: 600;
  color: var(--caramel);
}

.qi-name {
  font-size: 0.78rem;
  color: var(--forest);
  font-weight: 500;
}

.qi-time {
  font-size: 0.75rem;
  color: var(--forest-40);
}

.empty-box {
  text-align: center;
  padding: 60px 0;
}

.empty-icon {
  color: var(--forest-20);
  margin-bottom: 14px;
}

.empty-box p {
  font-size: 0.9rem;
  color: var(--forest-40);
  margin-bottom: 20px;
}

.pagination {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

.card-lux {
  background: #fff;
  border: 1px solid var(--forest-08);
  border-radius: 20px;
  padding: 28px;
}

.side-card {
  position: sticky;
  top: 100px;
}

.side-title {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1.1rem;
  font-weight: 500;
  color: var(--forest);
}

.side-divider {
  width: 32px;
  height: 1.5px;
  background: var(--caramel);
  margin: 12px 0 18px;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.cloud-tag {
  display: inline-block;
  padding: 6px 16px;
  background: var(--cream);
  border: 1px solid var(--forest-08);
  border-radius: 999px;
  font-size: 0.78rem;
  color: var(--forest-80);
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.cloud-tag:hover, .cloud-tag.selected {
  background: var(--caramel);
  color: #fff;
  border-color: var(--caramel);
  transform: translateY(-1px);
}

.clear-filter {
  display: block;
  width: 100%;
  margin-top: 14px;
  padding: 8px;
  border: none;
  background: transparent;
  font-size: 0.75rem;
  color: var(--caramel);
  cursor: pointer;
  font-weight: 500;
  text-align: center;
  transition: color 0.2s;
}

.clear-filter:hover {
  color: var(--forest);
}

@media (max-width: 1024px) {
  .ql-grid { flex-direction: column; }
  .ql-side { width: 100%; }
  .side-card { position: static; }
  .qlh-row { flex-direction: column; gap: 16px; align-items: flex-start; }
}

@media (max-width: 640px) {
  .q-card { flex-direction: column; }
  .q-stats { flex-direction: row; }
  .q-meta { flex-direction: column; align-items: flex-start; gap: 10px; }
}
</style>
