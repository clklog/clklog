import request from '@/utils/requestManage'

export function saveSetting(params) {
  return request({
    url: '/global/saveSetting',
    method: 'post',
    data: params
  })
} 

export function getSetting() {
  return request({
    url: '/global/getSetting',
    method: 'post',
    data: {}
  })
} 

export function getStat(params) {
  return request({
    url: '/global/getStat',
    method: 'post',
    data: params
  })
} 