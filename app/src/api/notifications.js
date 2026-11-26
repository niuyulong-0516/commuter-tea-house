import { request } from '@/utils/request'

export function fetchNotifications(page = 0, size = 20) {
  return request({ url: `/api/notifications?page=${page}&size=${size}` })
}

export function fetchUnreadCount() {
  return request({ url: '/api/notifications/unread-count' })
}

export function markNotificationRead(id) {
  return request({ url: `/api/notifications/${id}/read`, method: 'POST' })
}

export function markAllNotificationsRead() {
  return request({ url: '/api/notifications/read-all', method: 'POST' })
}
