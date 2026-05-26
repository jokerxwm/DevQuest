<template>
  <el-dialog v-model="visible" title="分享" width="420px" destroy-on-close @close="handleClose">
    <div class="share-card">
      <div class="share-preview" ref="previewRef">
        <div class="preview-header">
          <span class="preview-logo">DevQuest</span>
        </div>
        <h3 class="preview-title">{{ title }}</h3>
        <p class="preview-desc">{{ description?.substring(0, 100) }}...</p>
        <div class="preview-footer">
          <span>来自 DevQuest 技术问答社区</span>
        </div>
      </div>

      <div class="share-link">
        <el-input v-model="shareLink" readonly>
          <template #append>
            <el-button @click="copyLink">复制链接</el-button>
          </template>
        </el-input>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  },
  description: {
    type: String,
    default: ''
  },
  questionId: {
    type: [Number, String],
    default: null
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(props.modelValue)
const previewRef = ref(null)

const shareLink = computed(() => {
  return `${window.location.origin}/questions/${props.questionId}`
})

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const copyLink = () => {
  if (navigator.clipboard) {
    navigator.clipboard.writeText(shareLink.value)
    ElMessage.success('链接已复制到剪贴板')
  }
}

const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.share-card {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.share-preview {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.preview-header {
  background: linear-gradient(135deg, #1e80ff, #36d399);
  padding: 12px 16px;
}

.preview-logo {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.preview-title {
  padding: 16px 16px 8px;
  font-size: 16px;
  color: #333;
  line-height: 1.4;
}

.preview-desc {
  padding: 0 16px 16px;
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.preview-footer {
  padding: 10px 16px;
  background: #f9f9f9;
  font-size: 12px;
  color: #999;
  border-top: 1px solid #eee;
}

.share-link {
  margin-top: 4px;
}
</style>
