<template>
  <view class="page">
    <view v-if="loading" class="state">
      <text class="state-text">{{ $t('detail.loading') }}</text>
    </view>

    <view v-else-if="post" class="detail-card">
      <view class="detail-head">
        <view class="avatar" :class="post.anonymous ? 'avatar-anon' : 'avatar-user'">
          {{ avatarText(post) }}
        </view>
        <view class="head-main">
          <view class="name-row">
            <text class="author">{{ post.authorName }}</text>
            <text
              class="category-badge"
              :class="post.category === 'RANT' ? 'badge-rant' : 'badge-fresh'"
            >
              {{ categoryLabel(post.category) }}
            </text>
            <text class="transport-badge">{{ post.transportType === 'BUS' ? $t('home.badgeBus') : $t('home.badgeRail') }}</text>
          </view>
          <text class="time">{{ formatTime(post.createdAt) }}</text>
        </view>
      </view>

      <text class="content">{{ post.content }}</text>

      <view v-if="post.images && post.images.length" class="image-list">
        <image
          v-for="(image, index) in post.images"
          :key="image"
          class="detail-image"
          :src="resolveImageUrl(image)"
          mode="widthFix"
          @click="previewImage(index)"
        />
      </view>

      <view class="detail-actions">
        <view class="action-item" :class="{ active: interaction.liked }" @click="onLike">
          <app-icon name="heart" :size="32" :color="interaction.liked ? '#c05b4a' : '#9aa59f'" />
          <text class="action-label">{{ $t('detail.like') }} {{ interaction.likeCount }}</text>
        </view>
        <view class="action-item" :class="{ active: interaction.favorited }" @click="onFavorite">
          <app-icon name="bookmark" :size="32" :color="interaction.favorited ? '#c98a3c' : '#9aa59f'" />
          <text class="action-label">{{ $t('detail.favorite') }} {{ interaction.favoriteCount }}</text>
        </view>
        <view class="action-item">
          <app-icon name="message" :size="32" color="#9aa59f" />
          <text class="action-label">{{ $t('detail.comment') }} {{ interaction.commentCount }}</text>
        </view>
      </view>

      <view class="detail-foot">
        <text class="view-count">{{ $t('home.viewCount', { count: post.viewCount }) }}</text>
        <text class="foot-note">{{ $t('detail.footNote') }}</text>
      </view>
    </view>

    <view v-if="post" class="comment-card">
      <text class="section-title">{{ $t('detail.comments') }}</text>
      <view v-if="!comments.length" class="comment-empty">{{ $t('detail.noComments') }}</view>
      <view v-for="comment in comments" :key="comment.id" class="comment-item">
        <view class="comment-avatar">{{ comment.authorName.slice(-1) }}</view>
        <view class="comment-body">
          <text class="comment-author">{{ comment.authorName }}</text>
          <text class="comment-content">{{ comment.content }}</text>
          <text class="comment-time">{{ formatTime(comment.createdAt) }}</text>
        </view>
      </view>
    </view>

    <view v-else-if="!loading" class="state">
      <text class="state-title">{{ $t('detail.notFoundTitle') }}</text>
      <text class="state-text">{{ $t('detail.notFoundDesc') }}</text>
      <view class="back-button" @click="backToIndex">{{ $t('detail.backToIndex') }}</view>
    </view>

    <view v-if="post" class="bottom-space"></view>
    <view v-if="post" class="bottom-bar">
      <input
        class="comment-input"
        v-model="commentText"
        :placeholder="$t('detail.commentPlaceholder')"
        placeholder-class="comment-placeholder"
        confirm-type="send"
        @confirm="submitComment"
      />
      <view class="send-button" @click="submitComment">{{ $t('detail.send') }}</view>
      <view class="publish-button" @click="goPublish">{{ $t('detail.publishButton') }}</view>
    </view>
  </view>
</template>

<script>
import AppIcon from '@/components/app-icon.vue'
import { fetchPostDetail } from '@/api/posts'
import { recordView } from '@/api/library'
import {
  fetchInteractionState,
  toggleLike,
  toggleFavorite,
  fetchComments,
  createComment,
} from '@/api/social'
import {
  avatarText,
  timeAgoParts,
  resolveImageUrl,
} from '@/utils/format'

export default {
  components: { AppIcon },
  data() {
    return {
      id: '',
      post: null,
      loading: true,
      interaction: { liked: false, favorited: false, likeCount: 0, favoriteCount: 0, commentCount: 0 },
      comments: [],
      commentText: '',
    }
  },
  onLoad(options) {
    this.id = options.id
    if (!this.id) {
      this.loading = false
      return
    }
    this.loadDetail()
    this.loadInteraction()
    this.loadComments()
    recordView(this.id).catch(() => {})
  },
  methods: {
    avatarText,
    resolveImageUrl,
    formatTime(value) {
      const parts = timeAgoParts(value)
      return parts ? this.$t(parts.key, parts) : ''
    },
    categoryLabel(category) {
      return this.$t(category === 'RANT' ? 'home.rant' : 'home.fresh')
    },
    async loadDetail() {
      this.loading = true
      try {
        this.post = await fetchPostDetail(this.id)
        uni.setNavigationBarTitle({ title: this.$t('detail.title') })
      } catch (error) {
        this.post = null
        uni.showToast({ title: error.message, icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    async loadInteraction() {
      try {
        this.interaction = await fetchInteractionState(this.id)
      } catch (error) {
        // 互动状态加载失败不影响正文浏览
      }
    },
    async loadComments() {
      try {
        const data = await fetchComments(this.id)
        this.comments = data.items || []
      } catch (error) {
        this.comments = []
      }
    },
    async onLike() {
      try {
        const result = await toggleLike(this.id)
        this.interaction.liked = result.active
        this.interaction.likeCount = result.count
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
      }
    },
    async onFavorite() {
      try {
        const result = await toggleFavorite(this.id)
        this.interaction.favorited = result.active
        this.interaction.favoriteCount = result.count
        uni.showToast({ title: result.active ? this.$t('detail.favorited') : this.$t('detail.unfavorited'), icon: 'none' })
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
      }
    },
    async submitComment() {
      const content = this.commentText.trim()
      if (!content) return
      try {
        const comment = await createComment(this.id, content)
        this.comments.unshift(comment)
        this.commentText = ''
        this.interaction.commentCount += 1
        uni.showToast({ title: this.$t('detail.commentSent'), icon: 'none' })
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
      }
    },
    previewImage(index) {
      const urls = this.post.images.map(resolveImageUrl)
      uni.previewImage({ current: urls[index], urls })
    },
    goPublish() {
      uni.navigateTo({ url: '/pages/publish/publish' })
    },
    backToIndex() {
      uni.reLaunch({ url: '/pages/index/index' })
    },
  },
}
</script>

<style>
.page {
  padding: 28rpx 28rpx 0;
  background: transparent;
}

.detail-card {
  padding: 34rpx 32rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.72);
  border: 2rpx solid rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16rpx) saturate(1.2);
  box-shadow: 0 8rpx 28rpx rgba(43, 58, 49, 0.06);
}

.detail-head {
  display: flex;
  align-items: center;
}

.avatar {
  display: flex;
  flex: none;
  align-items: center;
  justify-content: center;
  width: 84rpx;
  height: 84rpx;
  margin-right: 20rpx;
  border-radius: 50%;
  color: #fff;
  font-size: 32rpx;
  font-weight: 600;
}

.avatar-anon {
  background: #8a948d;
}

.avatar-user {
  background: #4f7d64;
}

.head-main {
  display: flex;
  flex: 1;
  flex-direction: column;
}

.name-row {
  display: flex;
  align-items: center;
}

.author {
  color: #29342d;
  font-size: 30rpx;
  font-weight: 600;
}

.category-badge {
  margin-left: 14rpx;
  padding: 4rpx 14rpx;
  border-radius: 999rpx;
  font-size: 20rpx;
}

.badge-fresh {
  background: #edf4ef;
  color: #2f6b4f;
}

.badge-rant {
  background: #fbefe8;
  color: #b56b42;
}

.transport-badge {
  margin-left: 10rpx;
  padding: 4rpx 12rpx;
  border-radius: 999rpx;
  background: #eef1f4;
  color: #5b7189;
  font-size: 20rpx;
}

.time {
  margin-top: 6rpx;
  color: #9aa49e;
  font-size: 22rpx;
}

.content {
  display: block;
  margin-top: 32rpx;
  color: #25312a;
  font-size: 34rpx;
  white-space: pre-wrap;
  word-break: break-word;
}

.image-list {
  display: flex;
  flex-direction: column;
  margin-top: 28rpx;
  gap: 16rpx;
}

.detail-image {
  width: 100%;
  border-radius: 18rpx;
  background: #ece8de;
}

.detail-actions {
  display: flex;
  align-items: center;
  margin-top: 32rpx;
  padding-top: 24rpx;
  border-top: 2rpx solid #f2efe7;
}

.action-item {
  display: flex;
  align-items: center;
  margin-right: 40rpx;
}

.action-label {
  margin-left: 10rpx;
  color: #8e9a92;
  font-size: 24rpx;
}

.action-item.active .action-label {
  color: #2f6b4f;
  font-weight: 600;
}

.comment-card {
  margin-top: 24rpx;
  padding: 28rpx 32rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.72);
  border: 2rpx solid rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16rpx) saturate(1.2);
  box-shadow: 0 8rpx 28rpx rgba(43, 58, 49, 0.06);
}

.section-title {
  display: block;
  margin-bottom: 20rpx;
  color: #35433a;
  font-size: 28rpx;
  font-weight: 600;
}

.comment-empty {
  padding: 20rpx 0;
  color: #a1aaa4;
  font-size: 24rpx;
}

.comment-item {
  display: flex;
  padding: 20rpx 0;
  border-bottom: 2rpx solid #f3efe6;
}

.comment-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: #edf4ef;
  color: #2f6b4f;
  font-size: 24rpx;
}

.comment-body {
  display: flex;
  flex: 1;
  flex-direction: column;
  margin-left: 16rpx;
}

.comment-author {
  color: #35433a;
  font-size: 24rpx;
  font-weight: 600;
}

.comment-content {
  margin-top: 8rpx;
  color: #4a5951;
  font-size: 26rpx;
}

.comment-time {
  margin-top: 8rpx;
  color: #a1aaa4;
  font-size: 20rpx;
}

.detail-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 34rpx;
  padding-top: 24rpx;
  border-top: 2rpx solid #f2efe7;
}

.view-count {
  color: #9aa49e;
  font-size: 22rpx;
}

.foot-note {
  color: #b0b8b3;
  font-size: 22rpx;
}

.state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 160rpx 48rpx;
}

.state-title {
  color: #35433a;
  font-size: 34rpx;
  font-weight: 600;
}

.state-text {
  margin-top: 18rpx;
  color: #8f9b93;
  font-size: 26rpx;
  text-align: center;
}

.back-button {
  margin-top: 34rpx;
  padding: 18rpx 54rpx;
  border-radius: 999rpx;
  background: #2f6b4f;
  color: #fff;
  font-size: 28rpx;
  font-weight: 600;
}

.bottom-space {
  height: calc(160rpx + env(safe-area-inset-bottom));
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 20rpx 28rpx calc(20rpx + env(safe-area-inset-bottom));
  background: rgba(244, 240, 230, 0.96);
}

.comment-input {
  flex: 1;
  height: 80rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  background: #fff;
  color: #2b352f;
  font-size: 26rpx;
}

.comment-placeholder {
  color: #a7b0aa;
}

.send-button {
  padding: 0 20rpx;
  color: #2f6b4f;
  font-size: 28rpx;
  font-weight: 600;
}

.publish-button {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 80rpx;
  padding: 0 20rpx;
  border: 2rpx solid #2f6b4f;
  border-radius: 999rpx;
  background: #fff;
  color: #2f6b4f;
  font-size: 26rpx;
  font-weight: 600;
}
</style>
