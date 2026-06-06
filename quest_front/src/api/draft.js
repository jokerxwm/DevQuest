import request from '@/utils/request'

export function getDrafts(params) {
  return request.get('/drafts', { params })
}

export function saveDraft(data) {
  return request.post('/drafts', data)
}

export function updateDraft(draftId, data) {
  return request.put(`/drafts/${draftId}`, data)
}

export function deleteDraft(draftId) {
  return request.delete(`/drafts/${draftId}`)
}

export function getDraftDetail(draftId) {
  return request.get(`/drafts/${draftId}`)
}
