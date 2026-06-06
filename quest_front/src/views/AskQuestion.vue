<template>
  <div class="ask-page">
    <section class="ask-hero">
      <div class="ask-hero-inner">
        <span class="section-eyebrow">Ask a Question</span>
        <h1 class="ask-title">提出问题</h1>
        <p class="ask-desc">清晰描述你的技术问题，帮助社区更好地为你解答</p>
      </div>
    </section>

    <section class="ask-body">
      <div class="ask-container">
        <div class="ask-grid">
          <div class="ask-main">
            <div class="card-lux">
              <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
                <div class="form-group">
                  <label class="form-label">问题标题</label>
                  <el-input v-model="form.title" placeholder="简洁清晰地描述你的问题" maxlength="100" show-word-limit size="large" />
                  <span class="form-hint">一个好的标题能帮助你更快获得解答</span>
                </div>
                <div class="form-group">
                  <label class="form-label">问题详情</label>
                  <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请详细描述你的问题，包括：&#10;1. 你想要实现什么&#10;2. 你尝试了哪些方法&#10;3. 出现了什么问题或错误" />
                  <span class="form-hint">至少 20 个字符，越详细越容易获得好的回答</span>
                </div>
                <div class="form-group">
                  <label class="form-label">选择标签</label>
                  <el-select v-model="form.tagIds" multiple placeholder="选择相关标签（最多5个）" :multiple-limit="5" size="large" style="width: 100%">
                    <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
                  </el-select>
                  <span class="form-hint">选择合适的标签能让相关领域的开发者更快看到你的问题</span>
                </div>
                <div class="form-actions">
                  <button type="button" class="btn-primary" :class="{ loading }" :disabled="loading" @click="handleSubmit">
                    {{ loading ? '发布中...' : '发布问题' }}
                  </button>
                  <button type="button" class="btn-outline" :disabled="draftLoading" @click="handleSaveDraft">
                    {{ draftLoading ? '保存中...' : (currentDraftId ? '更新草稿' : '保存草稿') }}
                  </button>
                  <button type="button" class="btn-outline" @click="router.back()">
                    取消
                  </button>
                </div>
              </el-form>
            </div>
          </div>

          <aside class="ask-side">
            <div class="card-lux side-tip">
              <h3 class="side-title">提问技巧</h3>
              <div class="side-divider"></div>
              <div class="tip-list">
                <div class="tip-item">
                  <div class="tip-num">1</div>
                  <div class="tip-body">
                    <strong>标题要具体</strong>
                    <p>用一句话概括你的问题，避免使用"求助"等模糊标题</p>
                  </div>
                </div>
                <div class="tip-item">
                  <div class="tip-num">2</div>
                  <div class="tip-body">
                    <strong>提供上下文</strong>
                    <p>说明你使用的语言、框架版本和运行环境</p>
                  </div>
                </div>
                <div class="tip-item">
                  <div class="tip-num">3</div>
                  <div class="tip-body">
                    <strong>展示你的尝试</strong>
                    <p>分享你已经尝试过的方案，避免重复建议</p>
                  </div>
                </div>
                <div class="tip-item">
                  <div class="tip-num">4</div>
                  <div class="tip-body">
                    <strong>使用代码块</strong>
                    <p>粘贴相关代码时使用格式化，方便阅读</p>
                  </div>
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { createQuestion } from '@/api/question'
import { getTags } from '@/api/tag'
import { saveDraft, getDraftDetail, updateDraft, deleteDraft } from '@/api/draft'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const loading = ref(false)
const draftLoading = ref(false)
const tags = ref([])
const currentDraftId = ref(null)

const form = reactive({
  title: '',
  content: '',
  tagIds: []
})

const rules = {
  title: [
    { required: true, message: '请输入问题标题', trigger: 'blur' },
    { min: 10, max: 100, message: '标题长度在10-100个字符之间', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入问题内容', trigger: 'blur' },
    { min: 20, message: '问题内容至少20个字符', trigger: 'blur' }
  ],
  tagIds: [
    { required: true, message: '请选择至少一个标签', trigger: 'change' }
  ]
}

const fetchTags = async () => {
  try {
    const res = await getTags({ page: 1, size: 100 })
    tags.value = res.data || []
  } catch (error) {
    console.error('获取标签列表失败:', error)
  }
}

const handleSaveDraft = async () => {
  if (!form.title && !form.content) {
    ElMessage.warning('请至少输入标题或内容')
    return
  }
  draftLoading.value = true
  try {
    const draftData = {
      title: form.title || '无标题草稿',
      content: form.content || '',
      tags: form.tagIds.join(','),
      type: 'question'
    }
    if (currentDraftId.value) {
      await updateDraft(currentDraftId.value, draftData)
      ElMessage.success('草稿已更新')
    } else {
      const res = await saveDraft(draftData)
      currentDraftId.value = res.data?.id
      ElMessage.success('草稿已保存')
    }
  } catch (error) {
    console.error('保存草稿失败:', error)
  } finally {
    draftLoading.value = false
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await createQuestion(form)
    ElMessage.success('问题发布成功')
    if (currentDraftId.value) {
      await deleteDraft(currentDraftId.value).catch(() => {})
    }
    router.push(`/questions/${res.data.id}`)
  } catch (error) {
    console.error('发布问题失败:', error)
  } finally {
    loading.value = false
  }
}

const loadDraft = async (draftId) => {
  try {
    const res = await getDraftDetail(draftId)
    const draft = res.data
    if (draft) {
      currentDraftId.value = draft.id
      form.title = draft.title || ''
      form.content = draft.content || ''
      if (draft.tags) {
        form.tagIds = draft.tags.split(',').map(Number).filter(n => !isNaN(n))
      }
    }
  } catch (error) {
    console.error('加载草稿失败:', error)
    ElMessage.warning('草稿加载失败')
  }
}

onMounted(async () => {
  await fetchTags()
  const draftId = route.query.draftId
  if (draftId) {
    await loadDraft(draftId)
  }
})
</script>

<style scoped>
.ask-page {
  overflow: hidden;
}

.ask-hero {
  background: var(--cream);
  padding: 100px 0 40px;
  text-align: center;
}

.ask-hero-inner {
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
  margin-bottom: 10px;
}

.ask-title {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 2.2rem;
  font-weight: 400;
  color: var(--forest);
  margin-bottom: 10px;
}

.ask-desc {
  font-size: 0.95rem;
  color: var(--forest-50);
  font-weight: 300;
}

.ask-body {
  padding: 48px 0 100px;
}

.ask-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 2rem;
}

.ask-grid {
  display: flex;
  gap: 36px;
}

.ask-main {
  flex: 1;
  min-width: 0;
}

.ask-side {
  width: 320px;
  flex-shrink: 0;
}

.card-lux {
  background: #fff;
  border: 1px solid var(--forest-08);
  border-radius: 20px;
  padding: 32px;
}

.form-group {
  margin-bottom: 28px;
}

.form-label {
  display: block;
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--forest);
  margin-bottom: 8px;
  letter-spacing: 0.02em;
}

.form-hint {
  display: block;
  font-size: 0.75rem;
  color: var(--forest-40);
  margin-top: 6px;
  line-height: 1.5;
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 8px;
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

.btn-primary.loading {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-outline {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: transparent;
  color: var(--forest);
  border: 1.5px solid var(--forest-20);
  padding: 14px 28px;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-outline:hover {
  border-color: var(--caramel);
  color: var(--caramel);
}

.side-tip {
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

.tip-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.tip-item {
  display: flex;
  gap: 14px;
}

.tip-num {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--cream-dark);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 700;
  color: var(--caramel);
  flex-shrink: 0;
}

.tip-body strong {
  display: block;
  font-size: 0.85rem;
  color: var(--forest);
  margin-bottom: 4px;
  font-weight: 600;
}

.tip-body p {
  font-size: 0.78rem;
  color: var(--forest-50);
  line-height: 1.6;
}

@media (max-width: 1024px) {
  .ask-grid { flex-direction: column; }
  .ask-side { width: 100%; }
  .side-tip { position: static; }
}
</style>
