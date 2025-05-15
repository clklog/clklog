import axios from "axios";
import { MessageBox, Message } from "element-ui";
import store from "@/store";
import { getToken } from "@/utils/auth";
import Router from '@/router';
// create an axios instance
const service = axios.create({
  baseURL: "",
  timeout: 5000,
});
// request interceptor
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
        if (
          config.url == "/info/skip-subscription" ||
          config.url == "/info/subscribe"
        ) {
          config.url = "/DEV-APISUB" + config.url;
        } else {
          config.url = "/DEV-API" + config.url;
        }
      } else {
        // production
        if (
          config.url == "/info/skip-subscription" ||
          config.url == "/info/subscribe"
        ) {
          config.url = "https://support.clklog.com/public" + config.url;
        } else {
          config.url = window.globalConfig.BASE_API + config.url;
        }
      }
    } else if (config.method === "get") {
      if (process.env.NODE_ENV == "development") {
        config.url = "/DEV-API" + config.url;
      } else {
        config.url = window.globalConfig.BASE_API + config.url;
      }
    }
    return config;
  },
  (error) => {
    // do something with request error
    console.log(error); // for debug
    return Promise.reject(error);
  }
);
let isLoggingOut = false; // 添加标志位，防止死循环
// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
   */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  (response) => {
    const res = response.data;

    // if the custom code is not 200, it is judged as an error.
    if (res.code !== 200) {
      if (response.config.url.includes("info/subscribe")) {
        Message({
          message: "订阅失败",
          type: "error",
          duration: 5 * 1000,
        });
        return;
      }

      if (res.code == 403) {
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
      Message({
        message: res.message || "Error",
        type: "error",
        duration: 5 * 1000,
      });

      // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
        // to re-login
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
      return Promise.reject(new Error(res.message || "Error"));
    } else {
      return res;
    }
  },
  (error) => {
    console.log("err" + error); // for debug
    Message({
      message: error.message,
      type: "error",
      duration: 5 * 1000,
    });
    return Promise.reject(error);
  }
);

export default service;
