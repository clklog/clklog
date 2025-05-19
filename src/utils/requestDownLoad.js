import axios from "axios";
import { MessageBox, Message } from "element-ui";
import store from "@/store";
import { getToken } from "@/utils/auth";
import Router from '@/router';
const service = axios.create({
  baseURL: "",
  timeout: 5000, // request timeout
});
service.interceptors.request.use(
  (config) => {
    if (store.getters.token) {
      config.headers["Authorization"] = "Bearer " + getToken(); // 让每个请求携带自定义token 请根据实际情况自行修改
    }
    const isMock = config.url.includes("/vue-element-admin/user");
    if ((config.method === "post" || config.method === "put") && !isMock) {
      if (!config.headers["Content-Type"]) {
        config.headers["Content-Type"] = "application/json;";
      }
      if (process.env.NODE_ENV == "development") {
        config.url = "/DEV-API" + config.url;
      } else {
        config.url = window.globalConfig.BASE_API + config.url;
      }
    }
    return config;
  },
  (error) => {
    console.log(error); // for debug
    return Promise.reject(error);
  }
);
let isLoggingOut = false; // 添加标志位，防止死循环
service.interceptors.response.use(
  (res) => {
    if (res.status != 200) {
      Message({
        message: res.statusText || "Error",
        type: "error",
        duration: 5 * 1000,
      });
      if (res.status == 403) {
        if (!isLoggingOut) {
          // 检查标志位
          isLoggingOut = true; // 设置标志位为 true
          store.dispatch("user/logout").then(() => {
            Router.push({ path: "/login" });
            isLoggingOut = false; // 重置标志位
          });
        }
        return; // 直接返回，避免继续处理
      }
      return Promise.reject(new Error(res.statusText || "Error"));
    } else {
      return res;
    }
  },
  (error) => {
    console.log("err" + error); // for debug
    Message({
      message: error.statusText || "Error",
      type: "error",
      duration: 5 * 1000,
    });
    return Promise.reject(error);
  }
);

export default service;
