<template>
  <view class="page">
    <view v-if="loading" class="state">{{ $t('common.loading') }}</view>
    <view v-else-if="!posts.length" class="state">
      <text class="state-text">{{ $t('favorites.empty') }}</text>
    </view>
    <view v-else class="list">
      <view v-for="post in posts" :key="post.id" class="post-card" @click="goDetail(post.id)">
        <view class="post-head">
          <view class="avatar">{{ avatarText(post) }}</view>
          <view class="head-main">
            <text class="author">{{ post.authorName }}</text>
            <text class="time">{{ formatTime(post.createdAt) }}</text>
          </view>
        </view>
        <text class="content">{{ post.content }}</text>
        <view v-if="post.images && post.images.length" class="thumb">
          <image :src="resolveImageUrl(post.images[0])" mode="aspectFill" />
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { fetchFavorites } from '@/api/social'
import { avatarText, timeAgoParts, resolveImageUrl } from '@/utils/format'

export default {
  data() {
    return {
      posts: [],
      loading: true,
    }
  },
  onLoad() {
    uni.setNavigationBarTitle({ title: this.$t('drawer.favorite') })
    this.load()
  },
  onPullDownRefresh() {
    this.load().finally(() => uni.stopPullDownRefresh())
  },
  methods: {
    avatarText,
    resolveImageUrl,
    formatTime(value) {
      const parts = timeAgoParts(value)
      return parts ? this.$t(parts.key, parts) : ''
    },
    async load() {
      this.loading = true
      try {
        this.posts = await fetchFavorites() || []
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
        this.posts = []
      } finally {
        this.loading = false
      }
    },
    goDetail(id) {
      uni.navigateTo({ url: `/pages/detail/detail?id=${id}` })
    },
  },
}
</script>

<style>
.page {
  padding: 28rpx;
  background: #f4f0e6;
}

.state {
  padding: 120rpx 40rpx;
  color: #a1aaa4;
  font-size: 26rpx;
  text-align: center;
}

.post-card {
  margin-bottom: 24rpx;
  padding: 28rpx;
  border-radius: 28rpx;
  background: #fff;
  box-shadow: 0 8rpx 28rpx rgba(43, 58, 49, 0.06);
}

.post-head {
  display: flex;
  align-items: center;
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: #edf4ef;
  color: #2f6b4f;
  font-size: 26rpx;
}

.head-main {
  display: flex;
  flex: 1;
  flex-direction: column;
  margin-left: 16rpx;
}

.author {
  color: #35433a;
  font-size: 26rpx;
  font-weight: 600;
}

.time {
  margin-top: 4rpx;
  color: #a1aaa4;
  font-size: 20rpx;
}

.content {
  display: block;
  margin-top: 20rpx;
  color: #3c4a42;
  font-size: 28rpx;
}

.thumb {
  height: 240rpx;
  margin-top: 20rpx;
  overflow: hidden;
  border-radius: 16rpx;
}

.thumb image {
  width: 100%;
  height: 100%;
}
</style>
