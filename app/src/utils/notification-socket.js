import { reactive } from 'vue'
import { API_BASE_URL, MOCK_MODE } from '@/config'
import { getToken } from '@/utils/auth'
import { fetchUnreadCount } from '@/api/notifications'

// 全局共享：底部 tab 红点和消息页都读这份状态。
export const notificationState = reactive({
  unread: 0,
  // 新收到的通知（WebSocket 推送），消息页据此即时插入列表
  incoming: null,
})

let socket = null
let retryTimer = null
let retryDelay = 3000
let stopped = false

function wsUrl() {
  // https -> wss，http -> ws
  const base = API_BASE_URL.replace(/^http/, 'ws').replace(/\/$/, '')
  const token = encodeURIComponent(getToken() || '')
  return `${base}/ws/notifications?token=${token}`
}

function connect() {
  if (MOCK_MODE || stopped) return
  if (socket) return
  const token = getToken()
  if (!token || !uni.connectSocket) return

  try {
    socket = uni.connectSocket({ url: wsUrl(), success: () => {}, fail: () => {} })

    socket.onOpen(() => {
      retryDelay = 3000
      // 连上后主动拉一次未读数，补上离线期间的消息
      refreshUnread()
    })

    socket.onMessage((res) => {
      try {
        const data = JSON.parse(res.data)
        if (data.type === 'connected') return
        notificationState.unread += 1
        notificationState.incoming = data
      } catch (e) {
        // 忽略无法解析的消息
      }
    })

    socket.onClose(() => {
      socket = null
      scheduleReconnect()
    })

    socket.onError(() => {
      socket = null
      scheduleReconnect()
    })
  } catch (e) {
    scheduleReconnect()
  }
}

function scheduleReconnect() {
  if (retryTimer) return
  retryTimer = setTimeout(() => {
    retryTimer = null
    connect()
  }, retryDelay)
  retryDelay = Math.min(retryDelay * 2, 30000)
}

export function refreshUnread() {
  return fetchUnreadCount()
    .then((data) => {
      notificationState.unread = (data && data.count) || 0
    })
    .catch(() => {})
}

/** 应用启动时调用一次，登录后建立长连接。 */
export function startNotificationSocket() {
  if (MOCK_MODE) return
  stopped = false
  refreshUnread()
  connect()
}

/** 退出登录时断开。 */
export function stopNotificationSocket() {
  stopped = true
  if (retryTimer) {
    clearTimeout(retryTimer)
    retryTimer = null
  }
  if (socket && socket.close) {
    try { socket.close() } catch (e) { /* ignore */ }
  }
  socket = null
  notificationState.unread = 0
  notificationState.incoming = null
}
