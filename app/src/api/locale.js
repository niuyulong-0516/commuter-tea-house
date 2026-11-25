import { request } from '@/utils/request'

/** 按客户端 IP 探测默认语言，免登录。 */
export function detectLocale() {
  return request({ url: '/api/locale/detect' })
}
