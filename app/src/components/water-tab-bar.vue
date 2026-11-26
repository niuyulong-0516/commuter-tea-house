<template>
  <view class="tab-wrap">
    <view class="tab-bar">
      <view
        v-for="(item, index) in list"
        :key="item.pagePath"
        class="drop"
        :class="{ active: selected === index }"
        @click="switchTab(index)"
      >
        <view class="drop-icon-wrap">
          <app-icon :name="item.icon" :size="34" :color="selected === index ? '#ffffff' : '#4e7a5f'" />
          <!-- 消息未读红点 -->
          <view v-if="item.icon === 'message' && unread > 0" class="badge">
            {{ unread > 99 ? '99+' : unread }}
          </view>
        </view>
        <text class="drop-text">{{ item.text }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { reactive } from 'vue'
import AppIcon from '@/components/app-icon.vue'
import { notificationState } from '@/utils/notification-socket'

const TABS = [
  { pagePath: '/pages/index/index', text: '首页', icon: 'tea' },
  { pagePath: '/pages/message/message', text: '消息', icon: 'message' },
  { pagePath: '/pages/mine/mine', text: '我的', icon: 'user' },
]

// 选中状态放在模块级，首页和我的页共用同一份，避免各页面实例不同步。
const tabState = reactive({ selected: 0 })

function syncSelected() {
  const pages = getCurrentPages()
  const route = pages[pages.length - 1] && pages[pages.length - 1].route
  const index = TABS.findIndex((item) => item.pagePath.endsWith(route || ''))
  tabState.selected = index >= 0 ? index : 0
}

export default {
  components: { AppIcon },
  data() {
    return {
      list: TABS,
    }
  },
  computed: {
    unread() {
      return notificationState.unread
    },
    selected() {
      return tabState.selected
    },
  },
  mounted() {
    syncSelected()
  },
  methods: {
    switchTab(index) {
      if (tabState.selected === index) return
      // 先更新共享状态：所有页面的水滴组件读同一份，点击后立即生效
      tabState.selected = index
      uni.switchTab({ url: this.list[index].pagePath })
    },
  },
}
</script>

<style>
.tab-wrap {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 999;
  display: flex;
  justify-content: center;
  padding-bottom: calc(22rpx + env(safe-area-inset-bottom));
  pointer-events: none;
}

/* 半透明水盘，把两滴水托住 */
.tab-bar {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 12rpx 24rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.9);
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.45);
  box-shadow:
    inset 4rpx 4rpx 10rpx rgba(255, 255, 255, 0.9),
    0 12rpx 36rpx rgba(30, 70, 48, 0.18);
  backdrop-filter: blur(18rpx) saturate(1.35);
  pointer-events: auto;
}

/* 每滴水：上圆下尖的不对称圆角，像一颗水珠 */
.drop {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 112rpx;
  height: 88rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.9);
  border-radius: 50% 50% 50% 50% / 64% 64% 36% 36%;
  background: linear-gradient(150deg, rgba(255, 255, 255, 0.7), rgba(255, 255, 255, 0.25));
  box-shadow:
    inset 4rpx 6rpx 10rpx rgba(255, 255, 255, 0.95),
    inset -6rpx -8rpx 14rpx rgba(70, 130, 95, 0.16),
    0 8rpx 18rpx rgba(30, 70, 48, 0.14);
  transition: transform 0.2s ease;
}

.drop.active {
  transform: translateY(-6rpx) scale(1.08);
  border-color: rgba(255, 255, 255, 0.95);
  background: linear-gradient(150deg, rgba(93, 152, 113, 0.95), rgba(47, 107, 79, 0.88));
  box-shadow:
    inset 4rpx 6rpx 10rpx rgba(255, 255, 255, 0.5),
    inset -6rpx -8rpx 14rpx rgba(20, 70, 44, 0.3),
    0 12rpx 28rpx rgba(47, 107, 79, 0.4);
}

.drop-icon-wrap {
  position: relative;
}

.badge {
  position: absolute;
  top: -12rpx;
  right: -18rpx;
  min-width: 34rpx;
  height: 34rpx;
  padding: 0 8rpx;
  border: 2rpx solid #fff;
  border-radius: 999rpx;
  background: #e05c4b;
  color: #fff;
  font-size: 20rpx;
  text-align: center;
  line-height: 30rpx;
}

.drop-text {
  margin-top: 2rpx;
  color: #4e7a5f;
  font-size: 22rpx;
}

.drop.active .drop-text {
  color: #ffffff;
  font-weight: 600;
}
</style>
