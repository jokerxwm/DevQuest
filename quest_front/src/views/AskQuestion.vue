<template>
  <div class="ask-question container">
    <div class="card">
      <h2>提出问题</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
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
          <el-button type="primary" size="large" :loading="loading" @click="handleSubmit">发布问题</el-button>
          <el-button size="large" @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { createQuestion } from '@/api/question'
import { getTags } from '@/api/tag'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
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
  loading.value = true
  try {
    const res = await createQuestion(form)
    ElMessage.success('问题发布成功')
    router.push(`/questions/${res.data.id}`)
  } catch (error) {
    console.error('发布问题失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchTags()
})
</script>

<style scoped>
.ask-question {
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
