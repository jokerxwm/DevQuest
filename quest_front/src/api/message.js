import request from '@/utils/request'

export function getConversations(params) {
  return request.get('/messages/conversations', { params })
}

export function getMessages(userId, params) {
  return request.get(`/messages/${userId}`, { params })
}

export function sendMessage(userId, data) {
  return request.post(`/messages/${userId}`, data)
}

export function getUnreadCount() {
  return request.get('/messages/unread-count')
}

export function markAsRead(userId) {
  return request.post(`/messages/${userId}/read`)
}

export function searchUsers(keyword) {
  return request.get('/messages/search-users', { params: { keyword } })
}
