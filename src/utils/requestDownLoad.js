import axios from "axios";
import { MessageBox, Message } from "element-ui";
import store from "@/store";
import { getToken } from "@/utils/auth";
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API ? process.env.VUE_APP_BASE_API : "/api", // url = base url + request url
  timeout: 5000, // request timeout
});
service.interceptors.request.use(
  (config) => {
    if (store.getters.token) {
      config.headers["X-Token"] = getToken();
    }
    const isMock = config.url.includes("/vue-element-admin/user");
    if ((config.method === "post" || config.method === "put") && !isMock) {
      if (!config.headers["Content-Type"]) {
        config.headers["Content-Type"] = "application/json;";
        // config.headers["Content-Type"] = "multipart/form-data;";
      }
      config.data = JSON.stringify(config.data);
    }
    return config;
  },
  (error) => {
    console.log(error); // for debug
    return Promise.reject(error);
  }
);
service.interceptors.response.use(
  (res) => {
    // console.log(res, "下载------");
    // return res;
    // if (!res.type) {
    //   Message({
    //     message: res.message || "Error",
    //     type: "error",
    //     duration: 5 * 1000,
    //   });
    //   return Promise.reject(new Error(res.message || "Error"));
    // } else {
    //   return response;
    // }
    if (res.status != 200) {
      Message({
        message: res.statusText || "Error",
        type: "error",
        duration: 5 * 1000,
      });
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
