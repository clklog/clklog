import request from "@/utils/request";
// 获取标识
export function profileApi(query) {
  return request({
    url: "/profile",
    method: "get",
    params: query
  });
}
//提交激活状态
export function subscribeActiveApi(params) {
  return request({
    url: "/profile/subscribe",
    method: "post",
    data: params
  });
}
// 提交订阅
export function subscribeApi(params) {
  return request({
    url: "/info/subscribe",
    method: 'post',
    data: params
  });
}
// 跳过订阅
export function skipsubscriptionApi(params) {
  return request({
    url: "/info/skip-subscription",
    method: 'post',
    data: params
  });
}
