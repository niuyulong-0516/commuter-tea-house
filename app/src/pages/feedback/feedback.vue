<template>
  <view class="page">
    <view class="card">
      <view class="field">
        <text class="field-label">{{ $t('feedback.description') }} <text class="required">*</text></text>
        <textarea
          class="textarea"
          v-model="description"
          :placeholder="$t('feedback.descriptionPlaceholder')"
          placeholder-class="placeholder"
          maxlength="1000"
        />
        <text class="counter">{{ description.length }}/1000</text>
      </view>

      <view class="field">
        <text class="field-label">{{ $t('feedback.screenshots') }}</text>
        <view class="image-grid">
          <view v-for="(image, index) in images" :key="index" class="image-item">
            <image :src="image" mode="aspectFill" />
            <view class="image-remove" @click="removeImage(index)">×</view>
          </view>
          <view v-if="images.length < 3" class="image-add" @click="chooseImages">
            <text class="add-plus">+</text>
            <text class="add-text">{{ images.length }}/3</text>
          </view>
        </view>
      </view>

      <view class="field">
        <text class="field-label">{{ $t('feedback.whatsapp') }}</text>
        <input
          class="field-input"
          v-model="whatsapp"
          :placeholder="$t('feedback.whatsappPlaceholder')"
          placeholder-class="placeholder"
          maxlength="64"
        />
      </view>
    </view>

    <view class="submit-button" @click="submit">{{ $t('feedback.submit') }}</view>
  </view>
</template>

<script>
import { submitFeedback } from '@/api/feedback'
import { uploadPostImage } from '@/api/posts'

export default {
  data() {
    return {
      description: '',
      images: [],
      whatsapp: '',
      submitting: false,
    }
  },
  onLoad() {
    uni.setNavigationBarTitle({ title: this.$t('feedback.title') })
  },
  methods: {
    chooseImages() {
      const remaining = 3 - this.images.length
      uni.chooseImage({
        count: remaining,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: async (res) => {
          uni.showLoading({ title: this.$t('common.loading'), mask: true })
          try {
            for (const path of res.tempFilePaths) {
              const result = await uploadPostImage(path)
              if (result && result.url) this.images.push(result.url)
            }
          } catch (error) {
            uni.showToast({ title: error.message, icon: 'none' })
          } finally {
            uni.hideLoading()
          }
        },
      })
    },
    removeImage(index) {
      this.images.splice(index, 1)
    },
    async submit() {
      const description = this.description.trim()
      if (!description) {
        uni.showToast({ title: this.$t('feedback.descriptionRequired'), icon: 'none' })
        return
      }
      if (this.submitting) return
      this.submitting = true
      uni.showLoading({ title: this.$t('common.loading'), mask: true })
      try {
        await submitFeedback({
          description,
          images: this.images,
          whatsapp: this.whatsapp.trim(),
        })
        uni.hideLoading()
        uni.showToast({ title: this.$t('feedback.success'), icon: 'none' })
        setTimeout(() => uni.navigateBack(), 600)
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
  padding: 28rpx;
  background: transparent;
}

.card {
  padding: 28rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.72);
  border: 2rpx solid rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16rpx) saturate(1.2);
  box-shadow: 0 8rpx 28rpx rgba(43, 58, 49, 0.05);
}

.field {
  margin-bottom: 32rpx;
}

.field:last-child {
  margin-bottom: 0;
}

.field-label {
  display: block;
  margin-bottom: 14rpx;
  color: #6d7a71;
  font-size: 24rpx;
}

.required {
  color: #c05b4a;
}

.textarea {
  width: 100%;
  height: 240rpx;
  padding: 20rpx;
  border-radius: 16rpx;
  background: #f8f5ee;
  color: #2b352f;
  font-size: 26rpx;
}

.placeholder {
  color: #a7b0aa;
}

.counter {
  display: block;
  margin-top: 8rpx;
  color: #a1aaa4;
  font-size: 20rpx;
  text-align: right;
}

.image-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.image-item,
.image-add {
  position: relative;
  width: 160rpx;
  height: 160rpx;
  overflow: hidden;
  border-radius: 16rpx;
}

.image-item image {
  width: 100%;
  height: 100%;
}

.image-remove {
  position: absolute;
  top: 0;
  right: 0;
  width: 40rpx;
  height: 40rpx;
  border-radius: 0 0 0 16rpx;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 28rpx;
  text-align: center;
}

.image-add {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2rpx dashed #c9d2cb;
  background: #fbf9f4;
}

.add-plus {
  color: #8e9a92;
  font-size: 48rpx;
}

.add-text {
  color: #a1aaa4;
  font-size: 20rpx;
}

.field-input {
  width: 100%;
  height: 80rpx;
  padding: 0 20rpx;
  border-radius: 16rpx;
  background: #f8f5ee;
  color: #2b352f;
  font-size: 26rpx;
}

.submit-button {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 88rpx;
  margin-top: 32rpx;
  border-radius: 999rpx;
  background: #2f6b4f;
  color: #fff;
  font-size: 28rpx;
  font-weight: 600;
}
</style>
