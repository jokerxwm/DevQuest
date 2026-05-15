<template>
  <div class="edit-question container">
    <div class="card">
      <h2>编辑问题</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" v-loading="loadingQuestion">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入问题标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请详细描述你的问题..." />
        </el-form-item>
        <el-form-item label="标签" prop="tagIds">
          <el-select v-model="form.tagIds" multiple placeholder="请选择标签（最多5个）" :multiple-limit="5">
            <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">保存修改</el-button>
          <el-button size="large" @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
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
const loadingQuestion = ref(false)
const submitting = ref(false)
const tags = ref([])

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

const fetchQuestion = async () => {
  loadingQuestion.value = true
  try {
    const res = await getQuestionDetail(route.params.id)
    const question = res.data
    form.title = question.title
    form.content = question.content
    form.tagIds = (question.tags || []).map(tag => tag.id)
  } catch (error) {
    console.error('获取问题详情失败:', error)
    ElMessage.error('获取问题详情失败')
    router.back()
  } finally {
    loadingQuestion.value = false
  }
}

const fetchTags = async () => {
  try {
    const res = await getTags({ page: 1, size: 100 })
    tags.value = res.data.list || []
  } catch (error) {
    console.error('获取标签列表失败:', error)
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    await updateQuestion(route.params.id, form)
    ElMessage.success('问题修改成功')
    router.push(`/questions/${route.params.id}`)
  } catch (error) {
    console.error('修改问题失败:', error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchQuestion()
  fetchTags()
})
</script>

<style scoped>
.edit-question {
  padding: 24px 0;
  max-width: 800px;
}

.card h2 {
  font-size: 24px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}
</style>
