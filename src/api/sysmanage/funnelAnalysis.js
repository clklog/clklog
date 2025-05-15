import request from '@/utils/requestManage'

export function addsApi(params) {
  return request({
    url: "/funnel/add",
    method: "post",
    data: params,
  });
}
export function deleteApi(params) {
  return request({
    url: "/funnel/delete",
    method: "post",
    data: params,
  });
}

export function editApi(params) {
  return request({
    url: "/funnel/edit",
    method: "post",
    data: params,
  });
}
//  分页获取
export function getPageListApi(params) {
  return request({
    url: "/funnel/getPageList",
    method: "post",
    data: params,
  });
}
//   获取漏斗详情
export function getApi(params) {
  return request({
    url: "/funnel/get",
    method: "post",
    data: params,
  });
}
