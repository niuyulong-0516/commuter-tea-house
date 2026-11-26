<template>
  <view class="page">
    <page-nav-bar />
    <view v-if="loading" class="state">{{ $t('common.loading') }}</view>
    <view v-else-if="!drafts.length" class="state">{{ $t('drafts.empty') }}</view>
    <view v-else class="list">
      <view v-for="draft in drafts" :key="draft.id" class="draft-card" @click="editDraft(draft)">
        <view class="draft-head">
          <text class="draft-tag">{{ $t('drafts.tag') }}</text>
          <text class="draft-time">{{ formatTime(draft.updatedAt) }}</text>
        </view>
        <text class="draft-content">{{ draft.content || $t('drafts.noContent') }}</text>
        <view class="draft-footer">
          <text class="draft-category">{{ draft.category === 'RANT' ? $t('home.rant') : $t('home.fresh') }}</text>
          <text class="draft-delete" @click.stop="removeDraft(draft)">{{ $t('drafts.delete') }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { fetchDrafts, deleteDraft } from '@/api/library'
import PageNavBar from '@/components/page-nav-bar.vue'
import { timeAgoParts } from '@/utils/format'

export default {
  data() {
    return {
      drafts: [],
      loading: true,
    }
  },
  onLoad() {
    uni.setNavigationBarTitle({ title: this.$t('drawer.drafts') })
    this.load()
  },
  methods: {
    formatTime(value) {
      const parts = timeAgoParts(value)
      return parts ? this.$t(parts.key, parts) : ''
    },
    async load() {
      this.loading = true
      try {
        this.drafts = await fetchDrafts() || []
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
        this.drafts = []
      } finally {
        this.loading = false
      }
    },
    editDraft(draft) {
      uni.navigateTo({ url: `/pages/publish/publish?draftId=${draft.id}` })
    },
    removeDraft(draft) {
      uni.showModal({
        title: this.$t('drafts.deleteTitle'),
        content: this.$t('drafts.deleteContent'),
        success: async (res) => {
          if (!res.confirm) return
          try {
            await deleteDraft(draft.id)
            this.drafts = this.drafts.filter((item) => item.id !== draft.id)
            uni.showToast({ title: this.$t('drafts.deleted'), icon: 'none' })
          } catch (error) {
            uni.showToast({ title: error.message, icon: 'none' })
          }
        },
      })
    },
  },
}
</script>

<style>
.page {
  padding: 28rpx;
  background: transparent;
}

.state {
  padding: 120rpx 40rpx;
  color: #a1aaa4;
  font-size: 26rpx;
  text-align: center;
}

.draft-card {
  margin-bottom: 24rpx;
  padding: 28rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.72);
  border: 2rpx solid rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16rpx) saturate(1.2);
  box-shadow: 0 8rpx 28rpx rgba(43, 58, 49, 0.06);
}

.draft-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.draft-tag {
  padding: 6rpx 16rpx;
  border-radius: 999rpx;
  background: #edf4ef;
  color: #2f6b4f;
  font-size: 20rpx;
}

.draft-time {
  color: #a1aaa4;
  font-size: 20rpx;
}

.draft-content {
  display: block;
  margin-top: 20rpx;
  color: #3c4a42;
  font-size: 28rpx;
}

.draft-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 2rpx solid #f3efe6;
}

.draft-category {
  color: #8e9a92;
  font-size: 22rpx;
}

.draft-delete {
  color: #b56b42;
  font-size: 22rpx;
}
</style>
