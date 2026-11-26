<script>
import { isLoggedIn } from '@/utils/auth'
import { detectLocale } from '@/api/locale'
import { applyDetectedLanguage } from '@/i18n'

export default {
  onLaunch() {
    // 按 IP 探测默认语言；用户已在设置页手动选过时 applyDetectedLanguage 会跳过。
    detectLocale()
      .then((data) => applyDetectedLanguage(data && data.language))
      .catch(() => {})

    // 未登录时默认落在类型选择页，已登录直接进首页。
    if (isLoggedIn()) {
      setTimeout(() => {
        uni.reLaunch({ url: '/pages/index/index' })
      }, 0)
    }
  },
}
</script>

<style>
/* 隐藏滚动条，页面仍可正常滚动。uni-app H5 的滚动容器是 uni-page-body，需单独覆盖。 */
::-webkit-scrollbar,
html::-webkit-scrollbar,
body::-webkit-scrollbar,
page::-webkit-scrollbar,
uni-app::-webkit-scrollbar,
uni-page-wrapper::-webkit-scrollbar,
uni-page-body::-webkit-scrollbar,
uni-page::-webkit-scrollbar,
scroll-view::-webkit-scrollbar {
  display: none !important;
  width: 0 !important;
  height: 0 !important;
}

html,
body,
page,
uni-app,
uni-page-wrapper,
uni-page-body,
uni-page,
scroll-view {
  scrollbar-width: none !important;
  -ms-overflow-style: none !important;
}

/* 页面高度由内容决定；背景放在 html 上，不足一屏时也能铺满 */
html,
body {
  background-color: #eaf1eb;
}

/* 全局页面背景：由 utils/auth.js 的 applyPageBackground 写入 --tea-page-bg，
   所有页面共用同一张图，固定在底层不随滚动 */
page,
uni-page-body {
  background-color: #eaf1eb;
  background-image: var(--tea-page-bg, none);
  background-size: cover;
  background-position: center top;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

page {
  color: #1f2a24;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', sans-serif;
}

view,
text,
image,
button,
textarea,
input,
scroll-view {
  box-sizing: border-box;
}

button::after {
  border: none;
}

/* 隐藏原生 tabBar，改用页面内的水滴组件（switchTab 逻辑仍走原生） */
.uni-tabbar,
uni-tabbar {
  display: none !important;
}
</style>
