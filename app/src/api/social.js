import { request } from '@/utils/request'

export function fetchInteractionState(postId) {
  return request({ url: `/api/posts/${postId}/interactions` })
}

export function toggleLike(postId) {
  return request({ url: `/api/posts/${postId}/like`, method: 'POST' })
}

export function toggleFavorite(postId) {
  return request({ url: `/api/posts/${postId}/favorite`, method: 'POST' })
}

export function fetchComments(postId, page = 0, size = 20) {
  return request({ url: `/api/posts/${postId}/comments?page=${page}&size=${size}` })
}

export function createComment(postId, content) {
  return request({
    url: `/api/posts/${postId}/comments`,
    method: 'POST',
    data: { content },
  })
}

export function fetchFavorites() {
  return request({ url: '/api/posts/favorites' })
}
