<template>
  <div class="drafts-page">
    <section class="drafts-hero">
      <div class="drafts-hero-inner">
        <span class="section-eyebrow">Drafts</span>
        <h1 class="drafts-title">我的草稿箱</h1>
      </div>
    </section>

    <section class="drafts-body">
      <div class="drafts-container">
        <div class="card-lux">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="问题草稿" name="questions">
              <div v-loading="loading.questions" class="draft-list">
                <div v-for="draft in questionDrafts" :key="draft.id" class="draft-card">
                  <div class="draft-content" @click="editDraft('question', draft)">
                    <h3 class="draft-title">{{ draft.title || '无标题草稿' }}</h3>
                    <p class="draft-preview">{{ draft.content?.substring(0, 120) }}...</p>
                    <span class="draft-time">保存于 {{ formatTime(draft.updatedAt) }}</span>
                  </div>
                  <div class="draft-actions">
                    <button class="act-edit" @click="editDraft('question', draft)">编辑</button>
                    <button class="act-del" @click="handleDelete(draft.id, 'question')">删除</button>
                  </div>
                </div>
                <div v-if="!loading.questions && questionDrafts.length === 0" class="empty-box">
                  <p>暂无问题草稿</p>
                  <button class="btn-caramel" @click="router.push('/ask')">去提问</button>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="回答草稿" name="answers">
              <div v-loading="loading.answers" class="draft-list">
                <div v-for="draft in answerDrafts" :key="draft.id" class="draft-card">
                  <div class="draft-content" @click="editDraft('answer', draft)">
                    <h3 class="draft-title">{{ draft.questionTitle || '回答草稿' }}</h3>
                    <p class="draft-preview">{{ draft.content?.substring(0, 120) }}...</p>
                    <span class="draft-time">保存于 {{ formatTime(draft.updatedAt) }}</span>
                  </div>
                  <div class="draft-actions">
                    <button class="act-edit" @click="editDraft('answer', draft)">编辑</button>
                    <button class="act-del" @click="handleDelete(draft.id, 'answer')">删除</button>
                  </div>
                </div>
                <div v-if="!loading.answers && answerDrafts.length === 0" class="empty-box">
                  <p>暂无回答草稿</p>
                  <button class="btn-caramel" @click="router.push('/questions')">去浏览问题</button>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getDrafts, deleteDraft } from '@/api/draft'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const activeTab = ref('questions')
const questionDrafts = ref([])
const answerDrafts = ref([])
const loading = reactive({ questions: false, answers: false })

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

const fetchQuestionDrafts = async () => {
  loading.questions = true
  try { const res = await getDrafts({ type: 'question', page: 1, size: 50 }); questionDrafts.value = res.data.list || [] } catch (e) { console.error(e) }
  finally { loading.questions = false }
}

const fetchAnswerDrafts = async () => {
  loading.answers = true
  try { const res = await getDrafts({ type: 'answer', page: 1, size: 50 }); answerDrafts.value = res.data.list || [] } catch (e) { console.error(e) }
  finally { loading.answers = false }
}

const editDraft = (type, draft) => {
  if (type === 'question') router.push({ path: '/ask', query: { draftId: draft.id } })
  else router.push({ path: `/questions/${draft.questionId}/answer`, query: { draftId: draft.id } })
}

const handleDelete = async (draftId, type) => {
  try {
    await ElMessageBox.confirm('确定删除此草稿？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await deleteDraft(draftId)
    ElMessage.success('草稿已删除')
    if (type === 'question') fetchQuestionDrafts()
    else fetchAnswerDrafts()
  } catch (error) { if (error !== 'cancel') console.error(error) }
}

onMounted(() => { fetchQuestionDrafts(); fetchAnswerDrafts() })
</script>

<style scoped>
.drafts-page { overflow: hidden; }
.drafts-hero { background: var(--cream); padding: 100px 0 40px; text-align: center; border-bottom: 1px solid var(--border-light); }
.drafts-hero-inner { max-width: 1200px; margin: 0 auto; padding: 0 2rem; }
.section-eyebrow { display: block; font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.15em; color: var(--caramel); font-weight: 500; margin-bottom: 8px; }
.drafts-title { font-family: 'Noto Serif SC', 'Playfair Display', serif; font-size: 2rem; font-weight: 400; color: var(--forest); }
.drafts-body { padding: 48px 0 100px; }
.drafts-container { max-width: 900px; margin: 0 auto; padding: 0 2rem; }
.card-lux { background: #fff; border: 1px solid var(--forest-08); border-radius: 20px; padding: 32px; }

.draft-list { display: flex; flex-direction: column; gap: 12px; margin-top: 16px; }

.draft-card {
  display: flex; justify-content: space-between; align-items: flex-start;
  padding: 18px; border: 1px solid var(--forest-08); border-radius: 14px; transition: all 0.3s;
}
.draft-card:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(26, 46, 38, 0.06); }

.draft-content { flex: 1; cursor: pointer; min-width: 0; }
.draft-title { font-family: 'Noto Serif SC', 'Playfair Display', serif; font-size: 1rem; color: var(--forest); margin-bottom: 6px; }
.draft-preview { font-size: 0.82rem; color: var(--forest-60); line-height: 1.7; margin-bottom: 8px; }
.draft-time { font-size: 0.72rem; color: var(--forest-40); }

.draft-actions { display: flex; gap: 6px; flex-shrink: 0; margin-left: 16px; }
.act-edit, .act-del {
  border: none; padding: 6px 14px; border-radius: 999px; font-size: 0.72rem; font-weight: 500;
  cursor: pointer; transition: all 0.2s;
}
.act-edit { background: var(--cream-dark); color: var(--forest); }
.act-edit:hover { background: var(--caramel); color: #fff; }
.act-del { background: transparent; color: var(--forest-40); }
.act-del:hover { color: #e74c3c; }

.empty-box { text-align: center; padding: 40px 0; }
.empty-box p { font-size: 0.9rem; color: var(--forest-40); margin-bottom: 16px; }
.btn-caramel { background: var(--caramel); color: #fff; border: none; padding: 10px 24px; border-radius: 999px; font-size: 0.8rem; font-weight: 500; cursor: pointer; transition: background 0.3s; }
.btn-caramel:hover { background: var(--caramel-light); }
</style>
