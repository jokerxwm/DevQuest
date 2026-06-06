<template>
  <div class="settings-page">
    <section class="settings-hero">
      <div class="settings-hero-inner">
        <span class="section-eyebrow">Account Settings</span>
        <h1 class="settings-title">个人设置</h1>
      </div>
    </section>

    <section class="settings-body">
      <div class="settings-container">
        <div class="settings-grid">
          <div class="settings-main">
            <div class="card-lux">
              <div class="card-head">
                <h3 class="card-title">基本信息</h3>
                <div class="card-divider"></div>
              </div>
              <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-position="top">
                <div class="form-row">
                  <div class="form-group half">
                    <label class="form-label">用户名</label>
                    <el-input v-model="profileForm.username" disabled size="large" />
                  </div>
                  <div class="form-group half">
                    <label class="form-label">邮箱</label>
                    <el-input v-model="profileForm.email" disabled size="large" />
                  </div>
                </div>
                <div class="form-group">
                  <label class="form-label">昵称</label>
                  <el-input v-model="profileForm.nickname" placeholder="请输入昵称" size="large" />
                </div>
                <div class="form-group">
                  <label class="form-label">个人简介</label>
                  <el-input v-model="profileForm.bio" type="textarea" :rows="4" placeholder="介绍一下你自己" />
                </div>
                <div class="form-group">
                  <label class="form-label">所在地</label>
                  <el-input v-model="profileForm.location" placeholder="请输入所在地" size="large" />
                </div>
                <div class="form-group">
                  <label class="form-label">个人网站</label>
                  <el-input v-model="profileForm.website" placeholder="https://" size="large" />
                </div>
                <div class="form-group">
                  <label class="form-label">GitHub</label>
                  <el-input v-model="profileForm.github" placeholder="GitHub 用户名" size="large" />
                </div>
                <button type="button" class="btn-primary" @click="handleUpdateProfile">保存资料</button>
              </el-form>
            </div>

            <div class="card-lux">
              <div class="card-head">
                <h3 class="card-title">修改密码</h3>
                <div class="card-divider"></div>
              </div>
              <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-position="top">
                <div class="form-group">
                  <label class="form-label">当前密码</label>
                  <el-input v-model="passwordForm.currentPassword" type="password" placeholder="请输入当前密码" size="large" show-password />
                </div>
                <div class="form-row">
                  <div class="form-group half">
                    <label class="form-label">新密码</label>
                    <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" size="large" show-password />
                  </div>
                  <div class="form-group half">
                    <label class="form-label">确认新密码</label>
                    <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" size="large" show-password />
                  </div>
                </div>
                <button type="button" class="btn-primary" @click="handleUpdatePassword">修改密码</button>
              </el-form>
            </div>
          </div>

          <aside class="settings-side">
            <div class="card-lux side-card">
              <h3 class="side-title">账号信息</h3>
              <div class="side-divider"></div>
              <div class="info-row"><span class="info-label">注册时间</span><span class="info-val">{{ formatTime(userStore.user?.createdAt) }}</span></div>
              <div class="info-row"><span class="info-label">声望值</span><span class="info-val accent">{{ userStore.user?.reputation || 0 }}</span></div>
            </div>
          </aside>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const profileFormRef = ref()
const passwordFormRef = ref()
const profileForm = reactive({ username: '', email: '', nickname: '', bio: '', location: '', website: '', github: '' })
const passwordForm = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' })
const profileRules = { nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }] }
const passwordRules = { currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }], newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码长度至少6位', trigger: 'blur' }], confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' }] }
const formatTime = (t) => { if (!t) return ''; return new Date(t).toLocaleDateString() }

onMounted(() => { if (userStore.user) { Object.assign(profileForm, { username: userStore.user.username, email: userStore.user.email, nickname: userStore.user.nickname, bio: userStore.user.bio, location: userStore.user.location, website: userStore.user.website, github: userStore.user.github }) } })

const handleUpdateProfile = async () => { try { await request.put('/users/profile', profileForm); await userStore.fetchUserInfo(); ElMessage.success('资料更新成功') } catch (e) { console.error(e) } }
const handleUpdatePassword = async () => { if (passwordForm.newPassword !== passwordForm.confirmPassword) { ElMessage.error('两次输入的密码不一致'); return } try { await request.put('/users/password', passwordForm); ElMessage.success('密码修改成功'); Object.assign(passwordForm, { currentPassword: '', newPassword: '', confirmPassword: '' }) } catch (e) { console.error(e) } }
</script>

<style scoped>
.settings-page { overflow: hidden; }
.settings-hero { background: var(--cream); padding: 100px 0 40px; text-align: center; border-bottom: 1px solid var(--border-light); }
.settings-hero-inner { max-width: 1200px; margin: 0 auto; padding: 0 2rem; }
.section-eyebrow { display: block; font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.15em; color: var(--caramel); font-weight: 500; margin-bottom: 8px; }
.settings-title { font-family: 'Noto Serif SC', serif; font-size: 2rem; font-weight: 400; color: var(--forest); }
.settings-body { padding: 48px 0 100px; }
.settings-container { max-width: 1100px; margin: 0 auto; padding: 0 2rem; }
.settings-grid { display: flex; gap: 36px; }
.settings-main { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 24px; }
.settings-side { width: 280px; flex-shrink: 0; }
.card-lux { background: #fff; border: 1px solid var(--forest-08); border-radius: 20px; padding: 32px; }
.card-head { margin-bottom: 24px; }
.card-title { font-family: 'Noto Serif SC', serif; font-size: 1.2rem; font-weight: 500; color: var(--forest); }
.card-divider { width: 32px; height: 1.5px; background: var(--caramel); margin-top: 10px; }
.form-row { display: flex; gap: 16px; }
.form-group { margin-bottom: 20px; }
.form-group.half { flex: 1; }
.form-label { display: block; font-size: 0.85rem; font-weight: 600; color: var(--forest); margin-bottom: 8px; }
.btn-primary { background: var(--caramel); color: #fff; border: none; padding: 12px 28px; border-radius: 999px; font-size: 0.85rem; font-weight: 500; letter-spacing: 0.05em; cursor: pointer; transition: background 0.3s; }
.btn-primary:hover { background: var(--caramel-light); }
.side-card { position: sticky; top: 100px; }
.side-title { font-family: 'Noto Serif SC', serif; font-size: 1.05rem; font-weight: 500; color: var(--forest); }
.side-divider { width: 32px; height: 1.5px; background: var(--caramel); margin: 10px 0 16px; }
.info-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid var(--forest-08); font-size: 0.85rem; }
.info-row:last-child { border-bottom: none; }
.info-label { color: var(--forest-50); }
.info-val { color: var(--forest); font-weight: 500; }
.info-val.accent { color: var(--caramel); }
@media (max-width: 1024px) { .settings-grid { flex-direction: column; } .settings-side { width: 100%; } .side-card { position: static; } }
@media (max-width: 640px) { .form-row { flex-direction: column; gap: 0; } }
</style>
