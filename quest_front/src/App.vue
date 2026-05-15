<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

onMounted(async () => {
  if (userStore.isLoggedIn()) {
    try {
      await userStore.fetchUser()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
})
</script>
