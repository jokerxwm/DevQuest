<template>
  <div class="drafts container">
    <div class="card">
      <div class="page-header">
        <h2>我的草稿箱</h2>
      </div>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="问题草稿" name="questions">
          <div v-loading="loading.questions">
            <div v-for="draft in questionDrafts" :key="draft.id" class="draft-item">
              <div class="draft-content" @click="editDraft('question', draft)">
                <h3 class="draft-title">{{ draft.title || '无标题草稿' }}</h3>
                <p class="draft-preview">{{ draft.content?.substring(0, 120) }}...</p>
                <span class="draft-time">保存于 {{ formatTime(draft.updatedAt) }}</span>
              </div>
              <div class="draft-actions">
                <el-button text type="primary" @click="editDraft('question', draft)">编辑</el-button>
                <el-popconfirm title="确定删除此草稿？" @confirm="handleDelete(draft.id, 'question')">
                  <template #reference>
                    <el-button text type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
              </div>
            </div>
            <el-empty v-if="!loading.questions && questionDrafts.length === 0" description="暂无问题草稿">
              <el-button type="primary" @click="router.push('/ask')">去提问</el-button>
            </el-empty>
          </div>
        </el-tab-pane>

        <el-tab-pane label="回答草稿" name="answers">
          <div v-loading="loading.answers">
            <div v-for="draft in answerDrafts" :key="draft.id" class="draft-item">
              <div class="draft-content" @click="editDraft('answer', draft)">
                <h3 class="draft-title">{{ draft.questionTitle || '回答草稿' }}</h3>
                <p class="draft-preview">{{ draft.content?.substring(0, 120) }}...</p>
                <span class="draft-time">保存于 {{ formatTime(draft.updatedAt) }}</span>
              </div>
              <div class="draft-actions">
                <el-button text type="primary" @click="editDraft('answer', draft)">编辑</el-button>
                <el-popconfirm title="确定删除此草稿？" @confirm="handleDelete(draft.id, 'answer')">
                  <template #reference>
                    <el-button text type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
              </div>
            </div>
            <el-empty v-if="!loading.answers && answerDrafts.length === 0" description="暂无回答草稿">
              <el-button type="primary" @click="router.push('/questions')">去浏览问题</el-button>
            </el-empty>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getDrafts, deleteDraft } from '@/api/draft'
import { ElMessage } from 'element-plus'

const router = useRouter()

const activeTab = ref('questions')
const questionDrafts = ref([])
const answerDrafts = ref([])
const loading = reactive({
  questions: false,
  answers: false
})

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
  try {
    const res = await getDrafts({ type: 'question', page: 1, size: 50 })
    questionDrafts.value = res.data.list || []
  } catch (error) {
    console.error('获取问题草稿失败:', error)
  } finally {
    loading.questions = false
  }
}

const fetchAnswerDrafts = async () => {
  loading.answers = true
  try {
    const res = await getDrafts({ type: 'answer', page: 1, size: 50 })
    answerDrafts.value = res.data.list || []
  } catch (error) {
    console.error('获取回答草稿失败:', error)
  } finally {
    loading.answers = false
  }
}

const editDraft = (type, draft) => {
  if (type === 'question') {
    router.push({ path: '/ask', query: { draftId: draft.id } })
  } else {
    router.push({ path: `/questions/${draft.questionId}/answer`, query: { draftId: draft.id } })
  }
}

const handleDelete = async (draftId, type) => {
  try {
    await deleteDraft(draftId)
    ElMessage.success('草稿已删除')
    if (type === 'question') {
      fetchQuestionDrafts()
    } else {
      fetchAnswerDrafts()
    }
  } catch (error) {
    console.error('删除草稿失败:', error)
  }
}

onMounted(() => {
  fetchQuestionDrafts()
  fetchAnswerDrafts()
})
</script>

<style scoped>
.drafts {
  padding: 24px 0;
  max-width: 900px;
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.page-header h2 {
  font-size: 24px;
}

.draft-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
  transition: background 0.3s;
}

.draft-item:hover {
  background: #fafafa;
}

.draft-content {
  flex: 1;
  cursor: pointer;
  min-width: 0;
}

.draft-title {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
}

.draft-preview {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  line-height: 1.5;
}

.draft-time {
  font-size: 12px;
  color: #999;
}

.draft-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
  margin-left: 16px;
}
</style>
