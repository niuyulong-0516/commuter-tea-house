<template>
  <view class="page">
    <page-nav-bar title-key="typeSelect.title" />
    <view class="ts-hero">
      <view class="ts-hero-top">
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

    <view class="ts-bottom-bar">
      <view
        class="ts-confirm-button"
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
import PageNavBar from '@/components/page-nav-bar.vue'
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
  components: { AppIcon, PageNavBar },
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
  padding: calc(var(--status-bar-height) + 28rpx) 28rpx calc(180rpx + env(safe-area-inset-bottom));
  background: transparent;
}

.ts-hero {
  position: relative;
  margin: 0;
  padding: 52rpx 40rpx 60rpx;
  border-radius: 56rpx 56rpx 72rpx 28rpx;
  background: linear-gradient(135deg, rgba(47, 107, 79, 0.58), rgba(61, 124, 92, 0.38));
  box-shadow:
    inset 4rpx 6rpx 14rpx rgba(255, 255, 255, 0.28),
    0 20rpx 50rpx rgba(30, 70, 48, 0.22);
  backdrop-filter: blur(16rpx) saturate(1.2);
  overflow: hidden;
}

/* 顶部一道柔和高光，呼应水滴质感 */
.ts-hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 80rpx;
  width: 280rpx;
  height: 18rpx;
  border-radius: 0 0 50% 50%;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.55), rgba(255, 255, 255, 0));
}

.ts-hero-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 104rpx;
  height: 104rpx;
  border: 3rpx solid rgba(255, 255, 255, 0.55);
  border-radius: 50% 50% 50% 50% / 62% 62% 38% 38%;
  background: rgba(255, 255, 255, 0.18);
  box-shadow: inset 4rpx 6rpx 12rpx rgba(255, 255, 255, 0.4);
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
  margin: 24rpx 0 20rpx;
  padding: 20rpx 28rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.85);
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.62);
  box-shadow: inset 3rpx 4rpx 8rpx rgba(255, 255, 255, 0.9), 0 10rpx 24rpx rgba(30, 70, 48, 0.12);
  backdrop-filter: blur(14rpx) saturate(1.2);
  color: #4d6455;
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
  border: 3rpx solid rgba(255, 255, 255, 0.85);
  border-radius: 56rpx 56rpx 64rpx 24rpx;
  background: linear-gradient(150deg, rgba(255, 255, 255, 0.68), rgba(255, 255, 255, 0.3));
  box-shadow:
    inset 5rpx 6rpx 12rpx rgba(255, 255, 255, 0.9),
    inset -8rpx -10rpx 18rpx rgba(70, 130, 95, 0.12),
    0 16rpx 40rpx rgba(30, 70, 48, 0.14);
  backdrop-filter: blur(16rpx) saturate(1.2);
}

.option.active {
  border-color: rgba(47, 107, 79, 0.75);
  background: linear-gradient(150deg, rgba(237, 244, 239, 0.85), rgba(209, 232, 216, 0.6));
  box-shadow:
    inset 5rpx 6rpx 12rpx rgba(255, 255, 255, 0.95),
    inset -8rpx -10rpx 18rpx rgba(47, 107, 79, 0.18),
    0 18rpx 44rpx rgba(47, 107, 79, 0.24);
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

/* 底部托住按钮的玻璃盘，与底部 tabBar 的质感一致 */
.ts-bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 10;
  display: flex;
  justify-content: center;
  padding: 24rpx 28rpx calc(24rpx + env(safe-area-inset-bottom));
  background: linear-gradient(to top, rgba(234, 241, 235, 0.72), rgba(234, 241, 235, 0));
}

.ts-confirm-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 420rpx;
  height: 96rpx;
  border: 3rpx solid rgba(255, 255, 255, 0.8);
  /* 上圆下收的水滴轮廓，像一颗托起的水珠 */
  border-radius: 50% 50% 50% 50% / 72% 72% 36% 36%;
  background: linear-gradient(150deg, rgba(93, 152, 113, 0.85), rgba(47, 107, 79, 0.72));
  box-shadow:
    inset 5rpx 8rpx 14rpx rgba(255, 255, 255, 0.55),
    inset -8rpx -10rpx 18rpx rgba(20, 70, 44, 0.28),
    0 16rpx 36rpx rgba(47, 107, 79, 0.32);
  backdrop-filter: blur(16rpx) saturate(1.3);
  color: #fff;
  font-size: 30rpx;
  font-weight: 600;
  letter-spacing: 2rpx;
}

.ts-confirm-button.disabled {
  background: rgba(160, 176, 166, 0.55);
  box-shadow: inset 4rpx 6rpx 10rpx rgba(255, 255, 255, 0.4);
  color: rgba(255, 255, 255, 0.9);
}
</style>
