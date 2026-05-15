import request from '@/utils/request'

export function getTags(params) {
  return request.get('/tags', { params })
}

export function getHotTags() {
  return request.get('/tags/hot')
}
