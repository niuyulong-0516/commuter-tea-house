import { API_BASE_URL, MOCK_MODE } from '@/config'
import { clearAuth, getToken } from '@/utils/auth'
import { mockRequest, mockUploadImage } from '@/utils/mock'
import i18n from '@/i18n'

// 后端 message 是中文，前端按原文映射到 i18n key；后端文案改动时只需更新这里。
const errorKeyMap = {
  '请先登录': 'errors.loginRequired',
  '内容不能为空': 'errors.contentRequired',
  '内容最多 500 个字': 'errors.contentTooLong',
  '分类不正确': 'errors.invalidCategory',
  '出行类型不正确': 'errors.invalidTransportType',
  '这杯茶暂时找不到了': 'errors.postNotFound',
  '用户名或密码不正确': 'errors.invalidCredentials',
  '这个用户名已经有人用了': 'errors.usernameTaken',
  '用户名长度 3-32 位': 'errors.usernameLength',
  '密码长度 6-64 位': 'errors.passwordLength',
  '图片太大了，单张请控制在 10MB 以内': 'errors.imageTooLarge',
  '请求格式不正确': 'errors.badRequest',
  '茶馆暂时打烊了，请稍后再试': 'errors.serverError',
  '请求失败，请稍后再试': 'errors.requestFailed',
  '图片上传失败，请检查网络': 'errors.imageUploadFailed',
  '用户不存在': 'errors.loginRequired',
  '网络不太顺畅，请检查后端是否启动': 'errors.network',
}

function errorMessage(data, fallback) {
  if (!data) return fallback
  const message = typeof data === 'string' ? data : (data.message || data.error || '')
  const key = errorKeyMap[message]
  return key ? i18n.global.t(key) : (message || fallback)
}

function handleUnauthorized() {
  clearAuth()
  uni.reLaunch({ url: '/pages/type-select/type-select' })
}

export function request({ url, method = 'GET', data, header = {} }) {
  if (MOCK_MODE) return mockRequest({ url, method, data, header })
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${API_BASE_URL}${url}`,
      method,
      data,
      timeout: 15000,
      header: {
        'Content-Type': 'application/json',
        'X-Auth-Token': getToken(),
        ...header,
      },
      success(res) {
        if (res.statusCode === 401) {
          handleUnauthorized()
          reject(new Error(i18n.global.t('errors.unauthorized')))
          return
        }
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(res.data)
          return
        }
        reject(new Error(errorMessage(res.data, i18n.global.t('errors.requestFailed'))))
      },
      fail() {
        reject(new Error(i18n.global.t('errors.network')))
      },
    })
  })
}

export function uploadImage(filePath) {
  if (MOCK_MODE) return mockUploadImage()
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: `${API_BASE_URL}/api/uploads/images`,
      filePath,
      name: 'file',
      header: {
        'X-Auth-Token': getToken(),
      },
      success(res) {
        if (res.statusCode === 401) {
          handleUnauthorized()
          reject(new Error(i18n.global.t('errors.unauthorized')))
          return
        }
        let data = res.data
        try {
          data = JSON.parse(data)
        } catch (error) {
          // 保留原始响应，下面按状态码处理
        }
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(data)
          return
        }
        reject(new Error(errorMessage(data, i18n.global.t('errors.imageUploadFailed'))))
      },
      fail() {
        reject(new Error(i18n.global.t('errors.imageUploadFailed')))
      },
    })
  })
}
