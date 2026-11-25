import { request } from '@/utils/request'

export function fetchBanners() {
  return request({ url: '/api/banners' })
}
