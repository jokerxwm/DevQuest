<template>
  <div class="fav-page">
    <section class="fav-hero">
      <div class="fav-hero-inner">
        <span class="section-eyebrow">My Collection</span>
        <h1 class="fav-title">我的收藏</h1>
      </div>
    </section>

    <section class="fav-body">
      <div class="fav-container">
        <div class="card-lux">
          <div v-loading="loading" class="fav-list">
            <div v-for="item in favorites" :key="item.id" class="fav-card" @click="router.push(`/questions/${item.questionId}`)">
              <div class="fav-stats">
                <div class="fs-item">
                  <span class="fs-val">{{ item.voteCount || 0 }}</span>
                  <span class="fs-lbl">投票</span>
                </div>
                <div class="fs-item" :class="{ active: item.answerCount > 0 }">
                  <span class="fs-val">{{ item.answerCount || 0 }}</span>
                  <span class="fs-lbl">回答</span>
                </div>
              </div>
              <div class="fav-body-content">
                <h3 class="fav-q-title">{{ item.title }}</h3>
                <p class="fav-excerpt">{{ item.content?.substring(0, 140) }}...</p>
                <div class="fav-meta">
                  <div class="fav-tags">
                    <span v-for="tag in (item.tags || [])" :key="tag.id" class="fav-tag">{{ tag.name }}</span>
                  </div>
                  <div class="fav-info">
                    <span class="fi-time">收藏于 {{ formatTime(item.createdAt) }}</span>
                    <button class="fi-remove" @click.stop="handleRemoveFavorite(item.questionId)">取消收藏</button>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="!loading && favorites.length === 0" class="empty-box">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="empty-icon">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
              </svg>
              <p>暂无收藏</p>
              <button class="btn-caramel" @click="router.push('/questions')">去浏览问题</button>
            </div>
          </div>

          <div v-if="total > 0" class="pagination">
            <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="fetchFavorites" />
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFavorites, removeFavorite } from '@/api/favorite'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const favorites = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
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

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavorites({ page: page.value, size: pageSize.value })
    favorites.value = res.data.records || res.data.list || []
    total.value = res.data.total || 0
  } catch (error) { console.error(error) }
  finally { loading.value = false }
}

const handleRemoveFavorite = async (questionId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏该问题吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await removeFavorite(questionId)
    ElMessage.success('已取消收藏')
    fetchFavorites()
  } catch (error) { if (error !== 'cancel') console.error(error) }
}

onMounted(() => { fetchFavorites() })
</script>

<style scoped>
.fav-page { overflow: hidden; }
.fav-hero { background: var(--cream); padding: 100px 0 40px; text-align: center; border-bottom: 1px solid var(--border-light); }
.fav-hero-inner { max-width: 1200px; margin: 0 auto; padding: 0 2rem; }
.section-eyebrow { display: block; font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.15em; color: var(--caramel); font-weight: 500; margin-bottom: 8px; }
.fav-title { font-family: 'Noto Serif SC', 'Playfair Display', serif; font-size: 2rem; font-weight: 400; color: var(--forest); }
.fav-body { padding: 48px 0 100px; }
.fav-container { max-width: 900px; margin: 0 auto; padding: 0 2rem; }
.card-lux { background: #fff; border: 1px solid var(--forest-08); border-radius: 20px; padding: 32px; }

.fav-list { display: flex; flex-direction: column; gap: 14px; }

.fav-card {
  display: flex; gap: 20px; padding: 20px; border: 1px solid var(--forest-08); border-radius: 16px;
  cursor: pointer; transition: all 0.3s;
}
.fav-card:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(26, 46, 38, 0.06); }

.fav-stats { display: flex; flex-direction: column; gap: 6px; min-width: 56px; }
.fs-item { text-align: center; padding: 6px 8px; border-radius: 10px; background: var(--cream-dark); }
.fs-item.active { background: rgba(184, 138, 89, 0.1); }
.fs-val { display: block; font-family: 'Noto Serif SC', 'Playfair Display', serif; font-size: 1rem; font-weight: 500; color: var(--forest); }
.fs-lbl { font-size: 0.65rem; text-transform: uppercase; letter-spacing: 0.08em; color: var(--forest-40); }

.fav-body-content { flex: 1; min-width: 0; }
.fav-q-title { font-family: 'Noto Serif SC', 'Playfair Display', serif; font-size: 1rem; color: var(--forest); margin-bottom: 6px; transition: color 0.3s; }
.fav-card:hover .fav-q-title { color: var(--caramel); }
.fav-excerpt { font-size: 0.82rem; color: var(--forest-60); line-height: 1.7; margin-bottom: 12px; }
.fav-meta { display: flex; justify-content: space-between; align-items: center; }
.fav-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.fav-tag { display: inline-block; padding: 3px 10px; background: var(--cream); border-radius: 999px; font-size: 0.7rem; color: var(--forest-80); font-weight: 500; }
.fav-info { display: flex; align-items: center; gap: 12px; }
.fi-time { font-size: 0.72rem; color: var(--forest-40); }
.fi-remove { background: none; border: none; font-size: 0.72rem; color: var(--caramel); cursor: pointer; font-weight: 500; transition: color 0.2s; }
.fi-remove:hover { color: #e74c3c; }

.empty-box { text-align: center; padding: 48px 0; }
.empty-icon { color: var(--forest-20); margin-bottom: 12px; }
.empty-box p { font-size: 0.9rem; color: var(--forest-40); margin-bottom: 16px; }
.btn-caramel { background: var(--caramel); color: #fff; border: none; padding: 10px 24px; border-radius: 999px; font-size: 0.8rem; font-weight: 500; cursor: pointer; transition: background 0.3s; }
.btn-caramel:hover { background: var(--caramel-light); }

.pagination { margin-top: 24px; display: flex; justify-content: center; }

@media (max-width: 640px) {
  .fav-card { flex-direction: column; }
  .fav-stats { flex-direction: row; }
  .fav-meta { flex-direction: column; align-items: flex-start; gap: 8px; }
}
</style>
