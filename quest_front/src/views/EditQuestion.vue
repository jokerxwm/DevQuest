<template>
  <div class="eq-page">
    <section class="eq-hero">
      <div class="eq-hero-inner">
        <span class="section-eyebrow">Edit Question</span>
        <h1 class="eq-title">编辑问题</h1>
      </div>
    </section>

    <section class="eq-body">
      <div class="eq-container">
        <div class="card-lux">
          <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
            <div class="form-group">
              <label class="form-label">问题标题</label>
              <el-input v-model="form.title" placeholder="请输入问题标题" maxlength="100" show-word-limit size="large" />
            </div>
            <div class="form-group">
              <label class="form-label">问题内容</label>
              <el-input v-model="form.content" type="textarea" :rows="12" placeholder="请详细描述你的问题" />
            </div>
            <div class="form-group">
              <label class="form-label">选择标签</label>
              <el-select v-model="form.tagIds" multiple placeholder="选择相关标签（最多5个）" :multiple-limit="5" size="large" style="width: 100%">
                <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
              </el-select>
            </div>
            <div class="form-actions">
              <button type="button" class="btn-primary" :disabled="loading" @click="handleSubmit">{{ loading ? '保存中...' : '保存修改' }}</button>
              <button type="button" class="btn-outline" @click="router.back()">取消</button>
            </div>
          </el-form>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getQuestionDetail, updateQuestion } from '@/api/question'
import { getTags } from '@/api/tag'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const tags = ref([])
const form = reactive({ title: '', content: '', tagIds: [] })
const rules = { title: [{ required: true, message: '请输入问题标题', trigger: 'blur' }], content: [{ required: true, message: '请输入问题内容', trigger: 'blur' }] }

const fetchQuestion = async () => {
  try { const res = await getQuestionDetail(route.params.id); const q = res.data; form.title = q.title; form.content = q.content; form.tagIds = (q.tags || []).map(t => t.id) }
  catch (e) { console.error(e) }
}

const fetchTags = async () => { try { const res = await getTags({ page: 1, size: 100 }); tags.value = res.data || [] } catch (e) { console.error(e) } }

const handleSubmit = async () => {
  await formRef.value.validate(); loading.value = true
  try { await updateQuestion(route.params.id, form); ElMessage.success('修改成功'); router.push(`/questions/${route.params.id}`) }
  catch (e) { console.error(e) }
  finally { loading.value = false }
}

onMounted(() => { fetchQuestion(); fetchTags() })
</script>

<style scoped>
.eq-page { overflow: hidden; }
.eq-hero { background: var(--cream); padding: 100px 0 40px; text-align: center; border-bottom: 1px solid var(--border-light); }
.eq-hero-inner { max-width: 1200px; margin: 0 auto; padding: 0 2rem; }
.section-eyebrow { display: block; font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.15em; color: var(--caramel); font-weight: 500; margin-bottom: 8px; }
.eq-title { font-family: 'Noto Serif SC', serif; font-size: 2rem; font-weight: 400; color: var(--forest); }
.eq-body { padding: 48px 0 100px; }
.eq-container { max-width: 800px; margin: 0 auto; padding: 0 2rem; }
.card-lux { background: #fff; border: 1px solid var(--forest-08); border-radius: 20px; padding: 32px; }
.form-group { margin-bottom: 24px; }
.form-label { display: block; font-size: 0.85rem; font-weight: 600; color: var(--forest); margin-bottom: 8px; }
.form-actions { display: flex; gap: 12px; padding-top: 8px; }
.btn-primary { background: var(--caramel); color: #fff; border: none; padding: 14px 32px; border-radius: 999px; font-size: 0.85rem; font-weight: 500; letter-spacing: 0.08em; text-transform: uppercase; cursor: pointer; transition: background 0.3s; }
.btn-primary:hover { background: var(--caramel-light); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-outline { background: transparent; color: var(--forest); border: 1.5px solid var(--forest-20); padding: 14px 28px; border-radius: 999px; font-size: 0.85rem; font-weight: 500; cursor: pointer; transition: all 0.3s; }
.btn-outline:hover { border-color: var(--caramel); color: var(--caramel); }
</style>
