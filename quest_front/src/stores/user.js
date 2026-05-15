import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCurrentUser, login as loginApi, register as registerApi, logout as logoutApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')

  async function fetchUser() {
    try {
      const res = await getCurrentUser()
      user.value = res.data
      return res.data
    } catch (error) {
      user.value = null
      token.value = ''
      localStorage.removeItem('token')
      throw error
    }
  }

  async function login(loginData) {
    const res = await loginApi(loginData)
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    await fetchUser()
    return res
  }

  async function register(registerData) {
    const res = await registerApi(registerData)
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    await fetchUser()
    return res
  }

  async function logout() {
    try {
      await logoutApi()
    } finally {
      user.value = null
      token.value = ''
      localStorage.removeItem('token')
    }
  }

  function isLoggedIn() {
    return !!token.value
  }

  return {
    user,
    token,
    fetchUser,
    login,
    register,
    logout,
    isLoggedIn
  }
})
