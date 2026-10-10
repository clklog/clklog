import request from "@/utils/requestManage";

// 属性 分页获取基本属性列表
export function getPageListApi(params) {
  return request({
    url: "/normal/property/getPageList",
    method: "post",
    data: params,
  });
}
// 获取基本属性分组列表
export function getGroupNameListApi(params) {
  return request({
    url: "/normal/property/getGroupNameList",
    method: "post",
    data: params,
  });
}
// 获取基本属性标签列表
export function getTagListApi(params) {
  return request({
    url: "/normal/property/getTagList",
    method: "post",
    data: params,
  });
}
// 新增
export function addApi(params) {
  return request({
    url: "/normal/property/add",
    method: "post",
    data: params,
  });
}
// 删除
export function deleteApi(params) {
  return request({
    url: "/normal/property/delete",
    method: "post",
    data: params,
  });
}
// 编辑
export function editApi(params) {
  return request({
    url: "/normal/property/edit",
    method: "post",
    data: params,
  });
}
// 获取基本属性详情
export function getApi(params) {
  return request({
    url: "/normal/property/get",
    method: "post",
    data: params,
  });
}
