import request from '@/utils/request'

export function addFavorite(questionId) {
  return request.post(`/favorites/${questionId}`)
}

export function removeFavorite(questionId) {
  return request.delete(`/favorites/${questionId}`)
}

export function getFavorites(params) {
  return request.get('/favorites/list', { params })
}

export function isFavorited(questionId) {
  return request.get(`/favorites/check/${questionId}`)
}
