import request from '@/utils/requestManage'
export function login(params) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: params
  })
}

export function getInfo(params) {
  return request({
    url: '/auth/getUser',
    method: 'post',
    data: params
  })
}

export function logout() {
  return request({
    url: '/logout',
    method: 'post',
    data: {}
  })
}

export function getMenuList(params) {
  return request({
    url: '/auth/getMenuList',
    method: 'post',
    data: params
  })
}

export function modifyPassword(params) {
  return request({
    url: '/auth/modifyPassword',
    method: 'post',
    data: params
  })
}