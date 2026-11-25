<template>
  <view class="page">
    <view class="card editor">
      <text class="section-label">{{ $t('publish.sectionLabel') }}</text>

      <view class="transport-tip">
        <text>{{ $t('publish.publishTo', { type: transportTypeLabel }) }}</text>
        <text class="transport-hint">{{ $t('publish.switchHint') }}</text>
      </view>

      <view class="category-row">
        <view
          v-for="item in categoryOptions"
          :key="item.key"
          class="category-item"
          :class="{ active: category === item.key }"
          @click="category = item.key"
        >
          <text class="category-name">{{ $t(item.label) }}</text>
          <text class="category-desc">{{ $t(item.desc) }}</text>
        </view>
      </view>

      <textarea
        v-model="content"
        class="editor-textarea"
        :maxlength="500"
        :placeholder="placeholder"
        placeholder-class="placeholder"
      />
      <view class="editor-foot">
        <text class="editor-hint">{{ $t('publish.editorHint') }}</text>
        <text class="counter">{{ $t('publish.counter', { count: content.length }) }}</text>
      </view>

      <view class="image-grid">
        <view
          v-for="(image, index) in images"
          :key="image"
          class="image-wrap"
        >
          <image
            class="image-preview"
            :src="image"
            mode="aspectFill"
            @click="previewImage(index)"
          />
          <view class="image-remove" @click.stop="removeImage(index)">×</view>
        </view>
        <view
          v-if="images.length < 9"
          class="image-add"
          @click="chooseImages"
        >
          <text class="image-add-plus">+</text>
          <text class="image-add-text">{{ $t('publish.image') }}</text>
        </view>
      </view>
      <text class="image-tip">{{ $t('publish.imageTip') }}</text>
    </view>

    <view class="card anon-card">
      <view class="anon-copy">
        <text class="anon-title">{{ $t('publish.anonymous') }}</text>
        <text class="anon-desc">
          {{ anonymous ? $t('publish.anonymousOn') : $t('publish.anonymousOff') }}
        </text>
      </view>
      <switch
        :checked="anonymous"
        color="#2f6b4f"
        @change="onAnonymousChange"
      />
    </view>

    <view class="card rules">
      <text class="rules-title">{{ $t('publish.rulesTitle') }}</text>
      <text class="rule-item">{{ $t('publish.rule1') }}</text>
      <text class="rule-item">{{ $t('publish.rule2') }}</text>
      <text class="rule-item">{{ $t('publish.rule3') }}</text>
    </view>

    <view class="bottom-space"></view>
    <view class="bottom-bar">
      <view class="draft-button" @click="saveAsDraft">{{ $t('publish.saveDraft') }}</view>
      <button
        class="submit-button"
        :class="{ disabled: !canSubmit }"
        :disabled="!canSubmit"
        @click="submit"
      >
        {{ submitting ? $t('publish.submitting') : $t('publish.submit') }}
      </button>
    </view>
  </view>
</template>

<script>
import { createPost, uploadPostImage } from '@/api/posts'
import { saveDraft, fetchDraft, deleteDraft } from '@/api/library'
import { getTransportType } from '@/utils/auth'

export default {
  data() {
    return {
      categoryOptions: [
        { key: 'FRESH', label: 'publish.freshLabel', desc: 'publish.freshDesc' },
        { key: 'RANT', label: 'publish.rantLabel', desc: 'publish.rantDesc' },
      ],
      category: 'FRESH',
      content: '',
      images: [],
      anonymous: true,
      submitting: false,
      draftId: null,
    }
  },
  onLoad(options) {
    this.draftId = options.draftId || null
    if (!this.draftId) return
    fetchDraft(this.draftId).then((draft) => {
      this.content = draft.content || ''
      this.category = draft.category || 'FRESH'
      this.anonymous = !!draft.anonymous
      this.images = draft.images || []
    }).catch((error) => {
      uni.showToast({ title: error.message, icon: 'none' })
    })
  },
  computed: {
    transportTypeLabel() {
      return getTransportType() === 'BUS'
        ? this.$t('typeSelect.bus')
        : this.$t('typeSelect.rail')
    },
    placeholder() {
      return this.category === 'RANT'
        ? this.$t('publish.placeholderRant')
        : this.$t('publish.placeholderFresh')
    },
    canSubmit() {
      return this.content.trim().length > 0 && !this.submitting
    },
  },
  onLoad() {
    uni.setNavigationBarTitle({ title: this.$t('publish.sectionLabel') })
  },
  methods: {
    chooseImages() {
      const remaining = 9 - this.images.length
      if (remaining <= 0) return

      uni.chooseImage({
        count: remaining,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          this.images = this.images.concat(res.tempFilePaths).slice(0, 9)
        },
      })
    },
    removeImage(index) {
      this.images.splice(index, 1)
    },
    previewImage(index) {
      uni.previewImage({
        current: this.images[index],
        urls: this.images,
      })
    },
    onAnonymousChange(event) {
      this.anonymous = event.detail.value
    },
    async saveAsDraft() {
      const data = {
        content: this.content,
        category: this.category,
        anonymous: this.anonymous,
        images: this.images,
      }
      uni.showLoading({ title: this.$t('common.loading'), mask: true })
      try {
        const draft = await saveDraft(data, this.draftId)
        this.draftId = draft.id
        uni.showToast({ title: this.$t('publish.draftSaved'), icon: 'none' })
      } catch (error) {
        uni.showToast({ title: error.message, icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },
    async submit() {
      const content = this.content.trim()
      if (!content) {
        uni.showToast({ title: this.$t('publish.emptyContent'), icon: 'none' })
        return
      }
      if (this.submitting) return

      this.submitting = true
      uni.showLoading({ title: this.$t('publish.submitting'), mask: true })

      try {
        const uploadedImages = []
        for (const image of this.images) {
          const result = await uploadPostImage(image)
          if (!result || !result.url) {
            throw new Error(this.$t('errors.imageUploadFailed'))
          }
          uploadedImages.push(result.url)
        }

        await createPost({
          content,
          category: this.category,
          anonymous: this.anonymous,
          images: uploadedImages,
        })

        if (this.draftId) {
          deleteDraft(this.draftId).catch(() => {})
        }
        uni.hideLoading()
        uni.showToast({ title: this.$t('publish.success'), icon: 'none' })
        uni.$emit('post:published')

        setTimeout(() => {
          const pages = getCurrentPages()
          if (pages.length > 1) {
            uni.navigateBack()
          } else {
            uni.reLaunch({ url: '/pages/index/index' })
          }
        }, 600)
      } catch (error) {
        uni.hideLoading()
        uni.showToast({ title: error.message, icon: 'none' })
      } finally {
        this.submitting = false
      }
    },
  },
}
</script>

<style>
.page {
  padding: 28rpx 28rpx 0;
  background: #f4f0e6;
}

.card {
  margin-bottom: 24rpx;
  padding: 30rpx;
  border-radius: 28rpx;
  background: #fff;
  box-shadow: 0 8rpx 28rpx rgba(43, 58, 49, 0.05);
}

.section-label {
  display: block;
  color: #29342d;
  font-size: 30rpx;
  font-weight: 600;
}

.transport-tip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 18rpx;
  padding: 18rpx 20rpx;
  border-radius: 16rpx;
  background: #edf4ef;
  color: #2f6b4f;
  font-size: 26rpx;
  font-weight: 600;
}

.transport-hint {
  color: #8e9a92;
  font-size: 22rpx;
  font-weight: 400;
}

.category-row {
  display: flex;
  margin-top: 22rpx;
  gap: 16rpx;
}

.category-item {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding: 22rpx;
  border: 2rpx solid #eee9df;
  border-radius: 20rpx;
  background: #fbf9f4;
}

.category-item.active {
  border-color: #2f6b4f;
  background: #edf4ef;
}

.category-name {
  color: #35433a;
  font-size: 28rpx;
  font-weight: 600;
}

.category-item.active .category-name {
  color: #2f6b4f;
}

.category-desc {
  margin-top: 8rpx;
  color: #8e9a92;
  font-size: 22rpx;
}

.editor-textarea {
  width: 100%;
  height: 320rpx;
  margin-top: 24rpx;
  padding: 22rpx;
  border-radius: 20rpx;
  background: #f8f5ee;
  color: #2b352f;
  font-size: 30rpx;
}

.placeholder {
  color: #a7b0aa;
}

.editor-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 14rpx;
}

.editor-hint {
  flex: 1;
  color: #a1aaa4;
  font-size: 22rpx;
}

.counter {
  margin-left: 20rpx;
  color: #a1aaa4;
  font-size: 22rpx;
}

.image-grid {
  display: flex;
  flex-wrap: wrap;
  margin-top: 24rpx;
  gap: 14rpx;
}

.image-wrap,
.image-add {
  position: relative;
  width: calc((100% - 28rpx) / 3);
  height: 190rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.image-preview {
  width: 100%;
  height: 100%;
}

.image-remove {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: rgba(20, 28, 23, 0.65);
  color: #fff;
  font-size: 28rpx;
}

.image-add {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2rpx dashed #d8d2c6;
  background: #fbf9f4;
}

.image-add-plus {
  color: #8b978f;
  font-size: 44rpx;
  font-weight: 300;
}

.image-add-text {
  margin-top: 2rpx;
  color: #9aa49e;
  font-size: 22rpx;
}

.image-tip {
  display: block;
  margin-top: 14rpx;
  color: #a1aaa4;
  font-size: 22rpx;
}

.anon-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.anon-copy {
  display: flex;
  flex-direction: column;
}

.anon-title {
  color: #29342d;
  font-size: 28rpx;
  font-weight: 600;
}

.anon-desc {
  margin-top: 8rpx;
  color: #8e9a92;
  font-size: 22rpx;
}

.rules {
  background: #fbf7ed;
  box-shadow: none;
}

.rules-title {
  display: block;
  color: #6d7a71;
  font-size: 24rpx;
  font-weight: 600;
}

.rule-item {
  display: block;
  margin-top: 12rpx;
  color: #8e9a92;
  font-size: 22rpx;
}

.bottom-space {
  height: calc(180rpx + env(safe-area-inset-bottom));
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 28rpx calc(20rpx + env(safe-area-inset-bottom));
  background: rgba(244, 240, 230, 0.96);
}

.draft-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 200rpx;
  height: 92rpx;
  border: 2rpx solid #2f6b4f;
  border-radius: 999rpx;
  background: #fff;
  color: #2f6b4f;
  font-size: 28rpx;
  font-weight: 600;
}

.submit-button {
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;
  height: 92rpx;
  border-radius: 999rpx;
  background: #2f6b4f;
  color: #fff;
  font-size: 30rpx;
  font-weight: 600;
}

.submit-button.disabled {
  background: #b8c4bc;
  color: #f4f0e6;
}
</style>
