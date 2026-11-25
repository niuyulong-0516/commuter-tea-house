const GUEST_ID_KEY = 'tea_guest_id'

// 第一版没有账号体系，用本地生成的访客标识区分作者。
export function getGuestId() {
  let guestId = uni.getStorageSync(GUEST_ID_KEY)
  if (!guestId) {
    guestId = `guest_${Date.now().toString(36)}${Math.random().toString(36).slice(2, 8)}`
    uni.setStorageSync(GUEST_ID_KEY, guestId)
  }
  return guestId
}
