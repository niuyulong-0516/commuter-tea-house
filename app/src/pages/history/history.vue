<template>
  <post-list-page
    title-key="drawer.history"
    :load="fetchHistory"
    :empty-text="$t('history.empty')"
    :open-url="openUrl"
  />
</template>

<script>
import PostListPage from '@/components/post-list-page.vue'
import { fetchHistory } from '@/api/library'

export default {
  components: { PostListPage },
  data() {
    return {
      fetchHistory,
    }
  },
  onLoad() {
    uni.setNavigationBarTitle({ title: this.$t('drawer.history') })
  },
  methods: {
    openUrl(post) {
      return `/pages/detail/detail?id=${post.id}`
    },
  },
}
</script>
