const BASE_URL = 'http://localhost:9000/api'

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    const method = (options.method || 'GET').toUpperCase()
    const isBodyMethod = ['POST', 'PUT', 'PATCH'].includes(method)

    uni.request({
      url: BASE_URL + options.url,
      method: method,
      data: isBodyMethod ? JSON.stringify(options.data) : options.data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token || ''
      },
      success: (res) => {
        if (res.data.code === 200) {
          resolve(res.data)
        } else {
          uni.showToast({ title: res.data.msg || '请求失败', icon: 'none' })
          if (res.data.code === 401) {
            uni.removeStorageSync('token')
            uni.navigateTo({ url: '/pages/login/login' })
          }
          reject(res.data)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      }
    })
  })
}

export const get = (url, data) => request({ url, method: 'GET', data })
export const post = (url, data) => request({ url, method: 'POST', data })
export const put = (url, data) => request({ url, method: 'PUT', data })
export const del = (url) => request({ url, method: 'DELETE' })

export default request
