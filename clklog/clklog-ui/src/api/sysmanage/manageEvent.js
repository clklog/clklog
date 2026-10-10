import request from '@/utils/requestManage'
// 事件管理分页获取事件列表
export function getPageListEventApi(params) {
    return request({
      url: '/meta/event/getPageList',
      method: 'post',
      data: params
    })
} 
//获取属性列表(不分组)
export function getListNoGroupEventApi(params) {
    return request({
      url: '/normal/property/getListNoGroup',
      method: 'post',
      data: params
    })
} 
// 获取事件分组列表
export function getGroupNameListEventApi(params) {
    return request({
      url: '/meta/event/getGroupNameList',
      method: 'post',
      data: params
    })
} 
// 获取事件标签列表
export function getTagListEventApi(params) {
    return request({
      url: '/meta/event/getTagList',
      method: 'post',
      data: params
    })
} 
// 编辑列表
export function editEventApi(params) {
    return request({
      url: '/meta/event/edit',
      method: 'post',
      data: params
    })
} 
// 删除列表
export function deleteEventApi(params) {
    return request({
      url: '/meta/event/delete',
      method: 'post',
      data: params
    })
} 
// 添加列表
export function addEventApi(params) {
    return request({
      url: '/meta/event/add',
      method: 'post',
      data: params
    })
} 
// 获取事件详情
export function getEventApi(params) {
    return request({
      url: '/meta/event/get',
      method: 'post',
      data: params
    })
} 
// 获取属性列表-分组
export function getListEventApi(params) {
  return request({
    url: '/normal/property/getList',
    method: 'post',
    data: params
  })
}
// 批量导入功能-自定义事件
export function importDataApi(params) {
  return request({
    url: '/meta/event/importData',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    method: 'post',
    data: params
  })
}
// 导入用户属性
export function importUserApi(params) {
  return request({
    url: '/normal/property/import',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    method: 'post',
    data: params
  })
}
export function getGroupedPropertiesApi(params) {
  return request({
    url: '/meta/event/getGroupedProperties',
    method: 'post',
    data: params
  })
}
// 获取通用属性列表
export function getCommonPropertyListApi(params) {
  return request({
    url: '/normal/property/getCommonPropertyList',
    method: 'post',
    data: params
  })
}
// // 自定义分析 
// export function customAnalysisApi(params) {
//   return request({
//     url: '/proapi/eventanalysis/customAnalysis',
//     method: 'post',
//     data: params
//   })
// }