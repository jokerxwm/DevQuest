import { reactive } from 'vue'
import { get, post } from '@/utils/request'

const state = reactive({
  token: uni.getStorageSync('token') || '',
  user: JSON.parse(uni.getStorageSync('user') || 'null'),
  isLogin: !!uni.getStorageSync('token')
})

export const login = async (username, password) => {
  const res = await post('/auth/login', { email: username, password })
  state.token = res.data.token
  state.user = res.data.user
  state.isLogin = true
  uni.setStorageSync('token', res.data.token)
  uni.setStorageSync('user', JSON.stringify(res.data.user))
  return res.data
}

export const register = async (data) => {
  const res = await post('/auth/register', data)
  state.token = res.data.token
  state.user = res.data.user
  state.isLogin = true
  uni.setStorageSync('token', res.data.token)
  uni.setStorageSync('user', JSON.stringify(res.data.user))
  return res.data
}

export const fetchUser = async () => {
  const res = await get('/auth/me')
  state.user = res.data
  uni.setStorageSync('user', JSON.stringify(res.data))
  return res.data
}

export const logout = () => {
  state.token = ''
  state.user = null
  state.isLogin = false
  uni.removeStorageSync('token')
  uni.removeStorageSync('user')
}

export default { state, login, register, fetchUser, logout }
