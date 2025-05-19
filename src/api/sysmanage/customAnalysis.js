import request from "@/utils/request";
// 自定义分析
export function customAnalysisApi(params) {
  return request({
    url: "/eventanalysis/customAnalysis",
    method: "post",
    data: params,
  });
}

// 获取查询漏斗的id
export function funnelAnalysisApi(params) {
  return request({
    url: "/eventanalysis/funnelAnalysis",
    method: "post",
    data: params,
  });
}
// 异步任务
export function taskRueryApi(params) {
  return request({
    url: "/task/query",
    method: "post",
    data: params,
  });
}
// 最终结果
export function taskFetchApi(params) {
  return request({
    url: "/task/fetch",
    method: "post",
    data: params,
  });
}