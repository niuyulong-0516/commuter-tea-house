<template>
  <view class="page">
    <page-nav-bar title-key="agreement.title" />
    <view v-if="loading" class="state">{{ $t('common.loading') }}</view>
    <view v-else-if="!content" class="state">{{ $t('agreement.empty') }}</view>
    <view v-else class="content">{{ content }}</view>
  </view>
</template>

<script>
import { fetchAgreement } from '@/api/agreements'
import PageNavBar from '@/components/page-nav-bar.vue'

export default {
  components: { PageNavBar },
  data() {
    return {
      content: '',
      loading: true,
    }
  },
  onLoad(options) {
    this.code = options.code || 'terms'
    uni.setNavigationBarTitle({
      title: this.code === 'privacy' ? this.$t('about.privacy') : this.$t('about.terms'),
    })
    this.load()
  },
  methods: {
    async load() {
      this.loading = true
      try {
        const data = await fetchAgreement(this.code)
        this.content = data.content || ''
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
        this.content = ''
      } finally {
        this.loading = false
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

.content {
  padding: 32rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.72);
  border: 2rpx solid rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16rpx) saturate(1.2);
  color: #3c4a42;
  font-size: 28rpx;
  white-space: pre-wrap;
  word-break: break-all;
}

.state {
  padding: 120rpx 40rpx;
  color: #a1aaa4;
  font-size: 26rpx;
  text-align: center;
}
</style>
