<template>
  <div class="ae-page">
    <section class="ae-hero">
      <div class="ae-hero-inner">
        <span class="section-eyebrow">Write Answer</span>
        <h1 class="ae-title">撰写回答</h1>
      </div>
    </section>

    <section class="ae-body">
      <div class="ae-container">
        <div class="ae-grid">
          <div class="ae-main">
            <div class="card-lux">
              <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
                <div class="form-group">
                  <label class="form-label">回答内容</label>
                  <el-input v-model="form.content" type="textarea" :rows="15" placeholder="分享你的知识和经验，帮助提问者解决问题..." />
                  <span class="form-hint">至少 10 个字符，提供详细的解决方案和代码示例</span>
                </div>
                <div class="form-actions">
                  <button type="button" class="btn-primary" :disabled="loading" @click="handleSubmit">{{ loading ? '提交中...' : '提交回答' }}</button>
                  <button type="button" class="btn-outline" @click="router.back()">取消</button>
                </div>
              </el-form>
            </div>
          </div>

          <aside class="ae-side">
            <div class="card-lux side-card">
              <h3 class="side-title">回答技巧</h3>
              <div class="side-divider"></div>
              <div class="tip-list">
                <div class="tip-item"><div class="tip-dot"></div><span>提供完整的解决方案，而不仅仅是提示</span></div>
                <div class="tip-item"><div class="tip-dot"></div><span>使用代码块格式化代码</span></div>
                <div class="tip-item"><div class="tip-dot"></div><span>解释你的思路和原理</span></div>
                <div class="tip-item"><div class="tip-dot"></div><span>测试你的代码确保可行</span></div>
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
import { useRoute, useRouter } from 'vue-router'
import { getQuestionDetail } from '@/api/question'
import { createAnswer } from '@/api/answer'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const question = ref({})
const form = reactive({ content: '' })
const rules = { content: [{ required: true, message: '请输入回答内容', trigger: 'blur' }, { min: 10, message: '回答内容至少10个字符', trigger: 'blur' }] }

const fetchQuestion = async () => { try { const res = await getQuestionDetail(route.params.id); question.value = res.data } catch (e) { console.error(e) } }

const handleSubmit = async () => {
  await formRef.value.validate(); loading.value = true
  try { await createAnswer(route.params.id, form); ElMessage.success('回答提交成功'); router.push(`/questions/${route.params.id}`) }
  catch (e) { console.error(e) }
  finally { loading.value = false }
}

onMounted(() => { fetchQuestion() })
</script>

<style scoped>
.ae-page { overflow: hidden; }
.ae-hero { background: var(--cream); padding: 100px 0 40px; text-align: center; border-bottom: 1px solid var(--border-light); }
.ae-hero-inner { max-width: 1200px; margin: 0 auto; padding: 0 2rem; }
.section-eyebrow { display: block; font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.15em; color: var(--caramel); font-weight: 500; margin-bottom: 8px; }
.ae-title { font-family: 'Noto Serif SC', serif; font-size: 2rem; font-weight: 400; color: var(--forest); }
.ae-body { padding: 48px 0 100px; }
.ae-container { max-width: 1100px; margin: 0 auto; padding: 0 2rem; }
.ae-grid { display: flex; gap: 36px; }
.ae-main { flex: 1; min-width: 0; }
.ae-side { width: 300px; flex-shrink: 0; }
.card-lux { background: #fff; border: 1px solid var(--forest-08); border-radius: 20px; padding: 32px; }
.form-group { margin-bottom: 24px; }
.form-label { display: block; font-size: 0.85rem; font-weight: 600; color: var(--forest); margin-bottom: 8px; }
.form-hint { display: block; font-size: 0.75rem; color: var(--forest-40); margin-top: 6px; }
.form-actions { display: flex; gap: 12px; }
.btn-primary { background: var(--caramel); color: #fff; border: none; padding: 14px 32px; border-radius: 999px; font-size: 0.85rem; font-weight: 500; letter-spacing: 0.08em; text-transform: uppercase; cursor: pointer; transition: background 0.3s; }
.btn-primary:hover { background: var(--caramel-light); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-outline { background: transparent; color: var(--forest); border: 1.5px solid var(--forest-20); padding: 14px 28px; border-radius: 999px; font-size: 0.85rem; font-weight: 500; cursor: pointer; transition: all 0.3s; }
.btn-outline:hover { border-color: var(--caramel); color: var(--caramel); }
.side-card { position: sticky; top: 100px; }
.side-title { font-family: 'Noto Serif SC', serif; font-size: 1.1rem; font-weight: 500; color: var(--forest); }
.side-divider { width: 32px; height: 1.5px; background: var(--caramel); margin: 12px 0 18px; }
.tip-list { display: flex; flex-direction: column; gap: 14px; }
.tip-item { display: flex; align-items: flex-start; gap: 10px; font-size: 0.82rem; color: var(--forest-70); line-height: 1.5; }
.tip-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--caramel); flex-shrink: 0; margin-top: 6px; }
@media (max-width: 1024px) { .ae-grid { flex-direction: column; } .ae-side { width: 100%; } .side-card { position: static; } }
</style>
