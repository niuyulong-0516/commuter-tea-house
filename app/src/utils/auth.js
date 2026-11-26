// 登录态与出行偏好统一放在这里管理，页面不要直接读写 storage 的 key。
const TOKEN_KEY = 'tea_auth_token'
const USER_KEY = 'tea_auth_user'
const PENDING_TYPE_KEY = 'tea_pending_transport_type'

export function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
}

export function getUser() {
  const raw = uni.getStorageSync(USER_KEY)
  if (!raw) return null
  try {
    return typeof raw === 'string' ? JSON.parse(raw) : raw
  } catch (error) {
    return null
  }
}

export function setUser(user) {
  uni.setStorageSync(USER_KEY, JSON.stringify(user))
}

export function updateLocalUser(user) {
  setUser({ ...(getUser() || {}), ...user })
}

export function clearAuth() {
  uni.removeStorageSync(TOKEN_KEY)
  uni.removeStorageSync(USER_KEY)
}

export function isLoggedIn() {
  return !!getToken()
}

// 注册前先选好的出行类型，注册完成后清掉。
export function getPendingTransportType() {
  return uni.getStorageSync(PENDING_TYPE_KEY) || ''
}

export function setPendingTransportType(type) {
  uni.setStorageSync(PENDING_TYPE_KEY, type)
}

export function clearPendingTransportType() {
  uni.removeStorageSync(PENDING_TYPE_KEY)
}

export function getTransportType() {
  const user = getUser()
  return (user && user.transportType) || getPendingTransportType() || 'RAIL'
}

// 内置默认背景：按出行方式取图；用户手动设置过则以用户选择的为准。
const DEFAULT_BACKGROUNDS = {
  RAIL: '/static/backgrounds/bg-rail.jpg',
  BUS: '/static/backgrounds/bg-bus.jpg',
}

export function getBackgroundUrl() {
  const user = getUser()
  if (user && user.backgroundImage) return user.backgroundImage
  return DEFAULT_BACKGROUNDS[getTransportType()] || DEFAULT_BACKGROUNDS.RAIL
}

export function goHome() {
  uni.reLaunch({ url: '/pages/index/index' })
}

export function goEntry() {
  clearAuth()
  clearPendingTransportType()
  uni.reLaunch({ url: '/pages/type-select/type-select' })
}
