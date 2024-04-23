import axios from "axios";
import { MessageBox, Message } from "element-ui";
import store from "@/store";
import { getToken } from "@/utils/auth";
// create an axios instance
const service = axios.create({
  baseURL: "", 
  timeout: 5000,
});
// request interceptor
service.interceptors.request.use(
  (config) => {
    // do something before request is sent
    // if (store.getters.token) {
    //   config.headers["X-Token"] = getToken();
    // }
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
      } else{
        config.url =  window.globalConfig.BASE_API + config.url;
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
