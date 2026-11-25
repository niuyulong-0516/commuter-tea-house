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
