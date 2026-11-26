<template>
  <view class="page">
    <view class="card">
      <view class="profile-head">
        <view class="avatar">
          <image v-if="user.avatar" class="avatar-image" :src="avatarUrl" mode="aspectFill" />
          <text v-else class="avatar-text">{{ avatarText }}</text>
        </view>
        <view class="profile-meta">
          <text class="profile-name">{{ displayName }}</text>
          <text class="profile-id">{{ $t('settings.memberId') }} {{ user.id }}</text>
        </view>
        <!-- <text class="profile-edit" @click="goProfile">{{ $t('settings.edit') }}</text> -->
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
  background: #f4f0e6;
}

.card {
  margin-bottom: 24rpx;
  padding: 28rpx;
  border-radius: 28rpx;
  background: #fff;
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
  width: 96rpx;
  height: 96rpx;
  overflow: hidden;
  border-radius: 50%;
  background: #edf4ef;
}

.avatar-image {
  width: 96rpx;
  height: 96rpx;
}

.avatar-text {
  color: #2f6b4f;
  font-size: 36rpx;
  font-weight: 600;
}

.profile-meta {
  display: flex;
  flex: 1;
  flex-direction: column;
  margin-left: 20rpx;
}

.profile-name {
  color: #35433a;
  font-size: 30rpx;
  font-weight: 600;
}

.profile-id {
  margin-top: 6rpx;
  color: #a1aaa4;
  font-size: 22rpx;
}

.profile-edit {
  color: #2f6b4f;
  font-size: 24rpx;
}

.profile-bio {
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 2rpx solid #f3efe6;
  color: #6d7a71;
  font-size: 24rpx;
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
