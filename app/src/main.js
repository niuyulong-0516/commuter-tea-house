import { createSSRApp } from 'vue'
import App from './App.vue'
import i18n from './i18n'
import { applyPageBackground } from '@/utils/auth'

export function createApp() {
  const app = createSSRApp(App)
  app.use(i18n)

  // 每个页面显示时同步全局背景图（用户换图/切换出行方式后所有页面一致）
  app.mixin({
    onShow() {
      applyPageBackground()
    },
  })

  return {
    app,
  }
}
