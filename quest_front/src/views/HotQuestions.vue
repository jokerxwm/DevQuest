<template>
  <div class="hot-page">
    <section class="hot-hero">
      <div class="hot-hero-inner">
        <span class="section-eyebrow">Trending</span>
        <div class="hh-row">
          <div>
            <h1 class="hot-title">技术热榜</h1>
            <p class="hot-desc">社区中最受欢迎的技术问题</p>
          </div>
          <div class="time-tabs">
            <button class="tt-btn" :class="{ active: timeRange === 'day' }" @click="timeRange = 'day'; fetchHotQuestions()">日榜</button>
            <button class="tt-btn" :class="{ active: timeRange === 'week' }" @click="timeRange = 'week'; fetchHotQuestions()">周榜</button>
            <button class="tt-btn" :class="{ active: timeRange === 'month' }" @click="timeRange = 'month'; fetchHotQuestions()">月榜</button>
          </div>
        </div>
      </div>
    </section>

    <section class="hot-body">
      <div class="hot-container">
        <div class="hot-grid">
          <div class="hot-main">
            <div v-loading="loading" class="hot-list">
              <div
                v-for="(question, index) in hotQuestions"
                :key="question.id"
                class="hot-card"
                :class="{ 'top-3': index < 3 }"
                @click="router.push(`/questions/${question.id}`)"
              >
                <div class="rank-box" :class="`rank-${index + 1}`">
                  <span class="rank-num">{{ index + 1 }}</span>
                </div>
                <div class="hot-content">
                  <h3 class="hot-q-title">{{ question.title }}</h3>
                  <p class="hot-excerpt">{{ question.content?.substring(0, 140) }}...</p>
                  <div class="hot-meta">
                    <div class="hot-tags">
                      <span
                        v-for="tag in (question.tags || []).slice(0, 3)"
                        :key="tag.id || tag"
                        class="hot-tag"
                      >
                        {{ tag.name || tag }}
                      </span>
                    </div>
                    <div class="hot-info">
                      <span class="hi-item">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 19V5M5 12l7-7 7 7"/></svg>
                        {{ question.voteCount || 0 }}
                      </span>
                      <span class="hi-item">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                        {{ question.answerCount || 0 }}
                      </span>
                      <span class="hi-item">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                        {{ question.viewCount || 0 }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="!loading && hotQuestions.length === 0" class="empty-box">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="empty-icon">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
              <p>暂无热榜数据</p>
            </div>
          </div>

          <aside class="hot-side">
            <div class="card-lux side-card">
              <h3 class="side-title">热榜规则</h3>
              <div class="side-divider"></div>
              <p class="rule-text">热榜根据问题的投票数、回答数、浏览量等综合指标进行排名。</p>
              <div class="rule-list">
                <div class="rule-item">
                  <div class="ri-dot"></div>
                  <span>日榜：最近 24 小时的热门问题</span>
                </div>
                <div class="rule-item">
                  <div class="ri-dot"></div>
                  <span>周榜：最近 7 天的热门问题</span>
                </div>
                <div class="rule-item">
                  <div class="ri-dot"></div>
                  <span>月榜：最近 30 天的热门问题</span>
                </div>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </section>
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
  } catch (error) { console.error('获取热榜失败:', error) }
  finally { loading.value = false }
}

onMounted(() => { fetchHotQuestions() })
</script>

<style scoped>
.hot-page { overflow: hidden; }

.hot-hero {
  background: var(--cream);
  padding: 100px 0 40px;
  border-bottom: 1px solid var(--border-light);
}

.hot-hero-inner {
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

.hh-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.hot-title {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 2rem;
  font-weight: 400;
  color: var(--forest);
  margin-bottom: 6px;
}

.hot-desc {
  font-size: 0.9rem;
  color: var(--forest-60);
  font-weight: 300;
}

.time-tabs {
  display: flex;
  gap: 4px;
  background: var(--cream-dark);
  border-radius: 14px;
  padding: 4px;
}

.tt-btn {
  padding: 8px 22px;
  border: none;
  background: transparent;
  border-radius: 10px;
  font-size: 0.8rem;
  font-weight: 500;
  color: var(--forest-60);
  cursor: pointer;
  transition: all 0.2s;
}

.tt-btn.active {
  background: var(--caramel);
  color: #fff;
}

.tt-btn:hover:not(.active) {
  color: var(--forest);
  background: rgba(184, 138, 89, 0.08);
}

.hot-body {
  padding: 40px 0 100px;
}

.hot-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.hot-grid {
  display: flex;
  gap: 36px;
}

.hot-main {
  flex: 1;
  min-width: 0;
}

.hot-side {
  width: 300px;
  flex-shrink: 0;
}

.hot-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.hot-card {
  display: flex;
  gap: 20px;
  padding: 24px;
  background: #fff;
  border: 1px solid var(--forest-08);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.hot-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 48px rgba(26, 46, 38, 0.08);
  border-color: var(--caramel-light);
}

.hot-card.top-3 {
  border-left: 4px solid var(--caramel);
}

.rank-box {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: var(--cream-dark);
  transition: all 0.3s;
}

.rank-num {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--forest-40);
}

.rank-1 {
  background: linear-gradient(135deg, var(--caramel), var(--caramel-light));
}

.rank-1 .rank-num {
  color: #fff;
}

.rank-2 {
  background: linear-gradient(135deg, var(--forest), var(--forest-light));
}

.rank-2 .rank-num {
  color: var(--cream);
}

.rank-3 {
  background: var(--caramel);
}

.rank-3 .rank-num {
  color: #fff;
}

.hot-content {
  flex: 1;
  min-width: 0;
}

.hot-q-title {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1.05rem;
  font-weight: 500;
  color: var(--forest);
  margin-bottom: 8px;
  line-height: 1.5;
  transition: color 0.3s;
}

.hot-card:hover .hot-q-title {
  color: var(--caramel);
}

.hot-excerpt {
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

.hot-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hot-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.hot-tag {
  display: inline-block;
  padding: 3px 12px;
  background: var(--cream);
  border-radius: 999px;
  font-size: 0.7rem;
  color: var(--forest-80);
  font-weight: 500;
  transition: all 0.2s;
}

.hot-tag:hover {
  background: var(--caramel);
  color: #fff;
}

.hot-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.hi-item {
  display: flex;
  align-items: center;
  gap: 4px;
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

.rule-text {
  font-size: 0.85rem;
  color: var(--forest-60);
  line-height: 1.7;
  margin-bottom: 18px;
  font-weight: 300;
}

.rule-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rule-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.82rem;
  color: var(--forest-70);
}

.ri-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--caramel);
  flex-shrink: 0;
}

@media (max-width: 1024px) {
  .hot-grid { flex-direction: column; }
  .hot-side { width: 100%; }
  .side-card { position: static; }
  .hh-row { flex-direction: column; gap: 16px; align-items: flex-start; }
}

@media (max-width: 640px) {
  .hot-card { flex-direction: column; }
  .hot-meta { flex-direction: column; align-items: flex-start; gap: 10px; }
}
</style>
