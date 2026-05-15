<template>
  <div class="user-settings container">
    <div class="content-wrapper">
      <div class="sidebar-nav">
        <div class="card">
          <div class="nav-item" :class="{ active: activeSection === 'profile' }" @click="activeSection = 'profile'">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </div>
          <div class="nav-item" :class="{ active: activeSection === 'account' }" @click="activeSection = 'account'">
            <el-icon><Setting /></el-icon>
            <span>账号安全</span>
          </div>
        </div>
      </div>

      <div class="main-content">
        <div v-if="activeSection === 'profile'" class="card settings-card">
          <h3>个人信息</h3>
          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-position="top">
            <el-form-item label="头像">
              <div class="avatar-upload">
                <el-avatar :size="80" :src="profileForm.avatar">
                  {{ profileForm.nickname?.charAt(0) }}
                </el-avatar>
                <el-upload
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                  :http-request="handleAvatarUpload"
                  accept="image/*"
                >
                  <el-button size="small">更换头像</el-button>
                </el-upload>
              </div>
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="profileForm.nickname" placeholder="请输入昵称" maxlength="30" show-word-limit />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="个人简介" prop="bio">
              <el-input v-model="profileForm.bio" type="textarea" :rows="4" placeholder="介绍一下自己..." maxlength="200" show-word-limit />
            </el-form-item>
            <el-form-item label="个人网站" prop="website">
              <el-input v-model="profileForm.website" placeholder="https://example.com" />
            </el-form-item>
            <el-form-item label="GitHub" prop="github">
              <el-input v-model="profileForm.github" placeholder="https://github.com/username" />
            </el-form-item>
            <el-form-item label="所在地" prop="location">
              <el-input v-model="profileForm.location" placeholder="城市" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="saveProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div v-if="activeSection === 'account'" class="card settings-card">
          <h3>修改密码</h3>
          <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-position="top">
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="changingPassword" @click="changePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User, Setting } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { updateUserInfo, changePassword as changePasswordApi, uploadAvatar } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const activeSection = ref('profile')
const saving = ref(false)
const changingPassword = ref(false)

const profileFormRef = ref()
const passwordFormRef = ref()

const profileForm = reactive({
  nickname: '',
  email: '',
  bio: '',
  avatar: '',
  website: '',
  github: '',
  location: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 30, message: '昵称长度在2-30个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
    return false
  }
  return true
}

const handleAvatarUpload = async (options) => {
  try {
    const res = await uploadAvatar(options.file)
    profileForm.avatar = res.data.url
    ElMessage.success('头像上传成功')
  } catch (error) {
    ElMessage.error('头像上传失败')
  }
}

const saveProfile = async () => {
  await profileFormRef.value.validate()
  saving.value = true
  try {
    await updateUserInfo(profileForm)
    await userStore.fetchUser()
    ElMessage.success('个人信息保存成功')
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

const changePassword = async () => {
  await passwordFormRef.value.validate()
  changingPassword.value = true
  try {
    await changePasswordApi({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    changingPassword.value = false
  }
}

onMounted(() => {
  if (userStore.user) {
    Object.assign(profileForm, {
      nickname: userStore.user.nickname || '',
      email: userStore.user.email || '',
      bio: userStore.user.bio || '',
      avatar: userStore.user.avatar || '',
      website: userStore.user.website || '',
      github: userStore.user.github || '',
      location: userStore.user.location || ''
    })
  }
})
</script>

<style scoped>
.user-settings {
  padding: 24px 0;
}

.content-wrapper {
  display: flex;
  gap: 24px;
}

.sidebar-nav {
  width: 200px;
  flex-shrink: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 4px;
  margin: 8px;
}

.nav-item:hover {
  background: #f5f5f5;
}

.nav-item.active {
  background: #e6f7ff;
  color: #1e80ff;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.settings-card h3 {
  font-size: 20px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}
</style>
