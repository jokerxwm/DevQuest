import request from '@/utils/request'

export function vote(data) {
  return request.post('/votes', data)
}

export function getUserVote(targetId, targetType) {
  return request.get('/votes/status', { params: { targetId, targetType } })
}
