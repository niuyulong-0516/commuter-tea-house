<template>
  <post-list-page
    :load="fetchMyPosts"
    :empty-text="$t('myPosts.empty')"
    :open-url="openUrl"
  />
</template>

<script>
import PostListPage from '@/components/post-list-page.vue'
import { fetchMyPosts } from '@/api/library'

export default {
  components: { PostListPage },
  data() {
    return {
      fetchMyPosts,
    }
  },
  onLoad() {
    uni.setNavigationBarTitle({ title: this.$t('drawer.myPosts') })
  },
  methods: {
    openUrl(post) {
      return `/pages/detail/detail?id=${post.id}`
    },
  },
}
</script>
