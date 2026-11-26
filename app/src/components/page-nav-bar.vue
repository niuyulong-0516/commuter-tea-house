<template>
  <view class="nav-bar">
    <view v-if="showBack" class="back-button" hover-class="back-button-active" @click="goBack">
      <view class="back-arrow"></view>
      <text class="back-text">{{ $t('common.back') }}</text>
    </view>
    <text v-else class="back-placeholder"></text>
    <text class="nav-title">{{ title }}</text>
    <text class="nav-right"></text>
  </view>
</template>

<script>
// 统一透明导航栏：与设置页一致，让全局背景图从顶部透出来。
const TITLES = {
  'pages/publish/publish': 'home.publish',
  'pages/detail/detail': 'detail.title',
  'pages/profile/profile': 'profile.title',
  'pages/favorites/favorites': 'drawer.favorite',
  'pages/history/history': 'drawer.history',
  'pages/my-posts/my-posts': 'drawer.myPosts',
  'pages/drafts/drafts': 'drawer.drafts',
  'pages/feedback/feedback': 'feedback.title',
  'pages/about/about': 'about.title',
  'pages/agreement/agreement': 'agreement.title',
  'pages/mine/mine': 'mine.title',
}

export default {
  props: {
    // 自定义标题；不传则按当前页面路由取 i18n key
    titleKey: { type: String, default: '' },
    // tab 页没有返回按钮
    showBack: { type: Boolean, default: true },
  },
  computed: {
    title() {
      if (this.titleKey) return this.$t(this.titleKey)
      const pages = getCurrentPages()
      const route = pages[pages.length - 1] && pages[pages.length - 1].route
      const key = TITLES[route || '']
      return key ? this.$t(key) : ''
    },
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
  },
}
</script>

<style>
.nav-bar {
  display: flex;
  align-items: center;
  margin-bottom: 28rpx;
  /* H5 下 --status-bar-height 为 0，额外留 20rpx 避免贴顶 */
  padding-top: calc(var(--status-bar-height) + 20rpx);
}

.nav-title {
  flex: 1;
  color: #35433a;
  font-size: 32rpx;
  font-weight: 600;
  text-align: center;
}

.back-placeholder,
.nav-right {
  width: 140rpx;
}

.back-button {
  display: flex;
  align-items: center;
  padding: 12rpx 20rpx 12rpx 14rpx;
  border: 2rpx solid rgba(47, 107, 79, 0.2);
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.85);
  box-shadow: 0 6rpx 18rpx rgba(30, 70, 48, 0.1);
}

.back-button-active {
  background: rgba(241, 245, 242, 0.95);
}

.back-arrow {
  width: 16rpx;
  height: 16rpx;
  margin-right: 10rpx;
  border-bottom: 4rpx solid #2f6b4f;
  border-left: 4rpx solid #2f6b4f;
  transform: rotate(45deg);
}

.back-text {
  color: #2f6b4f;
  font-size: 24rpx;
}
</style>
