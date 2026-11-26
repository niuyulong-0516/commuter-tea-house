<template>
  <view class="page">
    <page-nav-bar title-key="profile.title" />
    <view class="card">
      <view class="avatar-row" @click="chooseAvatar">
        <view class="avatar">
          <image v-if="profile.avatar" class="avatar-image" :src="avatarUrl" mode="aspectFill" />
          <text v-else class="avatar-text">{{ avatarText }}</text>
        </view>
        <view class="avatar-meta">
          <text class="avatar-title">{{ $t('profile.avatar') }}</text>
          <text class="avatar-hint">{{ $t('profile.avatarHint') }}</text>
        </view>
        <text class="row-arrow">›</text>
      </view>
    </view>

    <view class="card">
      <view class="field">
        <text class="field-label">{{ $t('profile.nickname') }}</text>
        <input
          class="field-input"
          v-model="profile.nickname"
          :placeholder="$t('profile.nicknamePlaceholder')"
          placeholder-class="placeholder"
          maxlength="32"
        />
      </view>

      <view class="field">
        <text class="field-label">{{ $t('settings.gender') }}</text>
        <view class="gender-row">
          <text
            v-for="item in genders"
            :key="item.value"
            class="gender-item"
            :class="{ active: profile.gender === item.value }"
            @click="profile.gender = item.value"
          >{{ $t(item.label) }}</text>
        </view>
      </view>

      <view class="field">
        <text class="field-label">{{ $t('settings.bio') }}</text>
        <input
          class="field-input"
          v-model="profile.bio"
          :placeholder="$t('settings.bioPlaceholder')"
          placeholder-class="placeholder"
          maxlength="100"
        />
      </view>
    </view>

    <view class="save-button" @click="saveProfile">{{ $t('settings.save') }}</view>
  </view>
</template>

<script>
import { updateProfile as updateProfileApi } from '@/api/auth'
import PageNavBar from '@/components/page-nav-bar.vue'
import { uploadPostImage } from '@/api/posts'
import { getUser, setUser } from '@/utils/auth'
import { resolveImageUrl } from '@/utils/format'

export default {
  components: { PageNavBar },
  data() {
    return {
      profile: { avatar: '', nickname: '', gender: 'SECRET', bio: '' },
      genders: [
        { value: 'MALE', label: 'settings.male' },
        { value: 'FEMALE', label: 'settings.female' },
        { value: 'SECRET', label: 'settings.secret' },
      ],
    }
  },
  onLoad() {
    const user = getUser() || {}
    this.profile = {
      avatar: user.avatar || '',
      nickname: user.nickname || user.username || '',
      gender: user.gender || 'SECRET',
      bio: user.bio || '',
    }
    uni.setNavigationBarTitle({ title: this.$t('profile.title') })
  },
  computed: {
    avatarUrl() {
      return resolveImageUrl(this.profile.avatar)
    },
    avatarText() {
      return this.profile.nickname ? this.profile.nickname.slice(-1).toUpperCase() : '茶'
    },
  },
  methods: {
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          const path = res.tempFilePaths[0]
          if (!path) return
          uni.showLoading({ title: this.$t('common.loading'), mask: true })
          try {
            const result = await uploadPostImage(path)
            if (result && result.url) this.profile.avatar = result.url
          } catch (error) {
            uni.showToast({ title: error.message, icon: 'none' })
          } finally {
            uni.hideLoading()
          }
        },
      })
    },
    async saveProfile() {
      const nickname = this.profile.nickname.trim()
      if (!nickname) {
        uni.showToast({ title: this.$t('profile.nicknameRequired'), icon: 'none' })
        return
      }
      uni.showLoading({ title: this.$t('common.loading'), mask: true })
      try {
        const user = await updateProfileApi({
          avatar: this.profile.avatar,
          nickname,
          gender: this.profile.gender,
          bio: this.profile.bio.trim(),
        })
        setUser({ ...(getUser() || {}), ...user })
        this.profile.nickname = nickname
        uni.showToast({ title: this.$t('settings.saved'), icon: 'none' })
        setTimeout(() => uni.navigateBack(), 500)
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },
  },
}
</script>

<style>
.page {
  padding: 28rpx;
  background: transparent;
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

.avatar-row {
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

.avatar-meta {
  display: flex;
  flex: 1;
  flex-direction: column;
  margin-left: 20rpx;
}

.avatar-title {
  color: #35433a;
  font-size: 28rpx;
}

.avatar-hint {
  margin-top: 6rpx;
  color: #a1aaa4;
  font-size: 22rpx;
}

.row-arrow {
  color: #b0b8b3;
  font-size: 32rpx;
}

.field {
  margin-top: 28rpx;
}

.field:first-child {
  margin-top: 0;
}

.field-label {
  display: block;
  margin-bottom: 14rpx;
  color: #6d7a71;
  font-size: 24rpx;
}

.field-input {
  width: 100%;
  height: 80rpx;
  padding: 0 20rpx;
  border-radius: 16rpx;
  background: #f8f5ee;
  color: #2b352f;
  font-size: 26rpx;
}

.placeholder {
  color: #a7b0aa;
}

.gender-row {
  display: flex;
  gap: 14rpx;
}

.gender-item {
  padding: 12rpx 28rpx;
  border: 2rpx solid #eee9df;
  border-radius: 999rpx;
  background: #fbf9f4;
  color: #6f7b74;
  font-size: 26rpx;
}

.gender-item.active {
  border-color: #2f6b4f;
  background: #edf4ef;
  color: #2f6b4f;
  font-weight: 600;
}

.save-button {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 88rpx;
  border-radius: 999rpx;
  background: #2f6b4f;
  color: #fff;
  font-size: 28rpx;
  font-weight: 600;
}
</style>
