<template>
  <div class="new-message container">
    <div class="card">
      <div class="page-header">
        <el-button text @click="router.back()">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
        <h2>发起私信</h2>
        <div></div>
      </div>

      <div class="form-section">
        <el-form-item label="收件人">
          <el-select
            v-model="selectedUser"
            filterable
            remote
            :remote-method="searchUser"
            :loading="searching"
            placeholder="搜索用户昵称..."
            style="width: 100%"
          >
            <el-option
              v-for="user in userOptions"
              :key="user.id"
              :label="user.nickname"
              :value="user.id"
            >
              <div class="user-option">
                <el-avatar :size="24">{{ user.nickname?.charAt(0) }}</el-avatar>
                <span>{{ user.nickname }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="消息内容">
          <el-input
            v-model="content"
            type="textarea"
            :rows="6"
            placeholder="输入你想发送的消息..."
          />
        </el-form-item>

        <div class="form-footer">
          <el-button @click="router.back()">取消</el-button>
          <el-button type="primary" :loading="sending" @click="handleSend">发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { sendMessage, searchUsers } from '@/api/message'
import { ElMessage } from 'element-plus'

const router = useRouter()

const selectedUser = ref(null)
const content = ref('')
const userOptions = ref([])
const searching = ref(false)
const sending = ref(false)

const searchUser = async (keyword) => {
  if (!keyword) {
    userOptions.value = []
    return
  }
  searching.value = true
  try {
    const res = await searchUsers(keyword)
    userOptions.value = res.data || []
  } catch (error) {
    console.error('搜索用户失败:', error)
  } finally {
    searching.value = false
  }
}

const handleSend = async () => {
  if (!selectedUser.value) {
    ElMessage.warning('请选择收件人')
    return
  }
  if (!content.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }
  sending.value = true
  try {
    await sendMessage(selectedUser.value, { content: content.value })
    ElMessage.success('消息发送成功')
    router.push(`/messages/${selectedUser.value}`)
  } catch (error) {
    console.error('发送消息失败:', error)
  } finally {
    sending.value = false
  }
}
</script>

<style scoped>
.new-message {
  padding: 24px 0;
  max-width: 600px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.page-header h2 {
  font-size: 20px;
}

.form-section {
  padding: 0 16px;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.user-option {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
