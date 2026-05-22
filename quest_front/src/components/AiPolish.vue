<template>
  <div class="ai-polish">
    <div class="polish-header">
      <el-input
        v-model="originalText"
        type="textarea"
        :rows="6"
        placeholder="输入需要润色的文本..."
      />
      <el-button type="primary" :loading="loading" @click="handlePolish" style="margin-top: 12px">
        开始润色
      </el-button>
    </div>

    <div v-if="polishedText" class="polish-result">
      <div class="compare-section">
        <div class="compare-panel">
          <h4>原文</h4>
          <div class="compare-content">{{ originalText }}</div>
        </div>
        <div class="compare-panel polished">
          <h4>润色后</h4>
          <div class="compare-content">{{ polishedText }}</div>
        </div>
      </div>
      <div class="polish-actions">
        <el-button @click="$emit('close')">取消</el-button>
        <el-button type="primary" @click="handleReplace">使用润色结果</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { chat } from '@/api/ai'
import { ElMessage } from 'element-plus'

const props = defineProps({
  original: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['replace', 'close'])

const originalText = ref(props.original)
const polishedText = ref('')
const loading = ref(false)

watch(() => props.original, (val) => {
  originalText.value = val
})

const handlePolish = async () => {
  if (!originalText.value.trim()) {
    ElMessage.warning('请输入需要润色的文本')
    return
  }
  loading.value = true
  try {
    const res = await chat({
      message: `请帮我润色以下文本，保持原意但改善表达：\n\n${originalText.value}`,
      conversationId: null
    })
    polishedText.value = res.data.content
  } catch (error) {
    console.error('AI润色失败:', error)
  } finally {
    loading.value = false
  }
}

const handleReplace = () => {
  emit('replace', polishedText.value)
}
</script>

<style scoped>
.ai-polish {
  padding: 8px;
}

.polish-header {
  margin-bottom: 24px;
}

.polish-result {
  border-top: 1px solid #eee;
  padding-top: 24px;
}

.compare-section {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.compare-panel {
  flex: 1;
  min-width: 0;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
}

.compare-panel h4 {
  padding: 10px 16px;
  margin: 0;
  font-size: 14px;
  background: #f5f5f5;
  color: #666;
}

.compare-panel.polished h4 {
  background: #e6f7ff;
  color: #1e80ff;
}

.compare-content {
  padding: 16px;
  font-size: 14px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
  min-height: 120px;
}

.polish-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
