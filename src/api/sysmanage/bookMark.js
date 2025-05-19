import request from '@/utils/requestManage'

// 保存书签
export function addApi(params) {
  return request({
    url: '/bookmark/add',
    method: 'post',
    data: params
  })
}
// 删除书签书签
export function deleteApi(params) {
  return request({
    url: '/bookmark/delete',
    method: 'post',
    data: params
  })
}
// 编辑书签
export function editApi(params) {
  return request({
    url: '/bookmark/edit',
    method: 'post',
    data: params
  })
}
// 分页获取书签列表
export function getPageListApi(params) {
  return request({
    url: '/bookmark/getPageList',
    method: 'post',
    data: params
  })
}
// 获取书签详情
export function getApi(params) {
  return request({
    url: '/bookmark/get',
    method: 'post',
    data: params
  })
}
