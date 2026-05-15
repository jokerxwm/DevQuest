import request from '@/utils/request'

export function chat(data) {
  return request.post('/ai/chat', data)
}

export function getHistory() {
  return request.get('/ai/history')
}

export function getConversation(conversationId) {
  return request.get(`/ai/conversation/${conversationId}`)
}
