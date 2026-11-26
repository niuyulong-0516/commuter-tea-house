<template>
  <view class="page">
    <page-nav-bar title-key="message.title" :show-back="false" />

    <view v-if="notifications.length" class="action-row">
      <text class="action-link" @click="markAllRead">{{ $t('message.markAllRead') }}</text>
    </view>

    <view v-if="loading && !notifications.length" class="state">{{ $t('common.loading') }}</view>
    <view v-else-if="!notifications.length" class="state">{{ $t('message.empty') }}</view>

    <view v-else class="list">
      <view
        v-for="item in notifications"
        :key="item.id"
        class="msg-card"
        :class="{ unread: !item.read }"
        @click="openNotification(item)"
      >
        <view class="msg-icon" :class="item.type.toLowerCase()">
          <app-icon :name="iconOf(item.type)" :size="38" :color="item.type === 'OFFICIAL' ? '#fff' : '#2f6b4f'" />
        </view>
        <view class="msg-content">
          <view class="msg-head">
            <text class="msg-title">{{ item.title }}</text>
            <text class="msg-time">{{ timeAgo(item.createdAt) }}</text>
          </view>
          <text class="msg-body">{{ item.content }}</text>
        </view>
        <view v-if="!item.read" class="unread-dot"></view>
      </view>
    </view>

    <water-tab-bar />
  </view>
</template>

<script>
import AppIcon from '@/components/app-icon.vue'
import PageNavBar from '@/components/page-nav-bar.vue'
import WaterTabBar from '@/components/water-tab-bar.vue'
import {
  fetchNotifications,
  markNotificationRead,
  markAllNotificationsRead,
} from '@/api/notifications'
import { notificationState, refreshUnread } from '@/utils/notification-socket'
import { timeAgoParts } from '@/utils/format'

export default {
  components: { AppIcon, PageNavBar, WaterTabBar },
  data() {
    return {
      notifications: [],
      loading: false,
      hasMore: false,
    }
  },
  onLoad() {
    this.reload()
  },
  onShow() {
    // 从详情页返回时刷新未读（全局 mixin 也会刷新一次，这里保证列表最新）
    refreshUnread()
  },
  methods: {
    async reload() {
      this.loading = true
      try {
        const data = await fetchNotifications(0, 20)
        this.notifications = (data && data.items) || []
        this.hasMore = !!(data && data.hasMore)
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    iconOf(type) {
      if (type === 'LIKE') return 'heart'
      if (type === 'COMMENT') return 'message'
      return 'tea'
    },
    timeAgo(value) {
      const parts = timeAgoParts(value)
      return parts ? this.$t(parts.key, parts) : ''
    },
    async openNotification(item) {
      if (!item.read) {
        try {
          await markNotificationRead(item.id)
          item.read = true
          if (notificationState.unread > 0) notificationState.unread -= 1
        } catch (e) {
          // 标记失败不影响跳转
        }
      }
      if (item.postId) {
        uni.navigateTo({ url: `/pages/detail/detail?id=${item.postId}` })
      }
    },
    async markAllRead() {
      try {
        await markAllNotificationsRead()
        this.notifications.forEach((item) => { item.read = true })
        notificationState.unread = 0
        uni.showToast({ title: this.$t('message.marked'), icon: 'none' })
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
      }
    },
  },
}
</script>

<style>
.page {
  padding: 0 28rpx 200rpx;
  background: transparent;
}

.action-row {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16rpx;
}

.action-link {
  padding: 8rpx 16rpx;
  color: #2f6b4f;
  font-size: 24rpx;
}

.state {
  padding: 80rpx 0;
  color: #6d7a71;
  font-size: 26rpx;
  text-align: center;
}

.list {
  display: block;
}

/* 通知卡：半透明水滴玻璃 */
.msg-card {
  position: relative;
  display: flex;
  align-items: flex-start;
  margin-bottom: 22rpx;
  padding: 28rpx;
  border: 3rpx solid rgba(255, 255, 255, 0.88);
  border-radius: 52rpx 52rpx 60rpx 24rpx;
  background: linear-gradient(150deg, rgba(255, 255, 255, 0.68), rgba(255, 255, 255, 0.3));
  box-shadow:
    inset 5rpx 6rpx 12rpx rgba(255, 255, 255, 0.9),
    inset -8rpx -10rpx 18rpx rgba(70, 130, 95, 0.14),
    0 16rpx 40rpx rgba(30, 70, 48, 0.16);
  backdrop-filter: blur(16rpx) saturate(1.25);
}

.msg-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  margin-right: 20rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.85);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.75);
  flex-shrink: 0;
}

.msg-icon.official {
  background: #2f6b4f;
}

.msg-content {
  flex: 1;
  min-width: 0;
}

.msg-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.msg-title {
  color: #26382d;
  font-size: 28rpx;
  font-weight: 600;
}

.msg-time {
  margin-left: 16rpx;
  color: #8b9a90;
  font-size: 22rpx;
  white-space: nowrap;
}

.msg-body {
  display: block;
  margin-top: 8rpx;
  color: #5c7063;
  font-size: 25rpx;
}

.unread-dot {
  position: absolute;
  top: 28rpx;
  right: 24rpx;
  width: 14rpx;
  height: 14rpx;
  border-radius: 50%;
  background: #e05c4b;
}
</style>
