import request from '@/utils/request'

export function login(data) {
  return request.post('/admin/auth/login', data)
}

export function getDashboardStats() {
  return request.get('/admin/dashboard/stats')
}

export function getTags(params) {
  return request.get('/admin/tags', { params })
}

export function createTag(data) {
  return request.post('/admin/tags', data)
}

export function updateTag(id, data) {
  return request.put(`/admin/tags/${id}`, data)
}

export function deleteTag(id) {
  return request.delete(`/admin/tags/${id}`)
}

export function getUsers(params) {
  return request.get('/admin/users', { params })
}

export function updateUserStatus(userId, data) {
  return request.put(`/admin/users/${userId}/status`, data)
}

export function updateUserRole(userId, data) {
  return request.put(`/admin/users/${userId}/role`, data)
}

export function getQuestions(params) {
  return request.get('/admin/questions', { params })
}

export function updateQuestionStatus(questionId, data) {
  return request.put(`/admin/questions/${questionId}/status`, data)
}

export function toggleQuestionTop(questionId) {
  return request.put(`/admin/questions/${questionId}/top`)
}

export function deleteQuestion(questionId) {
  return request.delete(`/admin/questions/${questionId}`)
}

export function getAnswers(params) {
  return request.get('/admin/answers', { params })
}

export function updateAnswerStatus(answerId, data) {
  return request.put(`/admin/answers/${answerId}/status`, data)
}

export function deleteAnswer(answerId) {
  return request.delete(`/admin/answers/${answerId}`)
}
