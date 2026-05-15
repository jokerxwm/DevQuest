import request from '@/utils/request'

export function getUserInfo(userId) {
  return request.get(`/users/${userId}`)
}

export function updateUserInfo(data) {
  return request.put('/users/profile', data)
}

export function getUserStats(userId) {
  return request.get(`/users/${userId}/stats`)
}
