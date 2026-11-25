import { API_BASE_URL } from '@/config'

export function avatarText(post) {
  if (post.anonymous) return '匿'
  const name = post.authorName || '茶客'
  return name.slice(-1)
}

export function resolveImageUrl(url) {
  if (!url) return ''
  if (/^(https?:\/\/|data:)/.test(url)) return url
  return `${API_BASE_URL}${url}`
}

/** 相对时间拆成 key + 参数，交给页面用 $t 渲染，避免工具层写死中文。 */
export function timeAgoParts(value) {
  if (!value) return null
  const time = new Date(String(value).replace('T', ' ').replace(/-/g, '/')).getTime()
  if (Number.isNaN(time)) return null

  const diff = Math.max(0, Date.now() - time)
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diff < minute) return { key: 'time.justNow' }
  if (diff < hour) return { key: 'time.minutesAgo', count: Math.floor(diff / minute) }
  if (diff < day) return { key: 'time.hoursAgo', count: Math.floor(diff / hour) }
  if (diff < 7 * day) return { key: 'time.daysAgo', count: Math.floor(diff / day) }

  const date = new Date(time)
  return { key: 'time.date', month: date.getMonth() + 1, day: date.getDate() }
}
