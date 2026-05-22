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

export function getDailyList(params) {
  return request.get('/ai/daily', { params })
}

export function getDailyDetail(dailyId) {
  return request.get(`/ai/daily/${dailyId}`)
}

export function polishText(data) {
  return request.post('/ai/polish', data)
}
