<template>
  <div class="profile-page">
    <section class="profile-hero">
      <div class="profile-hero-inner">
        <div class="ph-avatar">
          <span>{{ (userInfo.nickname || 'U').charAt(0).toUpperCase() }}</span>
        </div>
        <h1 class="ph-name">{{ userInfo.nickname }}</h1>
        <p class="ph-bio">{{ userInfo.bio || '这个人很懒，什么都没写~' }}</p>
        <div class="ph-stats">
          <div class="ph-stat">
            <span class="ph-sv">{{ stats.questionCount || 0 }}</span>
            <span class="ph-sl">提问</span>
          </div>
          <div class="ph-sdiv"></div>
          <div class="ph-stat">
            <span class="ph-sv">{{ stats.answerCount || 0 }}</span>
            <span class="ph-sl">回答</span>
          </div>
          <div class="ph-sdiv"></div>
          <div class="ph-stat">
            <span class="ph-sv">{{ stats.followers || 0 }}</span>
            <span class="ph-sl">关注者</span>
          </div>
        </div>
        <div class="ph-actions" v-if="userStore.isLoggedIn() && userStore.user?.id != route.params.id">
          <button v-if="!isFollowing" class="btn-caramel" @click="handleFollow">关注</button>
          <button v-else class="btn-outline-sm" @click="handleUnfollow">已关注</button>
        </div>
      </div>
    </section>

    <section class="profile-body">
      <div class="profile-container">
        <div class="profile-grid">
          <div class="profile-main">
            <div class="card-lux">
              <el-tabs v-model="activeTab" @tab-change="handleTabChange">
                <el-tab-pane label="提问" name="questions">
                  <div v-loading="loading.questions" class="tab-list">
                    <div v-for="q in questions" :key="q.id" class="list-card" @click="router.push(`/questions/${q.id}`)">
                      <h3 class="lc-title">{{ q.title }}</h3>
                      <div class="lc-meta">
                        <span>{{ q.answerCount || 0 }} 个回答</span>
                        <span>{{ formatTime(q.createdAt) }}</span>
                      </div>
                    </div>
                    <div v-if="!loading.questions && questions.length === 0" class="empty-box">
                      <p>暂无提问</p>
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="回答" name="answers">
                  <div v-loading="loading.answers" class="tab-list">
                    <div v-for="a in answers" :key="a.id" class="list-card" @click="router.push(`/questions/${a.questionId}`)">
                      <p class="lc-excerpt">{{ a.content?.substring(0, 120) }}...</p>
                      <div class="lc-meta">
                        <span>{{ a.questionTitle }}</span>
                        <span>{{ formatTime(a.createdAt) }}</span>
                      </div>
                    </div>
                    <div v-if="!loading.answers && answers.length === 0" class="empty-box">
                      <p>暂无回答</p>
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="关注者" name="followers">
                  <div v-loading="loading.followers" class="user-grid">
                    <div v-for="f in followers" :key="f.id" class="user-card" @click="router.push(`/user/${f.id}`)">
                      <div class="uc-avatar"><span>{{ (f.nickname || 'U').charAt(0).toUpperCase() }}</span></div>
                      <span class="uc-name">{{ f.nickname }}</span>
                    </div>
                    <div v-if="!loading.followers && followers.length === 0" class="empty-box full">
                      <p>暂无关注者</p>
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="关注" name="following">
                  <div v-loading="loading.following" class="user-grid">
                    <div v-for="f in following" :key="f.id" class="user-card" @click="router.push(`/user/${f.id}`)">
                      <div class="uc-avatar"><span>{{ (f.nickname || 'U').charAt(0).toUpperCase() }}</span></div>
                      <span class="uc-name">{{ f.nickname }}</span>
                    </div>
                    <div v-if="!loading.following && following.length === 0" class="empty-box full">
                      <p>暂无关注</p>
                    </div>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </div>

          <aside class="profile-side">
            <div class="card-lux side-card">
              <h3 class="side-title">用户信息</h3>
              <div class="side-divider"></div>
              <div class="info-row">
                <span class="info-label">注册时间</span>
                <span class="info-val">{{ formatTime(userInfo.createdAt) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">声望值</span>
                <span class="info-val accent">{{ userInfo.reputation || 0 }}</span>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </section>
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
const loading = reactive({ questions: false, answers: false, followers: false, following: false })

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString()
}

const fetchUserInfo = async () => {
  try { const res = await getUserInfo(route.params.id); userInfo.value = res.data } catch (e) { console.error(e) }
}
const fetchUserStats = async () => {
  try { const res = await getUserStats(route.params.id); stats.value = res.data } catch (e) { console.error(e) }
}
const fetchUserQuestions = async () => {
  loading.questions = true
  try { const res = await getUserQuestions(route.params.id, { page: 1, size: 20 }); questions.value = res.data.records || res.data.list || [] } catch (e) { console.error(e) }
  finally { loading.questions = false }
}
const fetchUserAnswers = async () => {
  loading.answers = true
  try { const res = await getUserAnswers(route.params.id, { page: 1, size: 20 }); answers.value = res.data.records || res.data.list || [] } catch (e) { console.error(e) }
  finally { loading.answers = false }
}
const fetchFollowers = async () => {
  loading.followers = true
  try { const res = await getFollowers(route.params.id, { page: 1, size: 20 }); followers.value = res.data.list || [] } catch (e) { console.error(e) }
  finally { loading.followers = false }
}
const fetchFollowing = async () => {
  loading.following = true
  try { const res = await getFollowing(route.params.id, { page: 1, size: 20 }); following.value = res.data.list || [] } catch (e) { console.error(e) }
  finally { loading.following = false }
}

const handleFollow = async () => {
  try { await followUser(route.params.id); isFollowing.value = true; stats.value.followers = (stats.value.followers || 0) + 1; ElMessage.success('关注成功') } catch (e) { console.error(e) }
}
const handleUnfollow = async () => {
  try { await unfollowUser(route.params.id); isFollowing.value = false; stats.value.followers = Math.max((stats.value.followers || 1) - 1, 0); ElMessage.success('已取消关注') } catch (e) { console.error(e) }
}

const handleTabChange = (tab) => {
  if (tab === 'questions') fetchUserQuestions()
  else if (tab === 'answers') fetchUserAnswers()
  else if (tab === 'followers') fetchFollowers()
  else if (tab === 'following') fetchFollowing()
}

watch(() => route.params.id, () => { fetchUserInfo(); fetchUserStats(); fetchUserQuestions() }, { immediate: true })
onMounted(() => { fetchUserInfo(); fetchUserStats(); fetchUserQuestions() })
</script>

<style scoped>
.profile-page { overflow: hidden; }

.profile-hero {
  background: var(--forest);
  padding: 100px 0 48px;
  text-align: center;
}

.profile-hero-inner { max-width: 600px; margin: 0 auto; padding: 0 2rem; }

.ph-avatar {
  width: 80px; height: 80px; border-radius: 50%;
  background: var(--forest-light);
  display: flex; align-items: center; justify-content: center;
  font-size: 2rem; font-weight: 600; color: var(--caramel);
  margin: 0 auto 20px;
}

.ph-name {
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-size: 1.8rem; font-weight: 400; color: var(--cream);
  margin-bottom: 8px;
}

.ph-bio { font-size: 0.9rem; color: var(--cream-60); font-weight: 300; margin-bottom: 28px; }

.ph-stats {
  display: flex; align-items: center; justify-content: center; gap: 0;
}

.ph-stat { display: flex; flex-direction: column; align-items: center; flex: 1; }
.ph-sv { font-family: 'Noto Serif SC', 'Playfair Display', serif; font-size: 1.5rem; color: var(--cream); margin-bottom: 4px; }
.ph-sl { font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.12em; color: var(--cream-50); font-weight: 500; }
.ph-sdiv { width: 1px; height: 32px; background: var(--cream-20); }

.ph-actions { margin-top: 24px; }
.btn-caramel {
  background: var(--caramel); color: #fff; border: none;
  padding: 10px 32px; border-radius: 999px; font-size: 0.8rem; font-weight: 500;
  letter-spacing: 0.08em; text-transform: uppercase; cursor: pointer; transition: background 0.3s;
}
.btn-caramel:hover { background: var(--caramel-light); }
.btn-outline-sm {
  background: transparent; color: var(--cream); border: 1.5px solid var(--cream-30);
  padding: 10px 32px; border-radius: 999px; font-size: 0.8rem; font-weight: 500;
  cursor: pointer; transition: all 0.3s;
}
.btn-outline-sm:hover { border-color: var(--caramel); color: var(--caramel); }

.profile-body { padding: 48px 0 100px; }
.profile-container { max-width: 1100px; margin: 0 auto; padding: 0 2rem; }
.profile-grid { display: flex; gap: 36px; }
.profile-main { flex: 1; min-width: 0; }
.profile-side { width: 280px; flex-shrink: 0; }

.card-lux { background: #fff; border: 1px solid var(--forest-08); border-radius: 20px; padding: 28px; }

.tab-list { display: flex; flex-direction: column; gap: 12px; margin-top: 16px; }

.list-card {
  padding: 18px; border: 1px solid var(--forest-08); border-radius: 14px;
  cursor: pointer; transition: all 0.3s;
}
.list-card:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(26, 46, 38, 0.06); }

.lc-title { font-family: 'Noto Serif SC', 'Playfair Display', serif; font-size: 1rem; color: var(--forest); margin-bottom: 6px; }
.lc-excerpt { font-size: 0.85rem; color: var(--forest-60); margin-bottom: 8px; line-height: 1.6; }
.lc-meta { display: flex; justify-content: space-between; font-size: 0.75rem; color: var(--forest-40); }

.user-grid { display: flex; flex-wrap: wrap; gap: 12px; margin-top: 16px; }
.user-card {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 18px; border: 1px solid var(--forest-08); border-radius: 14px;
  cursor: pointer; transition: all 0.3s;
}
.user-card:hover { border-color: var(--caramel); }
.uc-avatar {
  width: 36px; height: 36px; border-radius: 50%; background: var(--cream-dark);
  display: flex; align-items: center; justify-content: center;
  font-size: 0.8rem; font-weight: 600; color: var(--caramel);
}
.uc-name { font-size: 0.85rem; color: var(--forest); font-weight: 500; }

.empty-box { text-align: center; padding: 40px 0; }
.empty-box.full { width: 100%; }
.empty-box p { font-size: 0.9rem; color: var(--forest-40); }

.side-card { position: sticky; top: 100px; }
.side-title { font-family: 'Noto Serif SC', 'Playfair Display', serif; font-size: 1.05rem; font-weight: 500; color: var(--forest); }
.side-divider { width: 32px; height: 1.5px; background: var(--caramel); margin: 10px 0 16px; }
.info-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid var(--forest-08); font-size: 0.85rem; }
.info-row:last-child { border-bottom: none; }
.info-label { color: var(--forest-50); }
.info-val { color: var(--forest); font-weight: 500; }
.info-val.accent { color: var(--caramel); }

@media (max-width: 1024px) {
  .profile-grid { flex-direction: column; }
  .profile-side { width: 100%; }
  .side-card { position: static; }
}
</style>
