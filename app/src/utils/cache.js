// 缓存大小计算与清理（H5 下用 localStorage；其它端 uni.getStorageInfoSync）。
// 字符串按 UTF-8 字节数计算，与浏览器存储占用接近。

function byteLength(value) {
  if (value == null) return 0
  const str = String(value)
  let bytes = 0
  for (let i = 0; i < str.length; i += 1) {
    const code = str.charCodeAt(i)
    if (code < 0x80) bytes += 1
    else if (code < 0x800) bytes += 2
    else if (code >= 0xd800 && code <= 0xdbff && i + 1 < str.length) {
      const next = str.charCodeAt(i + 1)
      if (next >= 0xdc00 && next <= 0xdfff) {
        bytes += 4
        i += 1
      } else {
        bytes += 3
      }
    } else {
      bytes += 3
    }
  }
  return bytes
}

export function getCacheSize() {
  // #ifdef H5
  let total = 0
  try {
    for (let i = 0; i < localStorage.length; i += 1) {
      const key = localStorage.key(i)
      total += byteLength(key) + byteLength(localStorage.getItem(key))
    }
  } catch (error) {
    total = 0
  }
  return total
  // #endif
  // #ifndef H5
  try {
    const info = uni.getStorageInfoSync()
    return (info.currentSize || 0) * 1024
  } catch (error) {
    return 0
  }
  // #endif
}

export function formatCacheSize(bytes) {
  if (!bytes) return '0 KB'
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`
}

/** 清理缓存：保留登录态和语言偏好等必要数据，只删图片等临时缓存。 */
export function clearCache() {
  // #ifdef H5
  const keep = new Set([
    'tea_auth_token', 'tea_auth_user', 'tea_pending_transport_type',
    'tea_language', 'data',
  ])
  const removed = []
  try {
    for (let i = localStorage.length - 1; i >= 0; i -= 1) {
      const key = localStorage.key(i)
      if (key && !keep.has(key)) {
        removed.push(key)
        localStorage.removeItem(key)
      }
    }
  } catch (error) {
    // 清理失败时静默
  }
  return removed.length
  // #endif
  // #ifndef H5
  try {
    uni.clearStorageSync()
    return 1
  } catch (error) {
    return 0
  }
  // #endif
}
