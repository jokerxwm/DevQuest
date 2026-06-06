<template>
  <div class="home">
    <section class="hero">
      <div class="hero-content">
        <div class="hero-badge">
          <span class="badge-dot"></span>
          <span>AI 驱动的智能问答社区</span>
        </div>
        <h1 class="hero-title">
          遇到技术难题？
          <br />
          <span class="title-italic">让开发者社区</span>帮你解答
        </h1>
        <p class="hero-desc">
          DevQuest 汇聚全球开发者智慧，结合 AI 技术，为你提供精准、高效的技术问答体验
        </p>
        <div class="hero-actions">
          <button class="btn-primary" @click="router.push('/ask')">
            开始提问
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M12 5l7 7-7 7" />
            </svg>
          </button>
          <button class="btn-outline" @click="router.push('/questions')">
            浏览问答
          </button>
        </div>
        <div class="hero-features">
          <div class="hf-item">
            <svg width="28" height="28" viewBox="0 0 32 32" fill="none" stroke="currentColor" stroke-width="1.5" class="hf-icon">
              <rect x="4" y="4" width="24" height="24" rx="3" />
              <path d="M10 12h12M10 16h8M10 20h10" />
            </svg>
            <span>精准回答</span>
          </div>
          <div class="hf-item">
            <svg width="28" height="28" viewBox="0 0 32 32" fill="none" stroke="currentColor" stroke-width="1.5" class="hf-icon">
              <circle cx="16" cy="16" r="12" />
              <path d="M16 4C16 4 20 9 20 16C20 23 16 28 16 28" />
              <path d="M16 4C16 4 12 9 12 16C12 23 16 28 16 28" />
              <path d="M4 16h24" />
            </svg>
            <span>AI 加持</span>
          </div>
          <div class="hf-item">
            <svg width="28" height="28" viewBox="0 0 32 32" fill="none" stroke="currentColor" stroke-width="1.5" class="hf-icon">
              <circle cx="16" cy="12" r="6" />
              <path d="M6 28c0-5.523 4.477-10 10-10s10 4.477 10 10" />
              <path d="M22 10l4-4M26 10l-4-4" />
            </svg>
            <span>社区互助</span>
          </div>
          <div class="hf-item">
            <svg width="28" height="28" viewBox="0 0 32 32" fill="none" stroke="currentColor" stroke-width="1.5" class="hf-icon">
              <path d="M16 4l3.09 6.26L26 11.27l-5 4.87L22.18 23 16 19.77 9.82 23 11 16.14l-5-4.87 6.91-1.01L16 4z" />
            </svg>
            <span>知识沉淀</span>
          </div>
        </div>
      </div>
      <div class="hero-visual">
        <div class="visual-bg"></div>
        <div class="visual-main">
          <div class="code-block">
            <div class="code-header">
              <span class="code-dot red"></span>
              <span class="code-dot yellow"></span>
              <span class="code-dot green"></span>
              <span class="code-filename">devquest.ai</span>
            </div>
            <div class="code-body">
              <div class="code-line"><span class="ck">// AI 驱动的技术问答</span></div>
              <div class="code-line"><span class="ck">const</span> <span class="cv">solution</span> = <span class="ck">await</span> <span class="cf">askAI</span>({</div>
              <div class="code-line indent"><span class="cp">question</span>: <span class="cs">"如何优化 React 性能？"</span>,</div>
              <div class="code-line indent"><span class="cp">context</span>: <span class="cs">"大型 SPA 应用"</span></div>
              <div class="code-line">});</div>
              <div class="code-line mt"><span class="ck">const</span> <span class="cv">answer</span> = solution.<span class="cf">bestAnswer</span>();</div>
              <div class="code-line"><span class="cv">answer</span>.<span class="cf">upvote</span>(); <span class="ck">// ✓ 有用!</span></div>
            </div>
          </div>
        </div>
        <div class="visual-badge">
          <div class="vb-inner">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="vb-icon">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" />
            </svg>
            <span>已解决 <strong>2,847</strong> 个技术问题</span>
          </div>
        </div>
      </div>
    </section>

    <section class="stats-bar">
      <div class="stats-inner">
        <div class="stat-item">
          <span class="stat-num">{{ communityStats.questions || '1,280' }}</span>
          <span class="stat-label">技术问题</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-num">{{ communityStats.users || '560' }}</span>
          <span class="stat-label">活跃开发者</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-num">{{ communityStats.answers || '3,450' }}</span>
          <span class="stat-label">优质回答</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-num">98%</span>
          <span class="stat-label">问题解决率</span>
        </div>
      </div>
    </section>

    <section class="hot-section">
      <div class="section-container">
        <div class="content-grid">
          <div class="main-col">
            <div class="section-block">
              <div class="section-header">
                <div>
                  <span class="section-eyebrow">Trending Topics</span>
                  <h2 class="section-title">热门问题</h2>
                </div>
                <button class="link-btn" @click="router.push('/questions')">
                  查看全部
                  <span class="link-line"></span>
                </button>
              </div>
              <div v-loading="loading.hotQuestions" class="question-list">
                <div
                  v-for="(question, index) in hotQuestions"
                  :key="question.id"
                  class="q-card"
                  @click="router.push(`/questions/${question.id}`)"
                >
                  <div class="q-rank" :class="{ top: index < 3 }">
                    {{ index + 1 }}
                  </div>
                  <div class="q-body">
                    <h3 class="q-title">{{ question.title }}</h3>
                    <p class="q-excerpt" v-if="question.content">
                      {{ question.content.substring(0, 100) }}...
                    </p>
                    <div class="q-meta">
                      <div class="q-tags">
                        <span
                          v-for="tag in (question.tags || []).slice(0, 3)"
                          :key="tag.id || tag"
                          class="q-tag"
                        >
                          {{ tag.name || tag }}
                        </span>
                      </div>
                      <div class="q-info">
                        <span class="qi-item">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                          {{ question.viewCount || 0 }}
                        </span>
                        <span class="qi-item">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                          {{ question.answerCount || 0 }}
                        </span>
                        <span class="qi-time">{{ formatTime(question.createdAt || question.createTime) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-if="!loading.hotQuestions && hotQuestions.length === 0" class="empty-box">
                  <p>暂无热门问题</p>
                </div>
              </div>
            </div>

            <div class="section-block">
              <div class="section-header">
                <div>
                  <span class="section-eyebrow">Latest Questions</span>
                  <h2 class="section-title">最新问题</h2>
                </div>
                <button class="link-btn" @click="router.push('/questions')">
                  查看全部
                  <span class="link-line"></span>
                </button>
              </div>
              <div v-loading="loading.latestQuestions" class="question-list">
                <div
                  v-for="question in latestQuestions"
                  :key="question.id"
                  class="q-card"
                  @click="router.push(`/questions/${question.id}`)"
                >
                  <div class="q-body">
                    <h3 class="q-title">{{ question.title }}</h3>
                    <div class="q-meta">
                      <div class="q-tags">
                        <span
                          v-for="tag in (question.tags || []).slice(0, 3)"
                          :key="tag.id || tag"
                          class="q-tag"
                        >
                          {{ tag.name || tag }}
                        </span>
                      </div>
                      <div class="q-info">
                        <span class="qi-item">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                          {{ question.viewCount || 0 }}
                        </span>
                        <span class="qi-item">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                          {{ question.answerCount || 0 }}
                        </span>
                        <span class="qi-time">{{ formatTime(question.createdAt || question.createTime) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-if="!loading.latestQuestions && latestQuestions.length === 0" class="empty-box">
                  <p>暂无问题，快来提第一个吧</p>
                </div>
              </div>
            </div>
          </div>

          <aside class="side-col">
            <div class="side-card">
              <h3 class="side-title">热门标签</h3>
              <div class="side-divider"></div>
              <div v-loading="loading.hotTags" class="tag-cloud">
                <span
                  v-for="tag in hotTags"
                  :key="tag.id"
                  class="cloud-tag"
                  @click="router.push(`/questions?tag=${tag.id}`)"
                >
                  {{ tag.name }}
                </span>
              </div>
            </div>

            <div class="side-card ai-side-card">
              <div class="ai-head">
                <div class="ai-icon-box">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" />
                  </svg>
                </div>
                <h3 class="side-title">AI 智能助手</h3>
              </div>
              <p class="ai-desc">遇到难题？AI 助手可以帮你分析代码、解答技术问题、提供学习建议</p>
              <button class="ai-cta" @click="router.push('/ai')">
                开始对话
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7" /></svg>
              </button>
            </div>

            <div class="side-card">
              <h3 class="side-title">社区统计</h3>
              <div class="side-divider"></div>
              <div class="comm-stats">
                <div class="cs-row">
                  <span class="cs-label">问题总数</span>
                  <span class="cs-val">{{ communityStats.questions || 0 }}</span>
                </div>
                <div class="cs-row">
                  <span class="cs-label">回答总数</span>
                  <span class="cs-val">{{ communityStats.answers || 0 }}</span>
                </div>
                <div class="cs-row">
                  <span class="cs-label">注册用户</span>
                  <span class="cs-val">{{ communityStats.users || 0 }}</span>
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
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { getHotQuestions, getQuestionList } from '@/api/question'
import { getHotTags } from '@/api/tag'
import request from '@/utils/request'

const router = useRouter()

const hotQuestions = ref([])
const latestQuestions = ref([])
const hotTags = ref([])
const communityStats = ref({})
const loading = reactive({
  hotQuestions: false,
  hotTags: false,
  latestQuestions: false
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  if (diff < 2592000000) return `${Math.floor(diff / 86400000)} 天前`
  return date.toLocaleDateString()
}

const fetchHotQuestions = async () => {
  loading.hotQuestions = true
  try {
    const res = await getHotQuestions({ page: 1, size: 5 })
    hotQuestions.value = res.data.list || []
  } catch (error) {
    console.error('获取热门问题失败:', error)
  } finally {
    loading.hotQuestions = false
  }
}

const fetchLatestQuestions = async () => {
  loading.latestQuestions = true
  try {
    const res = await getQuestionList({ page: 1, size: 5 })
    latestQuestions.value = res.data.list || []
  } catch (error) {
    console.error('获取最新问题失败:', error)
  } finally {
    loading.latestQuestions = false
  }
}

const fetchHotTags = async () => {
  loading.hotTags = true
  try {
    const res = await getHotTags()
    hotTags.value = res.data || []
  } catch (error) {
    console.error('获取热门标签失败:', error)
  } finally {
    loading.hotTags = false
  }
}

const fetchCommunityStats = async () => {
  try {
    const res = await request.get('/stats')
    const d = res.data || {}
    communityStats.value = {
      questions: d.totalQuestions || 0,
      answers: d.totalAnswers || 0,
      users: d.totalUsers || 0
    }
  } catch (error) {
    communityStats.value = { questions: 0, answers: 0, users: 0 }
  }
}

onMounted(() => {
  fetchHotQuestions()
  fetchLatestQuestions()
  fetchHotTags()
  fetchCommunityStats()
})
</script>

<style scoped>
.home {
  overflow: hidden;
}

.hero {
  display: flex;
  min-height: calc(100vh - 80px);
  padding-top: 80px;
  position: relative;
}

.hero-content {
  width: 50%;
  padding: 60px 40px 60px max(2rem, calc((100% - 1200px) / 2 + 3rem));
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  z-index: 2;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 28px;
  width: max-content;
}

.badge-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--caramel);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.hero-badge span:last-child {
  font-size: 0.75rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  color: var(--caramel);
  font-weight: 500;
}

.hero-title {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: clamp(2.2rem, 4vw, 3.5rem);
  font-weight: 400;
  line-height: 1.15;
  color: var(--forest);
  margin-bottom: 24px;
  letter-spacing: -0.02em;
}

.title-italic {
  font-style: italic;
  color: var(--caramel);
}

.hero-desc {
  font-size: 1.05rem;
  color: var(--forest-60);
  line-height: 1.8;
  margin-bottom: 36px;
  max-width: 460px;
  font-weight: 300;
}

.hero-actions {
  display: flex;
  gap: 14px;
  margin-bottom: 56px;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: var(--caramel);
  color: #fff;
  border: none;
  padding: 14px 32px;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 500;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-primary:hover {
  background: var(--caramel-light);
}

.btn-primary:hover svg {
  transform: translateX(3px);
}

.btn-primary svg {
  transition: transform 0.3s;
}

.btn-outline {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: transparent;
  color: var(--forest);
  border: 1.5px solid var(--forest-20);
  padding: 14px 32px;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 500;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-outline:hover {
  border-color: var(--caramel);
  color: var(--caramel);
}

.hero-features {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  padding-top: 32px;
  border-top: 1px solid var(--forest-10);
  max-width: 400px;
}

.hf-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.hf-icon {
  color: var(--forest);
  flex-shrink: 0;
}

.hf-item span {
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--forest-80);
  font-weight: 500;
}

.hero-visual {
  width: 50%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.visual-bg {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  background: var(--cream-dark);
  border-bottom-left-radius: 200px;
}

.visual-main {
  position: relative;
  z-index: 2;
  width: 380px;
}

.code-block {
  background: var(--forest);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 24px 64px rgba(26, 46, 38, 0.2);
}

.code-header {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 14px 18px;
  border-bottom: 1px solid var(--forest-light);
}

.code-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.code-dot.red { background: #ff5f57; }
.code-dot.yellow { background: #febc2e; }
.code-dot.green { background: #28c840; }

.code-filename {
  margin-left: 12px;
  font-size: 0.75rem;
  color: var(--cream-60);
  font-family: 'Courier New', monospace;
}

.code-body {
  padding: 20px;
  font-family: 'Courier New', monospace;
  font-size: 0.85rem;
  line-height: 1.9;
  color: var(--cream);
}

.code-line { white-space: nowrap; }
.code-line.indent { padding-left: 24px; }
.code-line.mt { margin-top: 12px; }
.ck { color: #c792ea; }
.cv { color: #82aaff; }
.cf { color: #82e8c6; }
.cp { color: #ffcb6b; }
.cs { color: #c3e88d; }

.visual-badge {
  position: absolute;
  bottom: -20px;
  left: -40px;
  z-index: 3;
}

.vb-inner {
  display: flex;
  align-items: center;
  gap: 10px;
  background: var(--forest);
  color: var(--cream);
  padding: 14px 22px;
  border-radius: 999px;
  font-size: 0.8rem;
  box-shadow: 0 12px 40px rgba(26, 46, 38, 0.25);
}

.vb-icon {
  color: var(--caramel);
}

.vb-inner strong {
  color: var(--caramel);
}

.stats-bar {
  background: var(--forest);
  padding: 40px 0;
}

.stats-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  padding: 0 2rem;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.stat-num {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 2rem;
  font-weight: 400;
  color: var(--cream);
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--cream-60);
  font-weight: 500;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: var(--cream-20);
}

.hot-section {
  padding: 80px 0 100px;
}

.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.content-grid {
  display: flex;
  gap: 36px;
}

.main-col {
  flex: 1;
  min-width: 0;
}

.side-col {
  width: 320px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-block {
  margin-bottom: 48px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 28px;
}

.section-eyebrow {
  display: block;
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 0.15em;
  color: var(--caramel);
  font-weight: 500;
  margin-bottom: 6px;
}

.section-title {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1.75rem;
  font-weight: 400;
  color: var(--forest);
  letter-spacing: -0.01em;
}

.link-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: none;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--caramel);
  font-weight: 500;
  cursor: pointer;
  padding: 0;
}

.link-line {
  display: inline-block;
  width: 28px;
  height: 1px;
  background: var(--caramel);
  transition: width 0.3s;
}

.link-btn:hover .link-line {
  width: 44px;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.q-card {
  display: flex;
  gap: 18px;
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

.q-rank {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: var(--cream-dark);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--forest-40);
  flex-shrink: 0;
}

.q-rank.top {
  background: var(--caramel);
  color: #fff;
}

.q-body {
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
  background: var(--cream-dark);
  border-radius: 999px;
  font-size: 0.7rem;
  color: var(--forest-80);
  font-weight: 500;
  letter-spacing: 0.03em;
  transition: all 0.2s;
}

.q-tag:hover {
  background: var(--caramel);
  color: #fff;
}

.q-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.qi-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.75rem;
  color: var(--forest-40);
}

.qi-time {
  font-size: 0.75rem;
  color: var(--forest-30);
}

.empty-box {
  text-align: center;
  padding: 48px 0;
}

.empty-box p {
  font-size: 0.9rem;
  color: var(--forest-40);
}

.side-card {
  padding: 28px;
  background: #fff;
  border: 1px solid var(--forest-08);
  border-radius: 20px;
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

.cloud-tag:hover {
  background: var(--caramel);
  color: #fff;
  border-color: var(--caramel);
  transform: translateY(-1px);
}

.ai-side-card {
  background: var(--forest);
  border-color: var(--forest);
}

.ai-head {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.ai-icon-box {
  width: 40px;
  height: 40px;
  background: var(--forest-light);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-icon-box svg {
  color: var(--caramel);
}

.ai-side-card .side-title {
  color: var(--cream);
}

.ai-desc {
  font-size: 0.85rem;
  color: var(--cream-70);
  line-height: 1.7;
  margin-bottom: 18px;
  font-weight: 300;
}

.ai-cta {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  justify-content: center;
  background: var(--caramel);
  color: #fff;
  border: none;
  padding: 12px 0;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 500;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.3s;
}

.ai-cta:hover {
  background: var(--caramel-light);
}

.ai-cta:hover svg {
  transform: translateX(3px);
}

.ai-cta svg {
  transition: transform 0.3s;
}

.comm-stats {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.cs-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid var(--forest-08);
}

.cs-row:last-child {
  border-bottom: none;
}

.cs-label {
  font-size: 0.85rem;
  color: var(--forest-60);
  font-weight: 400;
}

.cs-val {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1.15rem;
  font-weight: 500;
  color: var(--caramel);
}

@media (max-width: 1024px) {
  .hero { flex-direction: column; min-height: auto; }
  .hero-content {
    width: 100%;
    padding: 100px 2rem 48px;
    align-items: center;
    text-align: center;
  }
  .hero-badge { margin: 0 auto 28px; }
  .hero-desc { max-width: 100%; }
  .hero-actions { justify-content: center; }
  .hero-features { max-width: 100%; justify-items: center; }
  .hero-visual {
    width: 100%;
    padding: 40px 2rem 60px;
  }
  .visual-bg { border-radius: 80px 0 0 0; }
  .visual-main { width: 100%; max-width: 380px; }
  .visual-badge { left: 50%; transform: translateX(-50%); }
  .content-grid { flex-direction: column; }
  .side-col { width: 100%; }
}

@media (max-width: 640px) {
  .hero-title { font-size: 1.8rem; }
  .hero-actions { flex-direction: column; }
  .hero-features { grid-template-columns: 1fr; }
  .stats-inner { flex-wrap: wrap; gap: 24px; }
  .stat-divider { display: none; }
  .stat-item { width: 45%; }
  .q-meta { flex-direction: column; align-items: flex-start; gap: 10px; }
}
</style>
