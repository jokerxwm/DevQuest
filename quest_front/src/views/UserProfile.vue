<template>
  <div class="user-profile container">
    <div class="content-wrapper">
      <div class="main-content">
        <div class="profile-card card">
          <div class="profile-header">
            <el-avatar :size="80">{{ userInfo.nickname?.charAt(0) }}</el-avatar>
            <div class="profile-info">
              <h2>{{ userInfo.nickname }}</h2>
              <p class="bio">{{ userInfo.bio || '这个人很懒，什么都没写~' }}</p>
              <div class="stats">
                <div class="stat-item">
                  <span class="stat-value">{{ stats.questionCount || 0 }}</span>
                  <span class="stat-label">提问</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ stats.answerCount || 0 }}</span>
                  <span class="stat-label">回答</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ stats.followers || 0 }}</span>
                  <span class="stat-label">关注者</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="action-buttons" v-if="userStore.isLoggedIn() && userStore.user?.id != route.params.id">
          <el-button v-if="!isFollowing" type="primary" @click="handleFollow">关注</el-button>
          <el-button v-else @click="handleUnfollow">已关注</el-button>
        </div>

        <div class="tabs">
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="提问" name="questions">
              <div v-loading="loading.questions">
                <div v-for="q in questions" :key="q.id" class="question-item card" @click="router.push(`/questions/${q.id}`)">
                  <h3>{{ q.title }}</h3>
                  <div class="question-meta">
                    <span>{{ q.answerCount || 0 }}个回答</span>
                    <span>{{ formatTime(q.createdAt) }}</span>
                  </div>
                </div>
                <el-empty v-if="!loading.questions && questions.length === 0" description="暂无提问" />
              </div>
            </el-tab-pane>
            <el-tab-pane label="回答" name="answers">
              <div v-loading="loading.answers">
                <div v-for="a in answers" :key="a.id" class="answer-item card" @click="router.push(`/questions/${a.questionId}`)">
                  <p class="answer-content">{{ a.content?.substring(0, 100) }}...</p>
                  <div class="answer-meta">
                    <span>回答于：{{ a.questionTitle }}</span>
                    <span>{{ formatTime(a.createdAt) }}</span>
                  </div>
                </div>
                <el-empty v-if="!loading.answers && answers.length === 0" description="暂无回答" />
              </div>
            </el-tab-pane>
            <el-tab-pane label="关注者" name="followers">
              <div v-loading="loading.followers">
                <div v-for="f in followers" :key="f.id" class="user-item card" @click="router.push(`/user/${f.id}`)">
                  <el-avatar :size="40">{{ f.nickname?.charAt(0) }}</el-avatar>
                  <div class="user-info">
                    <span class="user-name">{{ f.nickname }}</span>
                    <span class="user-bio">{{ f.bio || '这个人很懒，什么都没写~' }}</span>
                  </div>
                </div>
                <el-empty v-if="!loading.followers && followers.length === 0" description="暂无关注者" />
              </div>
            </el-tab-pane>
            <el-tab-pane label="关注" name="following">
              <div v-loading="loading.following">
                <div v-for="f in following" :key="f.id" class="user-item card" @click="router.push(`/user/${f.id}`)">
                  <el-avatar :size="40">{{ f.nickname?.charAt(0) }}</el-avatar>
                  <div class="user-info">
                    <span class="user-name">{{ f.nickname }}</span>
                    <span class="user-bio">{{ f.bio || '这个人很懒，什么都没写~' }}</span>
                  </div>
                </div>
                <el-empty v-if="!loading.following && following.length === 0" description="暂无关注" />
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>

      <div class="sidebar">
        <div class="card sidebar-card">
          <h3>用户信息</h3>
          <div class="info-item">
            <span class="label">注册时间</span>
            <span class="value">{{ formatTime(userInfo.createdAt) }}</span>
          </div>
          <div class="info-item">
            <span class="label">声望值</span>
            <span class="value">{{ userInfo.reputation || 0 }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo, getUserStats, getUserAnswers, getFollowers, getFollowing, followUser, unfollowUser } from '@/api/user'
import { getUserQuestions } from '@/api/question'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const userInfo = ref({})
const stats = ref({})
const questions = ref([])
const answers = ref([])
const followers = ref([])
const following = ref([])
const isFollowing = ref(false)
const activeTab = ref('questions')
const loading = reactive({
  questions: false,
  answers: false,
  followers: false,
  following: false
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString()
}

const fetchUserInfo = async () => {
  try {
    const res = await getUserInfo(route.params.id)
    userInfo.value = res.data
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const fetchUserStats = async () => {
  try {
    const res = await getUserStats(route.params.id)
    stats.value = res.data
  } catch (error) {
    console.error('获取用户统计失败:', error)
  }
}

const fetchUserQuestions = async () => {
  loading.questions = true
  try {
    const res = await getUserQuestions(route.params.id, { page: 1, size: 20 })
    questions.value = res.data.list || []
  } catch (error) {
    console.error('获取用户问题失败:', error)
  } finally {
    loading.questions = false
  }
}

const fetchUserAnswers = async () => {
  loading.answers = true
  try {
    const res = await getUserAnswers(route.params.id, { page: 1, size: 20 })
    answers.value = res.data.list || []
  } catch (error) {
    console.error('获取用户回答失败:', error)
  } finally {
    loading.answers = false
  }
}

const fetchFollowers = async () => {
  loading.followers = true
  try {
    const res = await getFollowers(route.params.id, { page: 1, size: 20 })
    followers.value = res.data.list || []
  } catch (error) {
    console.error('获取关注者失败:', error)
  } finally {
    loading.followers = false
  }
}

const fetchFollowing = async () => {
  loading.following = true
  try {
    const res = await getFollowing(route.params.id, { page: 1, size: 20 })
    following.value = res.data.list || []
  } catch (error) {
    console.error('获取关注列表失败:', error)
  } finally {
    loading.following = false
  }
}

const handleFollow = async () => {
  try {
    await followUser(route.params.id)
    isFollowing.value = true
    stats.value.followers = (stats.value.followers || 0) + 1
    ElMessage.success('关注成功')
  } catch (error) {
    console.error('关注失败:', error)
  }
}

const handleUnfollow = async () => {
  try {
    await unfollowUser(route.params.id)
    isFollowing.value = false
    stats.value.followers = Math.max((stats.value.followers || 1) - 1, 0)
    ElMessage.success('已取消关注')
  } catch (error) {
    console.error('取消关注失败:', error)
  }
}

const handleTabChange = (tab) => {
  if (tab === 'questions') {
    fetchUserQuestions()
  } else if (tab === 'answers') {
    fetchUserAnswers()
  } else if (tab === 'followers') {
    fetchFollowers()
  } else if (tab === 'following') {
    fetchFollowing()
  }
}

watch(() => route.params.id, () => {
  fetchUserInfo()
  fetchUserStats()
  fetchUserQuestions()
}, { immediate: true })

onMounted(() => {
  fetchUserInfo()
  fetchUserStats()
  fetchUserQuestions()
})
</script>

<style scoped>
.user-profile {
  padding: 24px 0;
}

.content-wrapper {
  display: flex;
  gap: 24px;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.sidebar {
  width: 300px;
  flex-shrink: 0;
}

.profile-card {
  margin-bottom: 24px;
}

.action-buttons {
  margin-bottom: 16px;
}

.profile-header {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.profile-info h2 {
  font-size: 24px;
  margin-bottom: 8px;
}

.bio {
  color: #666;
  margin-bottom: 16px;
}

.stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: 600;
  color: #1e80ff;
}

.stat-label {
  font-size: 13px;
  color: #999;
}

.question-item,
.answer-item {
  cursor: pointer;
  transition: box-shadow 0.3s;
}

.question-item:hover,
.answer-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.question-item h3 {
  font-size: 16px;
  margin-bottom: 8px;
}

.question-meta {
  font-size: 13px;
  color: #999;
  display: flex;
  gap: 16px;
}

.answer-content {
  color: #333;
  margin-bottom: 8px;
}

.answer-meta {
  font-size: 13px;
  color: #999;
  display: flex;
  gap: 16px;
}

.user-item {
  display: flex;
  gap: 16px;
  align-items: center;
  cursor: pointer;
  transition: box-shadow 0.3s;
}

.user-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 16px;
  color: #333;
  font-weight: 500;
  margin-bottom: 4px;
}

.user-bio {
  font-size: 13px;
  color: #999;
}

.sidebar-card {
  margin-bottom: 16px;
}

.sidebar-card h3 {
  font-size: 16px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
}

.label {
  color: #999;
}

.value {
  color: #333;
}
</style>
