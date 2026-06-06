<template>
  <el-dialog v-model="visible" title="举报" width="500px" destroy-on-close @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item label="举报原因" prop="reason">
        <el-radio-group v-model="form.reason">
          <el-radio label="spam">垃圾内容/广告</el-radio>
          <el-radio label="abuse">辱骂/骚扰</el-radio>
          <el-radio label="plagiarism">抄袭/侵权</el-radio>
          <el-radio label="misinformation">虚假信息</el-radio>
          <el-radio label="other">其他</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="补充说明" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          placeholder="请补充说明举报原因（可选）..."
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">提交举报</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { submitReport } from '@/api/report'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  targetType: {
    type: String,
    default: 'question'
  },
  targetId: {
    type: [Number, String],
    default: null
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(props.modelValue)
const formRef = ref()
const submitting = ref(false)

const form = reactive({
  reason: '',
  description: ''
})

const rules = {
  reason: [
    { required: true, message: '请选择举报原因', trigger: 'change' }
  ]
}

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const handleClose = () => {
  visible.value = false
  form.reason = ''
  form.description = ''
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    await submitReport({
      targetType: props.targetType,
      targetId: props.targetId,
      reason: form.reason,
      description: form.description
    })
    ElMessage.success('举报已提交，感谢你的反馈')
    handleClose()
  } catch (error) {
    console.error('提交举报失败:', error)
  } finally {
    submitting.value = false
  }
}
</script>
