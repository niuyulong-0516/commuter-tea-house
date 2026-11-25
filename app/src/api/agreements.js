import { request } from '@/utils/request'

export function fetchAgreement(code) {
  return request({ url: `/api/agreements/${code}` })
}
