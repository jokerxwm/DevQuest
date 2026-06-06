import request from '@/utils/request'

export function submitReport(data) {
  return request.post('/reports', data)
}
