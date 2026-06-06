<template>
  <div class="tags-page">
    <section class="tags-hero">
      <div class="tags-hero-inner">
        <span class="section-eyebrow">Explore Tags</span>
        <h1 class="tags-title">标签</h1>
        <p class="tags-desc">标签用于对问题进行分类，帮助你快速找到感兴趣的内容</p>
        <div class="tags-search">
          <el-input v-model="searchKeyword" placeholder="搜索标签..." size="large" clearable>
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </div>
      </div>
    </section>

    <section class="tags-body">
      <div class="tags-container">
        <div v-loading="loading" class="tags-grid">
          <div v-for="tag in filteredTags" :key="tag.id" class="tag-card" @click="router.push(`/tags/${tag.id}`)">
            <div class="tc-head">
              <span class="tc-name">{{ tag.name }}</span>
              <span class="tc-count">{{ tag.questionCount || 0 }} 个问题</span>
            </div>
            <p class="tc-desc">{{ tag.description || '暂无描述' }}</p>
          </div>
        </div>

        <div v-if="!loading && filteredTags.length === 0" class="empty-box">
          <p>未找到相关标签</p>
        </div>

        <div v-if="total > 0" class="pagination">
          <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="fetchTags" />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getTags } from '@/api/tag'

const router = useRouter()
const tags = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const page = ref(1)
const pageSize = ref(24)
const total = ref(0)

const filteredTags = computed(() => {
  if (!searchKeyword.value) return tags.value
  const k = searchKeyword.value.toLowerCase()
  return tags.value.filter(t => t.name.toLowerCase().includes(k) || (t.description && t.description.toLowerCase().includes(k)))
})

const fetchTags = async () => {
  loading.value = true
  try { const res = await getTags({ page: page.value, size: pageSize.value }); tags.value = res.data.list || []; total.value = res.data.total || 0 }
  catch (e) { console.error(e) }
  finally { loading.value = false }
}

onMounted(() => { fetchTags() })
</script>

<style scoped>
.tags-page { overflow: hidden; }
.tags-hero { background: var(--cream); padding: 100px 0 40px; text-align: center; border-bottom: 1px solid var(--border-light); }
.tags-hero-inner { max-width: 600px; margin: 0 auto; padding: 0 2rem; }
.section-eyebrow { display: block; font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.15em; color: var(--caramel); font-weight: 500; margin-bottom: 8px; }
.tags-title { font-family: 'Noto Serif SC', 'Playfair Display', serif; font-size: 2rem; font-weight: 400; color: var(--forest); margin-bottom: 8px; }
.tags-desc { font-size: 0.9rem; color: var(--forest-60); font-weight: 300; margin-bottom: 20px; }
.tags-search { max-width: 400px; margin: 0 auto; }
.tags-body { padding: 48px 0 100px; }
.tags-container { max-width: 1100px; margin: 0 auto; padding: 0 2rem; }
.tags-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; }
.tag-card { background: #fff; border: 1px solid var(--forest-08); border-radius: 20px; padding: 24px; cursor: pointer; transition: all 0.3s; }
.tag-card:hover { transform: translateY(-3px); box-shadow: 0 16px 48px rgba(26,46,38,0.08); border-color: var(--caramel-light); }
.tc-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.tc-name { font-family: 'Noto Serif SC', serif; font-size: 1rem; font-weight: 500; color: var(--forest); padding: 4px 14px; background: var(--cream-dark); border-radius: 999px; }
.tc-count { font-size: 0.75rem; color: var(--caramel); font-weight: 500; }
.tc-desc { font-size: 0.82rem; color: var(--forest-60); line-height: 1.6; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.empty-box { text-align: center; padding: 60px 0; }
.empty-box p { font-size: 0.9rem; color: var(--forest-40); }
.pagination { margin-top: 32px; display: flex; justify-content: center; }
</style>
