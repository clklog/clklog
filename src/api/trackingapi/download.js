import request from "@/utils/requestDownLoad";
import { blob } from "jszip/lib/support";
// 趋势分析
export function exportFlowTrendDetailApi(params) {
  return request({
    url: "/download/exportFlowTrendDetail",
    method: "post",
    data: params,
    responseType: "blob",
  });
}
// 搜索词分析
// 
export function exportSearchWordDetailApi(params) {
  return request({
    url: "/download/exportSearchWordDetail",
    method: "post",
    data: params,
    responseType: "blob",
  });
}
// 地域分析
export function exportAreaDetailApi(params) {
  return request({
    url: "/download/exportAreaDetail",
    method: "post",
    data: params,
    responseType: "blob",
  });
}
// 受访页面分析
export function exportVisitUriDetailApi(params) {
  return request({
    url: "/download/exportVisitUriDetail",
    method: "post",
    data: params,
    responseType: "blob",
  });
}
//  来源网站
export function exportSourceWebsiteDetailApi(params) {
  return request({
    url: "/download/exportSourceWebsiteDetail",
    method: "post",
    data: params,
    responseType: "blob",
  });
}
// 新老访客
export function exportVisitorDetailApi(params) {
  return request({
    url: "/download/exportVisitorDetail",
    method: "post",
    data: params,
    responseType: "blob",
  });
}
// 用户忠诚度分析
export function exportVisitorApi(params) {
  return request({
    url: "/download/exportVisitor",
    method: "post",
    data: params,
    responseType: "blob",
  });
}
// 设备分析
export function exportDeviceDetailApi(params) {
  return request({
    url: "/download/exportDeviceDetail",
    method: "post",
    data: params,
    responseType: "blob",
  });
}
// 渠道分析
export function exportChannelDetailApi(params) {
  return request({
    url: "/download/exportChannelDetail",
    method: "post",
    data: params,
    responseType: "blob",
  });
}
// 用户行为分析
export function exportVisitorListApi(params) {
  return request({
    url: "/download/exportVisitorList",
    method: "post",
    data: params,
    responseType: "blob",
  });
}
