<template>
  <div class="answer-editor container">
    <div class="editor-header card">
      <div class="question-info">
        <h2>撰写回答</h2>
        <p class="question-title" @click="router.push(`/questions/${route.params.id}`)">
          问题：{{ question.title }}
        </p>
      </div>
    </div>

    <div class="editor-container">
      <div class="editor-main card">
        <div class="editor-toolbar">
          <el-radio-group v-model="editorMode" size="small">
            <el-radio-button label="edit">编辑</el-radio-button>
            <el-radio-button label="preview">预览</el-radio-button>
          </el-radio-group>
          <el-button size="small" type="primary" plain @click="showAiPolish = true">
            AI 润色
          </el-button>
        </div>

        <div v-if="editorMode === 'edit'">
          <el-input
            v-model="content"
            type="textarea"
            :rows="18"
            placeholder="请使用 Markdown 格式撰写你的回答..."
          />
        </div>
        <div v-else class="preview-area">
          <div class="markdown-preview">{{ content || '暂无内容' }}</div>
        </div>

        <div class="editor-footer">
          <el-button @click="router.back()">取消</el-button>
          <el-button @click="handleSaveDraft">保存草稿</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交回答</el-button>
        </div>
      </div>

      <div class="editor-sidebar">
        <div class="card sidebar-card">
          <h3>回答提示</h3>
          <ul class="tips-list">
            <li>请确保回答与问题相关</li>
            <li>提供详细的代码示例</li>
            <li>解释你的解决方案思路</li>
            <li>支持 Markdown 语法</li>
          </ul>
        </div>

        <div class="card sidebar-card">
          <h3>问题详情</h3>
          <div class="info-item">
            <span class="label">回答数</span>
            <span class="value">{{ question.answerCount || 0 }}</span>
          </div>
          <div class="info-item">
            <span class="label">浏览数</span>
            <span class="value">{{ question.viewCount || 0 }}</span>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="showAiPolish" title="AI 润色" width="700px" destroy-on-close>
      <AiPolish :original="content" @replace="handleAiReplace" @close="showAiPolish = false" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getQuestionDetail } from '@/api/question'
import { createAnswer } from '@/api/answer'
import { saveDraft, getDraftDetail, updateDraft } from '@/api/draft'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import AiPolish from '@/components/AiPolish.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const question = ref({})
const content = ref('')
const editorMode = ref('edit')
const submitting = ref(false)
const showAiPolish = ref(false)
const draftId = ref(null)

const fetchQuestion = async () => {
  try {
    const res = await getQuestionDetail(route.params.id)
    question.value = res.data
  } catch (error) {
    console.error('获取问题详情失败:', error)
  }
}

const fetchDraft = async () => {
  if (route.query.draftId) {
    try {
      const res = await getDraftDetail(route.query.draftId)
      content.value = res.data.content || ''
      draftId.value = route.query.draftId
    } catch (error) {
      console.error('获取草稿失败:', error)
    }
  }
}

const handleSaveDraft = async () => {
  if (!content.value.trim()) {
    ElMessage.warning('请输入回答内容')
    return
  }
  try {
    const data = {
      type: 'answer',
      questionId: Number(route.params.id),
      content: content.value,
      questionTitle: question.value.title
    }
    if (draftId.value) {
      await updateDraft(draftId.value, data)
    } else {
      const res = await saveDraft(data)
      draftId.value = res.data?.id
    }
    ElMessage.success('草稿已保存')
  } catch (error) {
    console.error('保存草稿失败:', error)
  }
}

const handleAiReplace = (text) => {
  content.value = text
  showAiPolish.value = false
}

const handleSubmit = async () => {
  if (!content.value.trim()) {
    ElMessage.warning('请输入回答内容')
    return
  }
  submitting.value = true
  try {
    await createAnswer(route.params.id, { content: content.value })
    ElMessage.success('回答提交成功')
    router.push(`/questions/${route.params.id}`)
  } catch (error) {
    console.error('提交回答失败:', error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchQuestion()
  fetchDraft()
})
</script>

<style scoped>
.answer-editor {
  padding: 24px 0;
}

.editor-header {
  margin-bottom: 16px;
}

.editor-header h2 {
  font-size: 20px;
  margin-bottom: 8px;
}

.question-title {
  color: #1e80ff;
  cursor: pointer;
  font-size: 14px;
}

.question-title:hover {
  text-decoration: underline;
}

.editor-container {
  display: flex;
  gap: 24px;
}

.editor-main {
  flex: 1;
  min-width: 0;
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.preview-area {
  min-height: 400px;
  padding: 16px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.markdown-preview {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}

.editor-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.editor-sidebar {
  width: 280px;
  flex-shrink: 0;
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

.tips-list {
  padding-left: 16px;
  color: #666;
  font-size: 14px;
  line-height: 2;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
}

.label {
  color: #999;
}

.value {
  color: #333;
}
</style>
