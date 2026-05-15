import request from '@/utils/request'

export function getAnswers(questionId, params) {
  return request.get(`/answers/question/${questionId}`, { params })
}

export function createAnswer(questionId, data) {
  return request.post(`/answers/${questionId}`, data)
}

export function acceptAnswer(questionId, answerId) {
  return request.post(`/answers/${questionId}/${answerId}/accept`)
}

export function deleteAnswer(questionId, answerId) {
  return request.delete(`/answers/${questionId}/${answerId}`)
}
