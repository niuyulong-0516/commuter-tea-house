import { request } from '@/utils/request'

export function recordView(postId) {
  return request({ url: `/api/library/history/${postId}`, method: 'POST' })
}

export function fetchHistory() {
  return request({ url: '/api/library/history' })
}

export function fetchMyPosts() {
  return request({ url: '/api/library/my-posts' })
}

export function fetchDrafts() {
  return request({ url: '/api/library/drafts' })
}

export function fetchDraft(id) {
  return request({ url: `/api/library/drafts/${id}` })
}

export function saveDraft(data, id) {
  return request({
    url: id ? `/api/library/drafts/${id}` : '/api/library/drafts',
    method: id ? 'PUT' : 'POST',
    data,
  })
}

export function deleteDraft(id) {
  return request({ url: `/api/library/drafts/${id}`, method: 'DELETE' })
}
