import axios from "axios";
import { MessageBox, Message } from "element-ui";
import store from "@/store";
import Router from '@/router';
import { getToken } from "@/utils/auth";
const service = axios.create({
  baseURL: "",
  timeout: 10000,
});
service.interceptors.request.use(
  (config) => {
    if (store.getters.token) {
      config.headers['Authorization'] = 'Bearer ' + getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
    }
    const isMock = config.url.includes("/vue-element-admin/user");
    if ((config.method === "post" || config.method === "put") && !isMock) {
      if (!config.headers["Content-Type"]) {
        config.headers["Content-Type"] = "application/json;";
      }
      if (process.env.NODE_ENV == "development") {
        config.url = "/DEV-API-MANAGE" + config.url;
      } else {
        config.url = window.globalConfig.BASE_API_MANAGE + config.url;
      }
    }
    return config;
  },
  (error) => {
    console.log(error);
    return Promise.reject(error);
  }
);

let isLoggingOut = false; // 添加标志位，防止死循环
let isNoPermission = false; // 无项目权限跳转标志位，防止并发请求重复跳转

service.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code == 403) {
      if (!isLoggingOut) { // 检查标志位
        isLoggingOut = true; // 设置标志位为 true
        store.dispatch("user/logout").then(() => {
          Router.push({ path: "/login" });
          isLoggingOut = false; // 重置标志位
        });
      }
      return; // 直接返回，避免继续处理
    }
    if (res.code !== 200) {
      // code = 423：无该项目的数据访问权限，跳转到无权限页面
      if (res.code == 423) {
        if (!isNoPermission) {
          isNoPermission = true;
          Router.replace("/NoProjectPermission").then(() => {
            isNoPermission = false; // 导航完成后重置，便于下次（如切换项目后）再次跳转
          }).catch(() => {
            isNoPermission = false;
          });
        }
        return; // 直接返回，不弹错误提示
      }

      Message({
        message: res.message || res.msg || "Error",
        type: "error",
        duration: 5 * 1000,
      });
      if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
        MessageBox.confirm(
          "You have been logged out, you can cancel to stay on this page, or log in again",
          "Confirm logout",
          {
            confirmButtonText: "Re-Login",
            cancelButtonText: "Cancel",
            type: "warning",
          }
        ).then(() => {
          store.dispatch("user/resetToken").then(() => {
            location.reload();
          });
        });
      }
      return Promise.reject(new Error(res.message || res.msg || "Error"));
    } else {
      return res;
    }
  },
  (error) => {
    console.log("err" + error);
    Message({
      message: error.message || error.msg || "Error",
      type: "error",
      duration: 5 * 1000,
    });
    return Promise.reject(error);
  }
);
export default service;
