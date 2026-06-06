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

export function changePassword(data) {
  return request.put('/users/password', data)
}

export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/users/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getUserAnswers(userId, params) {
  return request.get(`/answers/user/${userId}`, { params })
}

export function getFollowers(userId, params) {
  return request.get(`/users/${userId}/followers`, { params })
}

export function getFollowing(userId, params) {
  return request.get(`/users/${userId}/following`, { params })
}

export function followUser(userId) {
  return request.post(`/users/${userId}/follow`)
}

export function unfollowUser(userId) {
  return request.delete(`/users/${userId}/follow`)
}
