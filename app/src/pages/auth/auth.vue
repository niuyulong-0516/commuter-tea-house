<template>
  <view class="page">
    <view class="hero">
      <view class="hero-top">
        <view class="auth-back-button" hover-class="auth-back-active" @click="goBack">
          <view class="auth-back-arrow"></view>
          <text class="auth-back-text">{{ $t('auth.backToType') }}</text>
        </view>
      </view>
      <view class="brand-mark"><app-icon name="tea" :size="48" /></view>
      <text class="brand-title">{{ mode === 'register' ? $t('auth.registerTitle') : $t('auth.loginTitle') }}</text>
      <text class="brand-subtitle">
        {{ mode === 'register' ? $t('auth.registerSubtitle') : $t('auth.loginSubtitle') }}
      </text>
    </view>

    <view class="card">
      <view class="mode-tabs">
        <text
          class="mode-tab"
          :class="{ active: mode === 'register' }"
          @click="switchMode('register')"
        >{{ $t('auth.register') }}</text>
        <text
          class="mode-tab"
          :class="{ active: mode === 'login' }"
          @click="switchMode('login')"
        >{{ $t('auth.login') }}</text>
      </view>

      <view class="field">
        <text class="field-label">{{ $t('auth.username') }}</text>
        <input
          class="field-input"
          v-model="username"
          :placeholder="$t('auth.usernamePlaceholder')"
          placeholder-class="placeholder"
        />
      </view>

      <view class="field">
        <text class="field-label">{{ $t('auth.password') }}</text>
        <input
          class="field-input"
          v-model="password"
          type="password"
          :placeholder="$t('auth.passwordPlaceholder')"
          placeholder-class="placeholder"
        />
      </view>

      <view v-if="mode === 'register'" class="field">
        <text class="field-label">{{ $t('auth.confirmPassword') }}</text>
        <input
          class="field-input"
          v-model="confirmPassword"
          type="password"
          :placeholder="$t('auth.confirmPasswordPlaceholder')"
          placeholder-class="placeholder"
        />
      </view>

      <view class="type-row">
        <text class="type-label">{{ $t('auth.transportType') }}</text>
        <text class="type-value">{{ transportTypeLabel }}</text>
        <text v-if="mode === 'register'" class="type-change" @click="changeType">{{ $t('auth.change') }}</text>
      </view>

      <view
        class="submit-button"
        :class="{ disabled: submitting }"
        @click="submit"
      >
        {{ submitting ? (mode === 'register' ? $t('auth.registering') : $t('auth.loggingIn')) : (mode === 'register' ? $t('auth.registerButton') : $t('auth.loginButton')) }}
      </view>
    </view>

    <view class="foot-note">{{ $t('auth.rule') }}</view>
  </view>
</template>

<script>
import AppIcon from '@/components/app-icon.vue'
import { login, register } from '@/api/auth'
import {
  clearPendingTransportType,
  getPendingTransportType,
  goHome,
  setToken,
  setUser,
} from '@/utils/auth'

export default {
  components: { AppIcon },
  data() {
    return {
      mode: 'register',
      username: '',
      password: '',
      confirmPassword: '',
      submitting: false,
    }
  },
  computed: {
    transportTypeLabel() {
      return getPendingTransportType() === 'BUS'
        ? this.$t('typeSelect.bus')
        : this.$t('typeSelect.rail')
    },
  },
  onLoad() {
    this.updateTitle()
  },
  methods: {
    updateTitle() {
      uni.setNavigationBarTitle({
        title: this.mode === 'register' ? this.$t('auth.register') : this.$t('auth.login'),
      })
    },
    switchMode(mode) {
      if (this.mode === mode) return
      this.mode = mode
      this.password = ''
      this.confirmPassword = ''
      this.updateTitle()
    },
    changeType() {
      this.goBack()
    },
    goBack() {
      uni.reLaunch({ url: '/pages/type-select/type-select' })
    },
    async submit() {
      const username = this.username.trim()
      const password = this.password
      if (!username) {
        uni.showToast({ title: this.$t('auth.usernameRequired'), icon: 'none' })
        return
      }
      if (!password) {
        uni.showToast({ title: this.$t('auth.passwordRequired'), icon: 'none' })
        return
      }
      if (this.mode === 'register') {
        if (password.length < 6) {
          uni.showToast({ title: this.$t('auth.passwordShort'), icon: 'none' })
          return
        }
        if (password !== this.confirmPassword) {
          uni.showToast({ title: this.$t('auth.passwordMismatch'), icon: 'none' })
          return
        }
      }

      this.submitting = true
      uni.showLoading({
        title: this.mode === 'register' ? this.$t('auth.registering') : this.$t('auth.loggingIn'),
        mask: true,
      })
      try {
        const payload = { username, password }
        if (this.mode === 'register') {
          payload.transportType = getPendingTransportType() || 'RAIL'
        }
        const user = this.mode === 'register'
          ? await register(payload)
          : await login(payload)

        setToken(user.token)
        setUser(user)
        clearPendingTransportType()
        uni.hideLoading()
        uni.showToast({
          title: this.mode === 'register'
            ? this.$t('auth.registerSuccess', { id: user.id })
            : this.$t('auth.loginSuccess'),
          icon: 'none',
        })
        setTimeout(goHome, 600)
      } catch (error) {
        uni.hideLoading()
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
  padding: calc(var(--status-bar-height) + 28rpx) 28rpx 60rpx;
  background: transparent;
}

.hero {
  position: relative;
  margin: 0;
  padding: 52rpx 40rpx 60rpx;
  border: 3rpx solid rgba(255, 255, 255, 0.5);
  border-radius: 56rpx 56rpx 72rpx 28rpx;
  background: linear-gradient(135deg, rgba(47, 107, 79, 0.72), rgba(61, 124, 92, 0.55));
  box-shadow:
    inset 5rpx 8rpx 14rpx rgba(255, 255, 255, 0.35),
    inset -8rpx -10rpx 18rpx rgba(20, 70, 44, 0.2),
    0 20rpx 50rpx rgba(30, 70, 48, 0.22);
  backdrop-filter: blur(16rpx) saturate(1.25);
  overflow: hidden;
}

/* 顶部弧光 */
.hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 60rpx;
  width: 260rpx;
  height: 18rpx;
  border-radius: 0 0 50% 50%;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.5), rgba(255, 255, 255, 0));
}

.hero-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28rpx;
}

.auth-back-button {
  display: flex;
  align-items: center;
  padding: 12rpx 24rpx 12rpx 18rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.32);
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.12);
}

.auth-back-active {
  background: rgba(255, 255, 255, 0.22);
}

.auth-back-arrow {
  width: 18rpx;
  height: 18rpx;
  margin-right: 14rpx;
  border-bottom: 4rpx solid #fff;
  border-left: 4rpx solid #fff;
  transform: rotate(45deg);
}

.auth-back-text {
  color: rgba(255, 255, 255, 0.9);
  font-size: 24rpx;
}

.brand-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 88rpx;
  height: 88rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  font-size: 40rpx;
  font-weight: 600;
}

.brand-title {
  display: block;
  margin-top: 24rpx;
  color: #fff;
  font-size: 44rpx;
  font-weight: 600;
}

.brand-subtitle {
  display: block;
  margin-top: 10rpx;
  color: rgba(255, 255, 255, 0.82);
  font-size: 26rpx;
}

.card {
  margin-top: -24rpx;
  padding: 32rpx 28rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.72);
  border: 2rpx solid rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16rpx) saturate(1.2);
  box-shadow: 0 12rpx 36rpx rgba(47, 107, 79, 0.08);
}

.mode-tabs {
  display: flex;
  padding: 8rpx;
  border-radius: 20rpx;
  background: #f5f2ea;
}

.mode-tab {
  flex: 1;
  padding: 18rpx 0;
  border-radius: 16rpx;
  color: #6f7b74;
  font-size: 28rpx;
  text-align: center;
}

.mode-tab.active {
  background: #2f6b4f;
  color: #fff;
  font-weight: 600;
}

.field {
  margin-top: 30rpx;
}

.field-label {
  display: block;
  margin-bottom: 14rpx;
  color: #35433a;
  font-size: 26rpx;
  font-weight: 600;
}

.field-input {
  width: 100%;
  height: 88rpx;
  padding: 0 22rpx;
  border-radius: 18rpx;
  background: #f8f5ee;
  color: #2b352f;
  font-size: 28rpx;
}

.placeholder {
  color: #a7b0aa;
}

.type-row {
  display: flex;
  align-items: center;
  margin-top: 34rpx;
  padding: 24rpx;
  border-radius: 18rpx;
  background: #edf4ef;
}

.type-label {
  color: #6d7a71;
  font-size: 26rpx;
}

.type-value {
  margin-left: 16rpx;
  color: #2f6b4f;
  font-size: 28rpx;
  font-weight: 600;
}

.type-change {
  margin-left: auto;
  color: #2f6b4f;
  font-size: 26rpx;
  text-decoration: underline;
}

.submit-button {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 92rpx;
  margin-top: 40rpx;
  border-radius: 999rpx;
  background: #2f6b4f;
  color: #fff;
  font-size: 30rpx;
  font-weight: 600;
}

.submit-button.disabled {
  background: #b8c4bc;
  color: #f4f0e6;
}

.foot-note {
  margin-top: 36rpx;
  color: #a1aaa4;
  font-size: 24rpx;
  text-align: center;
}
</style>
