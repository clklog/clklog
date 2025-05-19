import request from '@/utils/requestManage'


export function addApp(params) {
  return request({
    url: '/project/add',
    method: 'post',
    data: params
  })
}

export function editApp(params) {
  return request({
    url: '/project/edit',
    method: 'post',
    data: params
  })
}

export function getApp(params) {
  return request({
    url: '/project/get',
    method: 'post',
    data: params
  })
}

export function deleteApp(params) {
  return request({
    url: '/project/delete',
    method: 'post',
    data: params
  })
}

export function getAppList(params) {
  return request({
    url: '/project/getlist',
    method: 'post',
    data: params
  })
} 

export function getAppMyList() {
  return request({
    url: '/project/getMyList',
    method: 'post',
    data: {}
  })
}