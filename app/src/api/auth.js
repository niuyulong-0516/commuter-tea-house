import { request } from '@/utils/request'

export function register(params) {
  return request({
    url: '/api/auth/register',
    method: 'POST',
    data: params,
  })
}

export function login(params) {
  return request({
    url: '/api/auth/login',
    method: 'POST',
    data: params,
  })
}

export function fetchCurrentUser() {
  return request({ url: '/api/auth/me' })
}

export function logout() {
  return request({ url: '/api/auth/logout', method: 'POST' })
}

export function updateTransportType(transportType) {
  return request({
    url: '/api/auth/transport-type',
    method: 'PUT',
    data: { transportType },
  })
}

export function updateProfile(profile) {
  return request({
    url: '/api/auth/profile',
    method: 'PUT',
    data: profile,
  })
}

// 背景图历史：当前一张 + 用过的全部图片
export function fetchBackgrounds() {
  return request({ url: '/api/auth/backgrounds' })
}

// 上传后的图片地址存进历史并设为当前
export function saveBackground(url) {
  return request({ url: '/api/auth/backgrounds', method: 'POST', data: { url } })
}

// 从历史中选一张作为当前背景
export function selectBackground(id) {
  return request({ url: `/api/auth/backgrounds/${id}`, method: 'PUT' })
}

// 恢复默认背景（按出行方式展示内置图）
export function clearBackground() {
  return request({ url: '/api/auth/backgrounds', method: 'DELETE' })
}
