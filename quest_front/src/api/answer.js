import request from '@/utils/request'

export function getAnswers(questionId, params) {
  return request.get(`/answers/question/${questionId}`, { params })
}

export function createAnswer(questionId, data) {
  return request.post('/answers', {
    ...data,
    questionId
  })
}

export function acceptAnswer(questionId, answerId) {
  return request.post(`/answers/${answerId}/accept/${questionId}`)
}

export function deleteAnswer(answerId) {
  return request.delete(`/answers/${answerId}`)
}
