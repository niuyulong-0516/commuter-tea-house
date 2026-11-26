import { createSSRApp } from 'vue'
import App from './App.vue'
import i18n from './i18n'
import { applyPageBackground, getToken } from '@/utils/auth'
import { startNotificationSocket, refreshUnread } from '@/utils/notification-socket'

export function createApp() {
  const app = createSSRApp(App)
  app.use(i18n)

  // 每个页面显示时同步全局背景图（用户换图/切换出行方式后所有页面一致），并刷新未读数
  app.mixin({
    onShow() {
      applyPageBackground()
      if (getToken()) refreshUnread()
    },
  })

  // 登录后启动通知长连接（ WebSocket 推送）
  if (getToken()) {
    startNotificationSocket()
  }

  return {
    app,
  }
}
