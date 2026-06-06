import request from '@/utils/request'

export function searchQuestions(params) {
  return request.get('/search/questions', { params })
}

export function searchSemantic(params) {
  return request.get('/search/semantic', { params })
}
