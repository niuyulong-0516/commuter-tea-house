import { request, uploadImage } from '@/utils/request'

export function fetchPosts({ category = 'ALL', transportType = 'ALL', sort = 'latest', page = 0, size = 10 } = {}) {
  const query = [`page=${page}`, `size=${size}`, `sort=${sort}`]
  if (category && category !== 'ALL') {
    query.push(`category=${category}`)
  }
  if (transportType && transportType !== 'ALL') {
    query.push(`transportType=${transportType}`)
  }
  return request({ url: `/api/posts?${query.join('&')}` })
}

export function fetchPostDetail(id) {
  return request({ url: `/api/posts/${id}` })
}

export function searchPosts(keyword, page = 0, size = 10) {
  const query = [`keyword=${encodeURIComponent(keyword)}`, `page=${page}`, `size=${size}`]
  return request({ url: `/api/posts/search?${query.join('&')}` })
}

export function createPost(payload) {
  return request({
    url: '/api/posts',
    method: 'POST',
    data: payload,
  })
}

export function uploadPostImage(filePath) {
  return uploadImage(filePath)
}
