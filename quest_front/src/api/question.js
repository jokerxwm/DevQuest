import request from '@/utils/request'

export function getQuestionList(params) {
  return request.get('/questions/list', { params })
}

export function getHotQuestions(params) {
  return request.get('/questions/hot', { params })
}

export function getQuestionDetail(questionId) {
  return request.get(`/questions/${questionId}`)
}

export function createQuestion(data) {
  return request.post('/questions', data)
}

export function updateQuestion(questionId, data) {
  return request.put(`/questions/${questionId}`, data)
}

export function deleteQuestion(questionId) {
  return request.delete(`/questions/${questionId}`)
}

export function getUserQuestions(userId, params) {
  return request.get(`/questions/user/${userId}`, { params })
}
