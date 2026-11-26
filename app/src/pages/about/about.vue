<template>
  <view class="page">
    <view class="brand">
      <image class="logo" src="/static/app-logo.png" mode="aspectFit" />
      <text class="app-name">{{ $t('common.appName') }}</text>
      <text class="version">{{ $t('about.version') }} v0.1.0</text>
    </view>

    <view class="card">
      <view class="row" @click="copyEmail">
        <view class="row-left">
          <app-icon name="message" :size="32" color="#2f6b4f" />
          <text class="row-label">{{ $t('about.officialEmail') }}</text>
        </view>
        <view class="row-right">
          <text class="row-value">support@commutertea.app</text>
          <text class="row-arrow">›</text>
        </view>
      </view>
    </view>

    <view class="card">
      <view class="row" @click="goAgreement('terms')">
        <view class="row-left">
          <app-icon name="edit" :size="32" color="#2f6b4f" />
          <text class="row-label">{{ $t('about.terms') }}</text>
        </view>
        <text class="row-arrow">›</text>
      </view>
      <view class="row" @click="goAgreement('privacy')">
        <view class="row-left">
          <app-icon name="user" :size="32" color="#2f6b4f" />
          <text class="row-label">{{ $t('about.privacy') }}</text>
        </view>
        <text class="row-arrow">›</text>
      </view>
    </view>

    <view class="copyright">{{ $t('about.copyright') }}</view>
  </view>
</template>

<script>
import AppIcon from '@/components/app-icon.vue'

export default {
  components: { AppIcon },
  onLoad() {
    uni.setNavigationBarTitle({ title: this.$t('about.title') })
  },
  methods: {
    copyEmail() {
      uni.setClipboardData({
        data: 'support@commutertea.app',
        success: () => uni.showToast({ title: this.$t('about.emailCopied'), icon: 'none' }),
      })
    },
    goAgreement(code) {
      uni.navigateTo({ url: `/pages/agreement/agreement?code=${code}` })
    },
  },
}
</script>

<style>
.page {
  padding: 60rpx 28rpx 80rpx;
  background: transparent;
}

.brand {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60rpx;
}

.logo {
  width: 180rpx;
  height: 180rpx;
}

.app-name {
  margin-top: 24rpx;
  color: #35433a;
  font-size: 36rpx;
  font-weight: 600;
}

.version {
  margin-top: 10rpx;
  color: #a1aaa4;
  font-size: 24rpx;
}

.card {
  margin-bottom: 24rpx;
  padding: 12rpx 28rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.72);
  border: 2rpx solid rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16rpx) saturate(1.2);
  box-shadow: 0 8rpx 28rpx rgba(43, 58, 49, 0.05);
}

.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22rpx 0;
}

.row-left {
  display: flex;
  align-items: center;
}

.row-label {
  margin-left: 16rpx;
  color: #35433a;
  font-size: 28rpx;
}

.row-right {
  display: flex;
  align-items: center;
}

.row-value {
  color: #8e9a92;
  font-size: 24rpx;
}

.row-arrow {
  margin-left: 12rpx;
  color: #b0b8b3;
  font-size: 32rpx;
}

.copyright {
  margin-top: 40rpx;
  color: #a1aaa4;
  font-size: 22rpx;
  text-align: center;
}
</style>
