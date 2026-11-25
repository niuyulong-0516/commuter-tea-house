<template>
  <view class="page">
    <view class="hero">
      <view class="hero-top">
        <view class="brand-mark"><app-icon name="tea" :size="52" /></view>
      </view>
      <text class="brand-title">{{ $t('common.appName') }}</text>
      <text class="brand-subtitle">{{ $t('typeSelect.subtitle') }}</text>
    </view>

    <view class="guide">
      <text>{{ $t('typeSelect.guide') }}</text>
    </view>

    <view class="options">
      <view
        class="option"
        :class="{ active: selected === 'RAIL' }"
        @click="choose('RAIL')"
      >
        <view class="option-icon icon-rail"><app-icon name="rail" :size="52" /></view>
        <view class="option-copy">
          <text class="option-title">{{ $t('typeSelect.rail') }}</text>
          <text class="option-desc">{{ $t('typeSelect.railDesc') }}</text>
        </view>
        <view v-if="selected === 'RAIL'" class="option-checked">✓</view>
      </view>

      <view
        class="option"
        :class="{ active: selected === 'BUS' }"
        @click="choose('BUS')"
      >
        <view class="option-icon icon-bus"><app-icon name="bus" :size="52" /></view>
        <view class="option-copy">
          <text class="option-title">{{ $t('typeSelect.bus') }}</text>
          <text class="option-desc">{{ $t('typeSelect.busDesc') }}</text>
        </view>
        <view v-if="selected === 'BUS'" class="option-checked">✓</view>
      </view>
    </view>

    <view class="bottom-bar">
      <view
        class="confirm-button"
        :class="{ disabled: !selected || submitting }"
        @click="confirm"
      >
        {{ submitting ? $t('typeSelect.saving') : (fromSettings ? $t('typeSelect.save') : $t('typeSelect.continueRegister')) }}
      </view>
    </view>
  </view>
</template>

<script>
import AppIcon from '@/components/app-icon.vue'
import {
  getPendingTransportType,
  getUser,
  isLoggedIn,
  setPendingTransportType,
  updateLocalUser,
  goHome,
} from '@/utils/auth'
import { updateTransportType } from '@/api/auth'

export default {
  components: { AppIcon },
  data() {
    return {
      selected: '',
      fromSettings: false,
      submitting: false,
    }
  },
  onLoad(options) {
    this.fromSettings = options.from === 'settings'
    uni.setNavigationBarTitle({ title: this.$t('typeSelect.title') })
    if (isLoggedIn() && !this.fromSettings) {
      goHome()
      return
    }
    const user = getUser()
    this.selected = isLoggedIn()
      ? (user && user.transportType) || ''
      : getPendingTransportType()
  },
  methods: {
    choose(type) {
      this.selected = type
    },
    async confirm() {
      if (!this.selected || this.submitting) return
      if (!isLoggedIn()) {
        setPendingTransportType(this.selected)
        uni.redirectTo({ url: '/pages/auth/auth' })
        return
      }

      this.submitting = true
      try {
        const user = await updateTransportType(this.selected)
        updateLocalUser(user)
        uni.showToast({ title: this.$t('typeSelect.saved'), icon: 'none' })
        setTimeout(() => uni.navigateBack(), 500)
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
      } finally {
        this.submitting = false
      }
    },
  },
}
</script>

<style>
.page {
  padding: 0 28rpx calc(180rpx + env(safe-area-inset-bottom));
  background: #f4f0e6;
}

.hero {
  padding: 40rpx 32rpx 64rpx;
  border-radius: 0 0 40rpx 40rpx;
  background: linear-gradient(135deg, #2f6b4f 0%, #3d7c5c 100%);
}

.hero-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 96rpx;
  height: 96rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  font-size: 44rpx;
  font-weight: 600;
}

.brand-title {
  display: block;
  margin-top: 28rpx;
  color: #fff;
  font-size: 48rpx;
  font-weight: 600;
  letter-spacing: 2rpx;
}

.brand-subtitle {
  display: block;
  margin-top: 12rpx;
  color: rgba(255, 255, 255, 0.82);
  font-size: 26rpx;
}

.guide {
  padding: 40rpx 16rpx 20rpx;
  color: #8e9a92;
  font-size: 26rpx;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.option {
  display: flex;
  align-items: center;
  min-height: 180rpx;
  padding: 32rpx;
  border: 2rpx solid #eee9df;
  border-radius: 28rpx;
  background: #fff;
  box-shadow: 0 8rpx 28rpx rgba(43, 58, 49, 0.05);
}

.option.active {
  border-color: #2f6b4f;
  background: #edf4ef;
}

.option-icon {
  display: flex;
  flex: none;
  align-items: center;
  justify-content: center;
  width: 96rpx;
  height: 96rpx;
  margin-right: 24rpx;
  border-radius: 24rpx;
  color: #fff;
  font-size: 40rpx;
  font-weight: 600;
}

.icon-rail {
  background: #2f6b4f;
}

.icon-bus {
  background: #b2774f;
}

.option-copy {
  display: flex;
  flex: 1;
  flex-direction: column;
}

.option-title {
  color: #29342d;
  font-size: 32rpx;
  font-weight: 600;
}

.option-desc {
  margin-top: 8rpx;
  color: #8e9a92;
  font-size: 24rpx;
}

.option-checked {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background: #2f6b4f;
  color: #fff;
  font-size: 28rpx;
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 10;
  padding: 20rpx 28rpx calc(20rpx + env(safe-area-inset-bottom));
  background: rgba(244, 240, 230, 0.96);
}

.confirm-button {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 92rpx;
  border-radius: 999rpx;
  background: #2f6b4f;
  color: #fff;
  font-size: 30rpx;
  font-weight: 600;
}

.confirm-button.disabled {
  background: #b8c4bc;
  color: #f4f0e6;
}
</style>
