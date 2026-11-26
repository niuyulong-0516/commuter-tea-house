<template>
  <view class="page">
    <view class="nav-bar">
      <view class="back-button" hover-class="back-button-active" @click="goBack">
        <view class="back-arrow"></view>
        <text class="back-text">{{ $t('common.back') }}</text>
      </view>
      <text class="nav-title">{{ $t('settings.title') }}</text>
    </view>

    <view class="card">
      <view class="section-title">{{ $t('settings.general') }}</view>
      <view class="row" @click="goBackground">
        <view class="row-left">
          <app-icon name="image" :size="32" color="#2f6b4f" />
          <text class="row-label">{{ $t('settings.background') }}</text>
        </view>
        <text class="row-arrow">›</text>
      </view>
      <view class="row" @click="goTransport">
        <view class="row-left">
          <app-icon :name="transportType === 'BUS' ? 'bus' : 'rail'" :size="32" color="#2f6b4f" />
          <text class="row-label">{{ $t('settings.transport') }}</text>
        </view>
        <view class="row-right">
          <text class="row-value">{{ transportType === 'BUS' ? $t('typeSelect.bus') : $t('typeSelect.rail') }}</text>
          <text class="row-arrow">›</text>
        </view>
      </view>
      <view class="row" @click="clearCacheConfirm">
        <view class="row-left">
          <app-icon name="trash" :size="32" color="#2f6b4f" />
          <text class="row-label">{{ $t('settings.clearCache') }}</text>
        </view>
        <view class="row-right">
          <text class="row-value">{{ cacheSizeText }}</text>
          <text class="row-arrow">›</text>
        </view>
      </view>
      <view class="row" @click="goFeedback">
        <view class="row-left">
          <app-icon name="message" :size="32" color="#2f6b4f" />
          <text class="row-label">{{ $t('settings.helpFeedback') }}</text>
        </view>
        <text class="row-arrow">›</text>
      </view>
      <view class="row" @click="goAbout">
        <view class="row-left">
          <app-icon name="tea" :size="32" color="#2f6b4f" />
          <text class="row-label">{{ $t('about.title') }}</text>
        </view>
        <text class="row-arrow">›</text>
      </view>
      <view class="row" @click="showLanguage = !showLanguage">
        <view class="row-left">
          <app-icon name="lang" :size="32" color="#2f6b4f" />
          <text class="row-label">{{ $t('settings.language') }}</text>
        </view>
        <view class="row-right">
          <text class="row-value">{{ currentLabel }}</text>
          <text class="row-arrow">›</text>
        </view>
      </view>
      <view v-if="showLanguage" class="lang-grid">
        <view
          v-for="item in languages"
          :key="item.code"
          class="lang-item"
          :class="{ active: currentCode === item.code }"
          @click="changeLanguage(item.code)"
        >
          <text class="lang-label">{{ item.label }}</text>
          <text v-if="currentCode === item.code" class="lang-check">✓</text>
        </view>
      </view>
    </view>

    <view class="logout-button" @click="logout">{{ $t('home.logout') }}</view>

    <view class="version">{{ $t('settings.version') }}</view>
  </view>
</template>

<script>
import AppIcon from '@/components/app-icon.vue'
import { languages, currentLanguage, setLanguage } from '@/i18n'
import { logout as logoutApi } from '@/api/auth'
import { getTransportType, getUser, goEntry } from '@/utils/auth'
import { getCacheSize, formatCacheSize, clearCache } from '@/utils/cache'

export default {
  components: { AppIcon },
  data() {
    return {
      languages,
      currentCode: currentLanguage(),
      transportType: 'RAIL',
      showLanguage: false,
      cacheSize: 0,
    }
  },
  computed: {
    cacheSizeText() {
      return formatCacheSize(this.cacheSize)
    },
    currentLabel() {
      const item = languages.find((item) => item.code === this.currentCode)
      return item ? item.label : ''
    },
  },
  onShow() {
    const user = getUser() || {}
    this.transportType = user.transportType || getTransportType()
    this.cacheSize = getCacheSize()
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    clearCacheConfirm() {
      uni.showModal({
        title: this.$t('settings.clearCacheTitle'),
        content: this.$t('settings.clearCacheContent'),
        success: (res) => {
          if (!res.confirm) return
          clearCache()
          this.cacheSize = getCacheSize()
          uni.showToast({ title: this.$t('settings.cacheCleared'), icon: 'none' })
        },
      })
    },
    goFeedback() {
      uni.navigateTo({ url: '/pages/feedback/feedback' })
    },
    goAbout() {
      uni.navigateTo({ url: '/pages/about/about' })
    },
    changeLanguage(code) {
      if (this.currentCode === code) return
      setLanguage(code)
      this.currentCode = code
      uni.showToast({ title: this.$t('settings.changed'), icon: 'none' })
    },
    goTransport() {
      uni.navigateTo({ url: '/pages/type-select/type-select?from=settings' })
    },
    goBackground() {
      uni.navigateTo({ url: '/pages/background/background' })
    },
    async logout() {
      try {
        await logoutApi()
      } catch (error) {
        // 接口失败也要清掉本地登录态
      }
      goEntry()
    },
  },
}
</script>

<style>
.page {
  padding: 28rpx 28rpx 80rpx;
  background: transparent;
}

.nav-bar {
  display: flex;
  align-items: center;
  margin-bottom: 28rpx;
  /* custom 导航栏下留出状态栏高度，H5 下该变量为 0 */
  padding-top: var(--status-bar-height);
}

.nav-title {
  flex: 1;
  margin-right: 100rpx;
  color: #35433a;
  font-size: 32rpx;
  font-weight: 600;
  text-align: center;
}

.back-button {
  display: flex;
  align-items: center;
  padding: 12rpx 20rpx 12rpx 14rpx;
  border: 2rpx solid rgba(47, 107, 79, 0.2);
  border-radius: 999rpx;
  background: #fff;
}

.back-button-active {
  background: #f1f5f2;
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

.card {
  margin-bottom: 24rpx;
  padding: 28rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.72);
  border: 2rpx solid rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16rpx) saturate(1.2);
  box-shadow: 0 8rpx 28rpx rgba(43, 58, 49, 0.05);
}

.section-title {
  display: block;
  margin-bottom: 12rpx;
  color: #6d7a71;
  font-size: 24rpx;
  font-weight: 600;
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
  font-size: 26rpx;
}

.row-arrow {
  margin-left: 12rpx;
  color: #b0b8b3;
  font-size: 32rpx;
}

.lang-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
  padding-bottom: 12rpx;
}

.lang-item {
  display: flex;
  align-items: center;
  min-width: calc((100% - 14rpx) / 2);
  padding: 20rpx 16rpx;
  border: 2rpx solid #eee9df;
  border-radius: 16rpx;
  background: #fbf9f4;
}

.lang-item.active {
  border-color: #2f6b4f;
  background: #edf4ef;
}

.lang-label {
  flex: 1;
  color: #35433a;
  font-size: 26rpx;
}

.lang-item.active .lang-label {
  color: #2f6b4f;
  font-weight: 600;
}

.lang-check {
  color: #2f6b4f;
  font-size: 26rpx;
}

.logout-button {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 88rpx;
  border-radius: 999rpx;
  background: #fff;
  color: #b56b42;
  font-size: 28rpx;
  font-weight: 600;
  box-shadow: 0 8rpx 28rpx rgba(43, 58, 49, 0.05);
}

.version {
  margin-top: 20rpx;
  color: #a1aaa4;
  font-size: 22rpx;
  text-align: center;
}
</style>
