import request from '@/utils/requestManage'

export function addApiKey(params) {
  return request({
    url: '/apikey/add',
    method: 'post',
    data: params
  })
}

export function editApiKey(params) {
  return request({
    url: '/apikey/edit',
    method: 'post',
    data: params
  })
}

export function getApiKey(params) {
  return request({
    url: '/apikey/get',
    method: 'post',
    data: params
  })
}

export function deleteApiKey(params) {
  return request({
    url: '/apikey/delete',
    method: 'post',
    data: params
  })
}

export function listApiKey(params) {
  return request({
    url: '/apikey/list',
    method: 'post',
    data: params
  })
}
