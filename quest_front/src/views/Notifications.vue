<template>
  <div class="notif-page">
    <section class="notif-hero">
      <div class="notif-hero-inner">
        <span class="section-eyebrow">Notifications</span>
        <div class="nh-row">
          <h1 class="notif-title">消息通知</h1>
          <button class="btn-outline-sm" @click="markAllRead">全部已读</button>
        </div>
      </div>
    </section>

    <section class="notif-body">
      <div class="notif-container">
        <div class="card-lux">
          <div v-loading="loading" class="notif-list">
            <div v-for="n in notifications" :key="n.id" class="notif-card" :class="{ unread: !n.isRead }" @click="handleClick(n)">
              <div class="nc-icon" :class="n.type">
                <svg v-if="n.type === 'answer'" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                <svg v-else-if="n.type === 'vote'" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 19V5M5 12l7-7 7 7"/></svg>
                <svg v-else-if="n.type === 'accept'" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6L9 17l-5-5"/></svg>
                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0"/></svg>
              </div>
              <div class="nc-body">
                <p class="nc-text">{{ n.content }}</p>
                <span class="nc-time">{{ formatTime(n.createdAt) }}</span>
              </div>
              <div class="nc-dot" v-if="!n.isRead"></div>
            </div>

            <div v-if="!loading && notifications.length === 0" class="empty-box">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="empty-icon">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0" />
              </svg>
              <p>暂无通知</p>
            </div>
          </div>

          <div v-if="total > 0" class="pagination">
            <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="fetchNotifications" />
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const notifications = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 2592000000) return `${Math.floor(diff / 86400000)}天前`
  return date.toLocaleDateString()
}

const fetchNotifications = async () => {
  loading.value = true
  try {
    const res = await request.get('/notifications', { params: { page: page.value, size: pageSize.value } })
    notifications.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) { console.error(error) }
  finally { loading.value = false }
}

const markAllRead = async () => {
  try { await request.put('/notifications/read-all'); ElMessage.success('已全部标记为已读'); fetchNotifications() } catch (e) { console.error(e) }
}

const handleClick = async (n) => {
  if (!n.isRead) {
    try { await request.put(`/notifications/${n.id}/read`) } catch (e) { console.error(e) }
  }
  if (n.targetId) router.push(`/questions/${n.targetId}`)
}

onMounted(() => { fetchNotifications() })
</script>

<style scoped>
.notif-page { overflow: hidden; }
.notif-hero { background: var(--cream); padding: 100px 0 40px; border-bottom: 1px solid var(--border-light); }
.notif-hero-inner { max-width: 800px; margin: 0 auto; padding: 0 2rem; }
.section-eyebrow { display: block; font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.15em; color: var(--caramel); font-weight: 500; margin-bottom: 8px; }
.nh-row { display: flex; justify-content: space-between; align-items: center; }
.notif-title { font-family: 'Noto Serif SC', 'Playfair Display', serif; font-size: 2rem; font-weight: 400; color: var(--forest); }
.btn-outline-sm { background: transparent; color: var(--forest); border: 1.5px solid var(--forest-20); padding: 8px 20px; border-radius: 999px; font-size: 0.75rem; font-weight: 500; cursor: pointer; transition: all 0.3s; }
.btn-outline-sm:hover { border-color: var(--caramel); color: var(--caramel); }

.notif-body { padding: 48px 0 100px; }
.notif-container { max-width: 800px; margin: 0 auto; padding: 0 2rem; }
.card-lux { background: #fff; border: 1px solid var(--forest-08); border-radius: 20px; padding: 32px; }

.notif-list { display: flex; flex-direction: column; gap: 8px; }

.notif-card {
  display: flex; align-items: flex-start; gap: 14px; padding: 16px;
  border-radius: 14px; cursor: pointer; transition: all 0.3s; position: relative;
}
.notif-card:hover { background: var(--cream); }
.notif-card.unread { background: rgba(184, 138, 89, 0.05); }

.nc-icon {
  width: 36px; height: 36px; border-radius: 10px; background: var(--cream-dark);
  display: flex; align-items: center; justify-content: center; flex-shrink: 0; color: var(--forest-60);
}
.nc-icon.answer { color: var(--caramel); background: rgba(184, 138, 89, 0.1); }
.nc-icon.vote { color: #2ecc71; background: rgba(46, 204, 113, 0.1); }
.nc-icon.accept { color: #3498db; background: rgba(52, 152, 219, 0.1); }

.nc-body { flex: 1; min-width: 0; }
.nc-text { font-size: 0.85rem; color: var(--forest); line-height: 1.6; margin-bottom: 4px; }
.nc-time { font-size: 0.72rem; color: var(--forest-40); }

.nc-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--caramel); flex-shrink: 0; margin-top: 6px; }

.empty-box { text-align: center; padding: 48px 0; }
.empty-icon { color: var(--forest-20); margin-bottom: 12px; }
.empty-box p { font-size: 0.9rem; color: var(--forest-40); }

.pagination { margin-top: 24px; display: flex; justify-content: center; }
</style>
