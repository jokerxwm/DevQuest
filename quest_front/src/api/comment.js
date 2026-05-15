import request from '@/utils/request'

export function getComments(targetId, targetType, params) {
  return request.get(`/comments/${targetType}/${targetId}`, { params })
}

export function createComment(data) {
  return request.post('/comments', data)
}

export function deleteComment(commentId) {
  return request.delete(`/comments/${commentId}`)
}
