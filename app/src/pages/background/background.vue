<template>
  <view class="page">
    <view class="nav-bar">
      <view class="back-button" hover-class="back-button-active" @click="goBack">
        <view class="back-arrow"></view>
        <text class="back-text">{{ $t('common.back') }}</text>
      </view>
      <text class="nav-title">{{ $t('background.title') }}</text>
    </view>

    <view class="tip">{{ $t('background.tip') }}</view>

    <!-- 内置默认图：不算历史，选中即恢复默认 -->
    <view class="section-title">{{ $t('background.defaultTitle') }}</view>
    <view class="grid">
      <view
        v-for="item in defaults"
        :key="item.key"
        class="item"
        :class="{ active: isCurrent(item.url) }"
        @click="applyDefault(item.url)"
      >
        <image class="thumb" :src="item.url" mode="aspectFill" />
        <text class="item-label">{{ item.label }}</text>
        <text v-if="isCurrent(item.url)" class="check">✓</text>
      </view>
    </view>

    <!-- 用户上传过的历史图 -->
    <view class="section-title">{{ $t('background.historyTitle') }}</view>
    <view v-if="!items.length" class="empty-hint">{{ $t('background.empty') }}</view>
    <view v-else class="grid">
      <view
        v-for="item in items"
        :key="item.id"
        class="item"
        :class="{ active: isCurrent(item.url) }"
        @click="applyHistory(item)"
      >
        <image class="thumb" :src="resolveUrl(item.url)" mode="aspectFill" />
        <text v-if="isCurrent(item.url)" class="check">✓</text>
      </view>
    </view>

    <view class="upload-button" @click="chooseImage">{{ $t('background.upload') }}</view>
  </view>
</template>

<script>
import { fetchBackgrounds, saveBackground, selectBackground } from '@/api/auth'
import { uploadPostImage } from '@/api/posts'
import { getBackgroundUrl, updateLocalUser } from '@/utils/auth'
import { resolveImageUrl } from '@/utils/format'

const DEFAULT_IMAGES = [
  { key: 'RAIL', url: '/static/backgrounds/bg-rail.jpg', label: '轨道交通' },
  { key: 'BUS', url: '/static/backgrounds/bg-bus.jpg', label: '公共交通' },
]

export default {
  data() {
    return {
      defaults: DEFAULT_IMAGES.map((item) => ({
        ...item,
        label: this.$t(item.key === 'RAIL' ? 'typeSelect.rail' : 'typeSelect.bus'),
      })),
      current: '',
      items: [],
      loading: false,
    }
  },
  onLoad() {
    this.loadBackgrounds()
  },
  methods: {
    resolveUrl(url) {
      // 本地内置图直接显示；服务端返回的相对路径才需要拼接口域名
      if (url && url.startsWith('/static/')) return url
      return resolveImageUrl(url)
    },
    async loadBackgrounds() {
      try {
        const data = await fetchBackgrounds()
        this.current = data.current || ''
        this.items = data.items || []
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
      }
    },
    isCurrent(url) {
      if (this.current) return this.current === url
      // 没设过自定义图时，只有当前出行方式对应的默认图算选中
      return getBackgroundUrl() === url
    },
    async applyDefault(url) {
      if (this.loading) return
      this.loading = true
      uni.showLoading({ title: this.$t('common.loading'), mask: true })
      try {
        // 点内置图相当于手动固定这张：存到历史并设为当前，之后不再随出行方式变化。
        const data = await saveBackground(url)
        this.current = data.current || ''
        this.items = data.items || []
        updateLocalUser({ backgroundImage: this.current })
        uni.showToast({ title: this.$t('background.saved'), icon: 'none' })
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
      } finally {
        this.loading = false
        uni.hideLoading()
      }
    },
    async applyHistory(item) {
      if (this.loading) return
      this.loading = true
      uni.showLoading({ title: this.$t('common.loading'), mask: true })
      try {
        const data = await selectBackground(item.id)
        this.current = data.current || ''
        this.items = data.items || []
        updateLocalUser({ backgroundImage: this.current })
        uni.showToast({ title: this.$t('background.saved'), icon: 'none' })
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
      } finally {
        this.loading = false
        uni.hideLoading()
      }
    },
    chooseImage() {
      if (this.loading) return
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          const path = res.tempFilePaths[0]
          if (!path) return
          this.loading = true
          uni.showLoading({ title: this.$t('common.loading'), mask: true })
          try {
            const uploaded = await uploadPostImage(path)
            if (!uploaded || !uploaded.url) throw new Error(this.$t('errors.imageUploadFailed'))
            const data = await saveBackground(uploaded.url)
            this.current = data.current || ''
            this.items = data.items || []
            updateLocalUser({ backgroundImage: this.current })
            uni.showToast({ title: this.$t('background.saved'), icon: 'none' })
          } catch (error) {
            uni.showToast({ title: error.message, icon: 'none' })
          } finally {
            this.loading = false
            uni.hideLoading()
          }
        },
      })
    },
    goBack() {
      uni.navigateBack()
    },
  },
}
</script>

<style>
.page {
  min-height: 100vh;
  padding: 28rpx 28rpx 80rpx;
  background: #f4f0e6;
}

.nav-bar {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
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

.tip {
  margin-bottom: 24rpx;
  padding: 20rpx 24rpx;
  border-radius: 20rpx;
  background: rgba(47, 107, 79, 0.08);
  color: #5f7365;
  font-size: 24rpx;
}

.section-title {
  display: block;
  margin: 24rpx 0 12rpx;
  color: #6d7a71;
  font-size: 24rpx;
  font-weight: 600;
}

.grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.item {
  position: relative;
  width: calc((100% - 16rpx) / 2);
  border: 4rpx solid transparent;
  border-radius: 24rpx;
  overflow: hidden;
}

.item.active {
  border-color: #2f6b4f;
}

.thumb {
  display: block;
  width: 100%;
  height: 240rpx;
  border-radius: 20rpx;
  background: #e7ece8;
}

.item-label {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12rpx 16rpx;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.5), transparent);
  color: #fff;
  font-size: 24rpx;
}

.check {
  position: absolute;
  top: 12rpx;
  right: 12rpx;
  width: 44rpx;
  height: 44rpx;
  border-radius: 50%;
  background: #2f6b4f;
  color: #fff;
  font-size: 26rpx;
  text-align: center;
}

.empty-hint {
  padding: 40rpx 0;
  color: #9aa59e;
  font-size: 26rpx;
  text-align: center;
}

.upload-button {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 88rpx;
  margin-top: 40rpx;
  border-radius: 999rpx;
  background: #2f6b4f;
  color: #fff;
  font-size: 28rpx;
  font-weight: 600;
}
</style>
