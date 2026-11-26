<template>
  <view class="page">
    <view class="profile-card">
      <view class="profile-head">
        <view class="avatar">
          <image v-if="user.avatar" class="avatar-image" :src="avatarUrl" mode="aspectFill" />
          <text v-else class="avatar-text">{{ avatarText }}</text>
        </view>
        <view class="profile-meta">
          <text class="profile-name">{{ displayName }}</text>
          <text class="profile-id">{{ $t('settings.memberId') }} {{ user.id }}</text>
        </view>
      </view>
      <view class="profile-bio">{{ user.bio || $t('profile.bioPlaceholder') }}</view>
    </view>

    <view class="card">
      <view class="row" @click="goProfile">
        <view class="row-left">
          <app-icon name="user" :size="32" color="#2f6b4f" />
          <text class="row-label">{{ $t('profile.title') }}</text>
        </view>
        <text class="row-arrow">›</text>
      </view>
      <view class="row" @click="goFavorites">
        <view class="row-left">
          <app-icon name="bookmark" :size="32" color="#2f6b4f" />
          <text class="row-label">{{ $t('drawer.favorite') }}</text>
        </view>
        <text class="row-arrow">›</text>
      </view>
      <view class="row" @click="goSettings">
        <view class="row-left">
          <app-icon name="lang" :size="32" color="#2f6b4f" />
          <text class="row-label">{{ $t('settings.title') }}</text>
        </view>
        <text class="row-arrow">›</text>
      </view>
    </view>

    <view class="version">{{ $t('settings.version') }}</view>
    <water-tab-bar />
  </view>
</template>

<script>
import AppIcon from '@/components/app-icon.vue'
import WaterTabBar from '@/components/water-tab-bar.vue'
import { getUser } from '@/utils/auth'
import { resolveImageUrl } from '@/utils/format'

export default {
  components: { AppIcon, WaterTabBar },
  data() {
    return {
      user: {},
    }
  },
  onShow() {
    this.user = getUser() || {}
  },
  computed: {
    avatarUrl() {
      return resolveImageUrl(this.user.avatar)
    },
    avatarText() {
      return this.displayName ? this.displayName.slice(-1).toUpperCase() : '茶'
    },
    displayName() {
      return this.user.nickname || this.user.username || ''
    },
  },
  methods: {
    goProfile() {
      uni.navigateTo({ url: '/pages/profile/profile' })
    },
    goFavorites() {
      uni.navigateTo({ url: '/pages/favorites/favorites' })
    },
    goSettings() {
      uni.navigateTo({ url: '/pages/settings/settings' })
    },
  },
}
</script>

<style>
.page {
  padding: 28rpx 28rpx 180rpx;
  background: transparent;
}

/* 顶部个人信息：更大的水滴卡片，高光与立体感更强 */
.profile-card {
  position: relative;
  margin-bottom: 28rpx;
  padding: 44rpx 34rpx 34rpx;
  border: 3rpx solid rgba(255, 255, 255, 0.92);
  border-radius: 64rpx 64rpx 72rpx 28rpx;
  background: linear-gradient(150deg, rgba(255, 255, 255, 0.7) 0%, rgba(255, 255, 255, 0.3) 55%, rgba(226, 243, 232, 0.42) 100%);
  box-shadow:
    inset 6rpx 8rpx 16rpx rgba(255, 255, 255, 0.95),
    inset -10rpx -12rpx 24rpx rgba(70, 130, 95, 0.18),
    0 22rpx 52rpx rgba(30, 70, 48, 0.2);
  backdrop-filter: blur(18rpx) saturate(1.35);
  overflow: hidden;
}

.profile-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 48rpx;
  width: 220rpx;
  height: 16rpx;
  border-radius: 0 0 50% 50%;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0));
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

.profile-head {
  display: flex;
  align-items: center;
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 128rpx;
  height: 128rpx;
  overflow: hidden;
  border: 4rpx solid rgba(255, 255, 255, 0.92);
  border-radius: 50%;
  background: linear-gradient(145deg, #dcefe1, #bcd8c6);
  box-shadow:
    inset 4rpx 6rpx 10rpx rgba(255, 255, 255, 0.9),
    0 10rpx 24rpx rgba(30, 70, 48, 0.22);
}

.avatar-image {
  width: 128rpx;
  height: 128rpx;
}

.avatar-text {
  color: #2f6b4f;
  font-size: 48rpx;
  font-weight: 600;
}

.profile-meta {
  display: flex;
  flex: 1;
  flex-direction: column;
  margin-left: 28rpx;
}

.profile-name {
  color: #26382d;
  font-size: 36rpx;
  font-weight: 600;
}

.profile-id {
  margin-top: 8rpx;
  color: #6f8577;
  font-size: 24rpx;
}

.profile-edit {
  color: #2f6b4f;
  font-size: 24rpx;
}

.profile-bio {
  margin-top: 28rpx;
  padding-top: 24rpx;
  border-top: 2rpx solid rgba(255, 255, 255, 0.7);
  color: #5c7063;
  font-size: 26rpx;
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

.row-arrow {
  color: #b0b8b3;
  font-size: 32rpx;
}

.version {
  margin-top: 20rpx;
  color: #a1aaa4;
  font-size: 22rpx;
  text-align: center;
}
</style>
