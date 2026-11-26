<template>
  <view class="page">
    <!-- 底层背景图：用户手动设置优先，否则按出行方式显示内置图 -->
    <image class="page-bg" :src="backgroundUrl" mode="aspectFill" />
    <view class="page-mask"></view>

    <view class="content">
    <view class="search-bar">
      <view class="menu-button" @click="showDrawer = true">
        <app-icon name="menu" :size="36" color="#2f6b4f" />
      </view>
      <input
        class="search-input"
        v-model="keyword"
        :placeholder="$t('home.searchPlaceholder')"
        placeholder-class="search-placeholder"
        confirm-type="search"
        @confirm="doSearch"
      />
      <text class="search-button" @click="doSearch">{{ $t('home.search') }}</text>
    </view>

    <swiper
      v-if="hotPosts.length && !isSearching"
      class="banner"
      :indicator-dots="hotPosts.length > 1"
      indicator-color="rgba(255,255,255,0.4)"
      indicator-active-color="#ffffff"
      :autoplay="true"
      :interval="4000"
      :duration="400"
      :circular="true"
    >
      <swiper-item v-for="banner in hotPosts" :key="banner.postId" @click="goDetail(banner.postId)">
        <view class="banner-item">
          <image
            v-if="banner.image"
            class="banner-image"
            :src="resolveImageUrl(banner.image)"
            mode="aspectFill"
          />
          <view class="banner-content">
            <text class="banner-badge">{{ $t('home.hot') }}</text>
            <text class="banner-text">{{ banner.content }}</text>
          </view>
        </view>
      </swiper-item>
    </swiper>

    <view v-if="!isSearching" class="filter-card">
      <view class="transport-row">
        <text
          class="transport-item"
          :class="{ active: transportType === 'RAIL' }"
          @click="switchTransport('RAIL')"
        >{{ $t('typeSelect.rail') }}</text>
        <text
          class="transport-item"
          :class="{ active: transportType === 'BUS' }"
          @click="switchTransport('BUS')"
        >{{ $t('typeSelect.bus') }}</text>
      </view>
      <view class="tabs">
        <view
          v-for="item in categories"
          :key="item.key"
          class="tab"
          :class="{ active: category === item.key }"
          @click="switchCategory(item.key)"
        >
          {{ $t(item.label) }}
        </view>
      </view>
      <view class="sort-row">
        <view class="sort-tabs">
          <text
            class="sort-item"
            :class="{ active: sort === 'latest' }"
            @click="switchSort('latest')"
          >
            {{ $t('home.latest') }}
          </text>
          <text
            class="sort-item"
            :class="{ active: sort === 'hot' }"
            @click="switchSort('hot')"
          >
            {{ $t('home.hot') }}
          </text>
        </view>
      </view>
    </view>

    <view v-if="firstLoading" class="feed">
      <view v-for="item in 3" :key="item" class="post-card skeleton-card">
        <view class="skeleton-line skeleton-avatar"></view>
        <view class="skeleton-line skeleton-title"></view>
        <view class="skeleton-line skeleton-text"></view>
        <view class="skeleton-line skeleton-text short"></view>
      </view>
    </view>

    <view v-else-if="!posts.length" class="empty">
      <view class="empty-cup">
        <view class="cup-body"></view>
        <view class="cup-handle"></view>
        <view class="cup-steam steam-one"></view>
        <view class="cup-steam steam-two"></view>
      </view>
      <text class="empty-title">{{ isSearching ? $t('home.searchEmpty') : $t('home.emptyTitle') }}</text>
      <text class="empty-desc">{{ isSearching ? $t('home.searchEmptyDesc') : $t('home.emptyDesc') }}</text>
      <view v-if="!isSearching" class="empty-button" @click="goPublish">{{ $t('home.emptyButton') }}</view>
    </view>

    <view v-else class="feed">
      <view
        v-for="post in posts"
        :key="post.id"
        class="post-card"
        @click="goDetail(post.id)"
      >
        <view class="post-head">
          <view class="avatar" :class="avatarClass(post)">
            {{ avatarText(post) }}
          </view>
          <view class="head-main">
            <view class="name-row">
              <text class="author">{{ post.authorName }}</text>
              <text class="category-badge" :class="post.category === 'RANT' ? 'badge-rant' : 'badge-fresh'">
                {{ categoryLabel(post.category) }}
              </text>
              <text class="transport-badge">{{ post.transportType === 'BUS' ? $t('home.badgeBus') : $t('home.badgeRail') }}</text>
            </view>
            <text class="time">{{ formatTime(post.createdAt) }}</text>
          </view>
        </view>

        <text class="post-content">{{ post.content }}</text>

        <view v-if="post.images && post.images.length" class="image-grid" :class="gridClass(post.images.length)">
          <image
            v-for="(image, index) in post.images"
            :key="image"
            class="image-item"
            :src="resolveImageUrl(image)"
            mode="aspectFill"
            @click.stop="previewImage(post.images, index)"
          />
        </view>

        <view class="post-foot">
          <text class="view-count">{{ $t('home.viewCount', { count: post.viewCount }) }}</text>
          <text class="read-more">{{ $t('home.readMore') }}</text>
        </view>
      </view>

      <view class="list-tip">
        <text v-if="loading">{{ $t('home.loadingMore') }}</text>
        <text v-else-if="!hasMore">{{ $t('home.noMore') }}</text>
        <text v-else>{{ $t('home.scrollMore') }}</text>
      </view>
    </view>

    <view class="publish-fab" @click="goPublish">
      <text class="fab-plus">+</text>
      <text>{{ $t('home.publish') }}</text>
    </view>
    </view>

    <view v-if="showDrawer" class="drawer-mask" @click="showDrawer = false"></view>
    <view class="drawer" :class="{ open: showDrawer }">
      <view class="drawer-head">
        <view class="drawer-avatar">
          <text>{{ avatarText({ authorName: user.nickname || user.username }) }}</text>
        </view>
        <view class="drawer-user">
          <text class="drawer-name">{{ user.nickname || user.username || $t('home.guest') }}</text>
          <text class="drawer-id">{{ $t('settings.memberId') }} {{ user.id }}</text>
        </view>
      </view>

      <view class="drawer-section">{{ $t('drawer.browse') }}</view>
      <view class="drawer-item" @click="onDrawerAction('history')">
        <app-icon name="history" :size="36" color="#2f6b4f" />
        <text class="drawer-label">{{ $t('drawer.history') }}</text>
      </view>
      <view class="drawer-item" @click="onDrawerAction('favorite')">
        <app-icon name="star" :size="36" color="#2f6b4f" />
        <text class="drawer-label">{{ $t('drawer.favorite') }}</text>
      </view>
      <view class="drawer-item" @click="onDrawerAction('myPosts')">
        <app-icon name="edit" :size="36" color="#2f6b4f" />
        <text class="drawer-label">{{ $t('drawer.myPosts') }}</text>
      </view>
      <view class="drawer-item" @click="onDrawerAction('drafts')">
        <app-icon name="message" :size="36" color="#2f6b4f" />
        <text class="drawer-label">{{ $t('drawer.drafts') }}</text>
      </view>

      <view class="drawer-section">{{ $t('drawer.more') }}</view>
      <view class="drawer-item" @click="onDrawerAction('transport')">
        <app-icon :name="transportType === 'BUS' ? 'bus' : 'rail'" :size="36" color="#2f6b4f" />
        <text class="drawer-label">{{ $t('settings.transport') }}</text>
      </view>
      <view class="drawer-item" @click="onDrawerAction('settings')">
        <app-icon name="user" :size="36" color="#2f6b4f" />
        <text class="drawer-label">{{ $t('settings.title') }}</text>
      </view>
      <view class="drawer-item" @click="onDrawerAction('about')">
        <app-icon name="tea" :size="36" color="#2f6b4f" />
        <text class="drawer-label">{{ $t('drawer.about') }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import AppIcon from '@/components/app-icon.vue'
import { fetchPosts, searchPosts } from '@/api/posts'
import { fetchBanners } from '@/api/banners'
import { PAGE_SIZE } from '@/config'
import { getBackgroundUrl, getTransportType, getUser } from '@/utils/auth'
import {
  avatarText,
  timeAgoParts,
  resolveImageUrl,
} from '@/utils/format'

export default {
  components: { AppIcon },
  data() {
    return {
      showDrawer: false,
      hotPosts: [],
      keyword: '',
      isSearching: false,
      categories: [
        { key: 'ALL', label: 'home.all' },
        { key: 'FRESH', label: 'home.fresh' },
        { key: 'RANT', label: 'home.rant' },
      ],
      category: 'ALL',
      transportType: 'RAIL',
      backgroundUrl: '',
      user: {},
      posts: [],
      page: 0,
      hasMore: true,
      loading: false,
      firstLoading: true,
      requestSeq: 0,
    }
  },
  onLoad() {
    this.user = getUser() || {}
    this.transportType = this.user.transportType || getTransportType()
    this.backgroundUrl = getBackgroundUrl()
    this.handlePostPublished = () => {
      this.loadPosts(true)
      this.loadHotPosts()
    }
    uni.$on('post:published', this.handlePostPublished)
    this.loadPosts(true)
    this.loadHotPosts()
  },
  onShow() {
    // 从设置/背景图页返回后，重新读取用户与背景（出行偏好或背景可能变了）。
    const user = getUser()
    this.user = user || {}
    this.backgroundUrl = getBackgroundUrl()
    if (user && user.transportType && user.transportType !== this.transportType) {
      this.transportType = user.transportType
      this.posts = []
      this.firstLoading = true
      this.loadPosts(true)
    }
  },
  onUnload() {
    if (this.handlePostPublished) {
      uni.$off('post:published', this.handlePostPublished)
    }
  },
  onPullDownRefresh() {
    this.loadHotPosts()
    this.loadPosts(true).finally(() => uni.stopPullDownRefresh())
  },
  onReachBottom() {
    if (!this.loading && this.hasMore) {
      this.loadPosts(false)
    }
  },
  methods: {
    avatarText,
    resolveImageUrl,
    async loadHotPosts() {
      try {
        this.hotPosts = await fetchBanners() || []
      } catch (error) {
        this.hotPosts = []
      }
    },
    formatTime(value) {
      const parts = timeAgoParts(value)
      return parts ? this.$t(parts.key, parts) : ''
    },
    categoryLabel(category) {
      return this.$t(category === 'RANT' ? 'home.rant' : 'home.fresh')
    },
    avatarClass(post) {
      if (post.anonymous) return 'avatar-anon'
      return `avatar-tone-${(post.id || 0) % 3}`
    },
    gridClass(count) {
      if (count === 1) return 'grid-one'
      if (count === 2) return 'grid-two'
      return 'grid-three'
    },
    async loadPosts(reset) {
      if (!reset && (this.loading || !this.hasMore)) return
      const seq = ++this.requestSeq
      this.loading = true
      if (reset) {
        this.page = 0
        this.hasMore = true
      }

      try {
        const data = this.isSearching
          ? await searchPosts(this.keyword, reset ? 0 : this.page + 1, PAGE_SIZE)
          : await fetchPosts({
            category: this.category,
            transportType: this.transportType,
            sort: this.sort,
            page: reset ? 0 : this.page + 1,
            size: PAGE_SIZE,
          })
        if (seq !== this.requestSeq) return

        this.posts = reset ? data.items : this.posts.concat(data.items)
        this.page = data.page
        this.hasMore = data.hasMore
      } catch (error) {
        if (seq === this.requestSeq) {
          uni.showToast({ title: error.message, icon: 'none' })
        }
      } finally {
        if (seq === this.requestSeq) {
          this.loading = false
          this.firstLoading = false
        }
      }
    },
    doSearch() {
      const keyword = this.keyword.trim()
      if (!keyword) {
        if (this.isSearching) {
          this.isSearching = false
          this.keyword = ''
          this.posts = []
          this.firstLoading = true
          this.loadPosts(true)
        }
        return
      }
      this.isSearching = true
      this.keyword = keyword
      this.posts = []
      this.firstLoading = true
      this.loadPosts(true)
    },
    // 首页的切换只影响当前查看，不修改账号的出行偏好。
    switchTransport(type) {
      if (this.transportType === type) return
      this.transportType = type
      // 没手动设过背景时，默认图跟着出行方式切换
      if (!(this.user && this.user.backgroundImage)) {
        this.backgroundUrl = getBackgroundUrl()
      }
      this.posts = []
      this.firstLoading = true
      this.loadPosts(true)
    },
    switchCategory(category) {
      if (this.category === category) return
      this.category = category
      this.posts = []
      this.firstLoading = true
      this.loadPosts(true)
    },
    switchSort(sort) {
      if (this.sort === sort) return
      this.sort = sort
      this.posts = []
      this.firstLoading = true
      this.loadPosts(true)
    },
    onDrawerAction(action) {
      this.showDrawer = false
      if (action === 'settings') {
        uni.navigateTo({ url: '/pages/settings/settings' })
      } else if (action === 'transport') {
        uni.navigateTo({ url: '/pages/type-select/type-select?from=settings' })
      } else if (action === 'favorite') {
        uni.navigateTo({ url: '/pages/favorites/favorites' })
      } else if (action === 'history') {
        uni.navigateTo({ url: '/pages/history/history' })
      } else if (action === 'myPosts') {
        uni.navigateTo({ url: '/pages/my-posts/my-posts' })
      } else if (action === 'drafts') {
        uni.navigateTo({ url: '/pages/drafts/drafts' })
      } else if (action === 'about') {
        uni.navigateTo({ url: '/pages/about/about' })
      } else {
        uni.showToast({ title: this.$t('drawer.comingSoon'), icon: 'none' })
      }
    },
    goDetail(id) {
      uni.navigateTo({ url: `/pages/detail/detail?id=${id}` })
    },
    goPublish() {
      uni.navigateTo({ url: '/pages/publish/publish' })
    },
    previewImage(images, index) {
      const urls = images.map(resolveImageUrl)
      uni.previewImage({ current: urls[index], urls })
    },
  },
}
</script>

<style>
.page {
  position: relative;
  min-height: 100vh;
  padding-bottom: calc(160rpx + env(safe-area-inset-bottom));
  background: #eaf1eb;
}

/* 背景图固定在底层，滚动时不跟随，避免内容卡片遮挡时反复移动 */
.page-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

/* 很淡的蒙层：让背景图清晰可见，卡片的水珠质感才透得出来 */
.page-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  background: linear-gradient(180deg, rgba(246, 250, 246, 0.18) 0%, rgba(238, 246, 238, 0.32) 100%);
}

.content {
  position: relative;
  z-index: 2;
}

.search-bar {
  display: flex;
  align-items: center;
  /* H5 下 --status-bar-height 为 0，额外留 20rpx 避免贴顶 */
  padding: calc(var(--status-bar-height) + 20rpx) 28rpx 20rpx;
}

.menu-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  margin-right: 16rpx;
}

.search-input {
  flex: 1;
  height: 72rpx;
  padding: 0 24rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.85);
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.58);
  box-shadow: inset 0 2rpx 6rpx rgba(255, 255, 255, 0.9), 0 8rpx 20rpx rgba(43, 58, 49, 0.08);
  color: #2b352f;
  font-size: 26rpx;
}

.search-placeholder {
  color: #a7b0aa;
}

.search-button {
  margin-left: 16rpx;
  color: #2f6b4f;
  font-size: 28rpx;
  font-weight: 600;
}

.banner {
  height: 240rpx;
  border-radius: 0 0 40rpx 40rpx;
  overflow: hidden;
}

.banner-item {
  position: relative;
  width: 100%;
  height: 240rpx;
  overflow: hidden;
  background: linear-gradient(135deg, #2f6b4f 0%, #3d7c5c 100%);
}

.banner-image {
  width: 100%;
  height: 240rpx;
}

.banner-content {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  padding: 40rpx 32rpx 28rpx;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.6), transparent);
}

.banner-badge {
  display: inline-block;
  margin-bottom: 10rpx;
  padding: 4rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.22);
  color: #fff;
  font-size: 20rpx;
}

.banner-text {
  display: block;
  color: #fff;
  font-size: 28rpx;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 水滴卡片：斜向高光渐变 + 内反光 + 不对称圆角（右上更尖），模拟水珠鼓起的感觉 */
.filter-card {
  position: relative;
  margin: 24rpx 28rpx 0;
  padding: 26rpx 26rpx 30rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.85);
  border-radius: 48rpx 48rpx 56rpx 20rpx;
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.82) 0%, rgba(255, 255, 255, 0.42) 55%, rgba(240, 248, 242, 0.55) 100%);
  box-shadow:
    inset 4rpx 4rpx 10rpx rgba(255, 255, 255, 0.95),
    inset -6rpx -8rpx 16rpx rgba(120, 160, 135, 0.12),
    0 16rpx 40rpx rgba(43, 76, 56, 0.16);
  backdrop-filter: blur(16rpx) saturate(1.2);
}

.transport-row {
  display: flex;
  padding: 8rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.55);
  box-shadow: inset 0 2rpx 8rpx rgba(90, 130, 105, 0.08);
}

.transport-item {
  flex: 1;
  padding: 16rpx 0;
  border-radius: 16rpx;
  color: #6f7b74;
  font-size: 28rpx;
  text-align: center;
}

.transport-item.active {
  background: #2f6b4f;
  color: #fff;
  font-weight: 600;
  box-shadow: 0 8rpx 18rpx rgba(47, 107, 79, 0.22);
}

.tabs {
  display: flex;
  margin-top: 16rpx;
  padding: 8rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.55);
  box-shadow: inset 0 2rpx 8rpx rgba(90, 130, 105, 0.08);
}

.tab {
  flex: 1;
  padding: 18rpx 0;
  border-radius: 16rpx;
  color: #6f7b74;
  font-size: 28rpx;
  text-align: center;
}

.tab.active {
  background: #2f6b4f;
  color: #fff;
  font-weight: 600;
  box-shadow: 0 8rpx 18rpx rgba(47, 107, 79, 0.22);
}

.sort-row {
  display: flex;
  align-items: center;
  margin-top: 20rpx;
}

.sort-tabs {
  display: flex;
  align-items: center;
}

.sort-item {
  padding: 6rpx 18rpx;
  border-radius: 999rpx;
  color: #7d8881;
  font-size: 24rpx;
}

.sort-item.active {
  background: #edf3ee;
  color: #2f6b4f;
  font-weight: 600;
}

.feed {
  padding: 28rpx 28rpx 0;
}

.post-card {
  position: relative;
  margin-bottom: 26rpx;
  padding: 32rpx 30rpx 36rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.85);
  border-radius: 52rpx 52rpx 60rpx 22rpx;
  background: linear-gradient(150deg, rgba(255, 255, 255, 0.78) 0%, rgba(255, 255, 255, 0.4) 60%, rgba(238, 247, 240, 0.52) 100%);
  box-shadow:
    inset 4rpx 4rpx 12rpx rgba(255, 255, 255, 0.95),
    inset -8rpx -10rpx 18rpx rgba(120, 160, 135, 0.12),
    0 18rpx 44rpx rgba(43, 76, 56, 0.16);
  backdrop-filter: blur(14rpx) saturate(1.15);
  overflow: hidden;
}

/* 水珠顶部的一小块高光，像光照在透明水滴上 */
.post-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 40rpx;
  width: 180rpx;
  height: 14rpx;
  border-radius: 0 0 50% 50%;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0));
}

.post-head {
  display: flex;
  align-items: center;
}

.avatar {
  display: flex;
  flex: none;
  align-items: center;
  justify-content: center;
  width: 76rpx;
  height: 76rpx;
  margin-right: 20rpx;
  border-radius: 50%;
  color: #fff;
  font-size: 30rpx;
  font-weight: 600;
}

.avatar-anon {
  background: #8a948d;
}

.avatar-tone-0 {
  background: #4f7d64;
}

.avatar-tone-1 {
  background: #b2774f;
}

.avatar-tone-2 {
  background: #5b7189;
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
  max-width: 260rpx;
  overflow: hidden;
  color: #29342d;
  font-size: 28rpx;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
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

.post-content {
  display: block;
  margin-top: 24rpx;
  color: #2b352f;
  font-size: 30rpx;
  word-break: break-all;
}

.image-grid {
  display: flex;
  flex-wrap: wrap;
  margin-top: 22rpx;
  gap: 10rpx;
}

.image-item {
  border-radius: 16rpx;
  background: #ece8de;
}

.grid-one .image-item {
  width: 100%;
  height: 360rpx;
}

.grid-two .image-item {
  width: calc((100% - 10rpx) / 2);
  height: 260rpx;
}

.grid-three .image-item {
  width: calc((100% - 20rpx) / 3);
  height: 190rpx;
}

.post-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 24rpx;
  padding-top: 20rpx;
  border-top: 2rpx solid #f2efe7;
}

.view-count {
  color: #9aa49e;
  font-size: 22rpx;
}

.read-more {
  color: #2f6b4f;
  font-size: 24rpx;
  font-weight: 600;
}

.list-tip {
  padding: 20rpx 0 40rpx;
  color: #a0aaa4;
  font-size: 24rpx;
  text-align: center;
}

.publish-fab {
  position: fixed;
  right: 32rpx;
  bottom: calc(132rpx + env(safe-area-inset-bottom));
  z-index: 10;
  display: flex;
  align-items: center;
  padding: 24rpx 34rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.5);
  border-radius: 999rpx 999rpx 999rpx 28rpx;
  background: linear-gradient(135deg, rgba(47, 107, 79, 0.96), rgba(61, 124, 92, 0.88));
  box-shadow: inset 2rpx 2rpx 6rpx rgba(255, 255, 255, 0.35), 0 16rpx 34rpx rgba(47, 107, 79, 0.32);
  color: #fff;
  font-size: 28rpx;
  font-weight: 600;
}

.fab-plus {
  margin-right: 8rpx;
  font-size: 36rpx;
  font-weight: 400;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 28rpx 28rpx 0;
  padding: 64rpx 48rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.85);
  border-radius: 56rpx 56rpx 64rpx 24rpx;
  background: linear-gradient(150deg, rgba(255, 255, 255, 0.78) 0%, rgba(255, 255, 255, 0.42) 60%, rgba(238, 247, 240, 0.52) 100%);
  box-shadow:
    inset 4rpx 4rpx 12rpx rgba(255, 255, 255, 0.95),
    inset -8rpx -10rpx 18rpx rgba(120, 160, 135, 0.12),
    0 18rpx 44rpx rgba(43, 76, 56, 0.16);
  backdrop-filter: blur(14rpx) saturate(1.15);
}

.empty-cup {
  position: relative;
  width: 160rpx;
  height: 140rpx;
}

.cup-body {
  position: absolute;
  left: 20rpx;
  bottom: 0;
  width: 110rpx;
  height: 74rpx;
  border: 4rpx solid #9fb3a7;
  border-top: none;
  border-radius: 0 0 40rpx 40rpx;
}

.cup-handle {
  position: absolute;
  right: 0;
  bottom: 22rpx;
  width: 38rpx;
  height: 40rpx;
  border: 4rpx solid #9fb3a7;
  border-left: none;
  border-radius: 0 24rpx 24rpx 0;
}

.cup-steam {
  position: absolute;
  bottom: 84rpx;
  width: 4rpx;
  height: 36rpx;
  border-radius: 999rpx;
  background: #b8c8be;
}

.steam-one {
  left: 54rpx;
  transform: rotate(8deg);
}

.steam-two {
  left: 84rpx;
  height: 28rpx;
  transform: rotate(-8deg);
}

.empty-title {
  margin-top: 28rpx;
  color: #35433a;
  font-size: 34rpx;
  font-weight: 600;
}

.empty-desc {
  margin-top: 12rpx;
  color: #8f9b93;
  font-size: 26rpx;
}

.empty-button {
  margin-top: 34rpx;
  padding: 18rpx 54rpx;
  border-radius: 999rpx;
  background: #2f6b4f;
  color: #fff;
  font-size: 28rpx;
  font-weight: 600;
}

.skeleton-card {
  padding-top: 34rpx;
}

.skeleton-line {
  background: #eeeae1;
  border-radius: 12rpx;
  animation: pulse 1.4s ease-in-out infinite;
}

.skeleton-avatar {
  width: 76rpx;
  height: 76rpx;
  border-radius: 50%;
}

.skeleton-title {
  width: 180rpx;
  height: 28rpx;
  margin-top: 28rpx;
}

.skeleton-text {
  width: 100%;
  height: 28rpx;
  margin-top: 20rpx;
}

.skeleton-text.short {
  width: 62%;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.55;
  }
}

.drawer-mask {
  position: fixed;
  z-index: 20;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
}

.drawer {
  position: fixed;
  z-index: 21;
  top: 0;
  bottom: 0;
  left: 0;
  width: 72%;
  max-width: 620rpx;
  padding: calc(var(--status-bar-height) + 40rpx) 32rpx 40rpx;
  overflow-y: auto;
  background: #fffdf8;
  transform: translateX(-100%);
  transition: transform 0.25s ease;
}

.drawer.open {
  transform: translateX(0);
}

.drawer-head {
  display: flex;
  align-items: center;
  margin-bottom: 40rpx;
}

.drawer-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  background: #edf4ef;
  color: #2f6b4f;
  font-size: 34rpx;
  font-weight: 600;
}

.drawer-user {
  display: flex;
  flex-direction: column;
  margin-left: 20rpx;
}

.drawer-name {
  color: #35433a;
  font-size: 30rpx;
  font-weight: 600;
}

.drawer-id {
  margin-top: 6rpx;
  color: #a1aaa4;
  font-size: 22rpx;
}

.drawer-section {
  margin: 24rpx 0 12rpx;
  color: #a1aaa4;
  font-size: 22rpx;
}

.drawer-item {
  display: flex;
  align-items: center;
  padding: 20rpx 8rpx;
  border-bottom: 2rpx solid #f3efe6;
}

.drawer-label {
  margin-left: 20rpx;
  color: #35433a;
  font-size: 28rpx;
}
</style>
