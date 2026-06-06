<template>
  <div class="nf-page">
    <div class="nf-container">
      <div class="nf-content">
        <span class="nf-code">404</span>
        <h1 class="nf-title">页面未找到</h1>
        <p class="nf-desc">抱歉，你访问的页面不存在或已被移除</p>
        <div class="nf-actions">
          <button class="btn-caramel" @click="router.push('/')">返回首页</button>
          <button class="btn-outline" @click="router.back()">返回上页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style scoped>
.nf-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: var(--cream-dark); }
.nf-container { max-width: 600px; padding: 2rem; text-align: center; }
.nf-code { font-family: 'Noto Serif SC', 'Playfair Display', serif; font-size: 6rem; font-weight: 300; color: var(--caramel); line-height: 1; display: block; margin-bottom: 16px; }
.nf-title { font-family: 'Noto Serif SC', 'Playfair Display', serif; font-size: 1.8rem; font-weight: 400; color: var(--forest); margin-bottom: 12px; }
.nf-desc { font-size: 0.95rem; color: var(--forest-60); margin-bottom: 32px; font-weight: 300; line-height: 1.7; }
.nf-actions { display: flex; gap: 12px; justify-content: center; }
.btn-caramel { background: var(--caramel); color: #fff; border: none; padding: 14px 32px; border-radius: 999px; font-size: 0.85rem; font-weight: 500; letter-spacing: 0.08em; text-transform: uppercase; cursor: pointer; transition: background 0.3s; }
.btn-caramel:hover { background: var(--caramel-light); }
.btn-outline { background: transparent; color: var(--forest); border: 1.5px solid var(--forest-20); padding: 14px 32px; border-radius: 999px; font-size: 0.85rem; font-weight: 500; cursor: pointer; transition: all 0.3s; }
.btn-outline:hover { border-color: var(--caramel); color: var(--caramel); }
</style>
