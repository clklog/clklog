import "babel-polyfill";

import Vue from "vue";
import Cookies from "js-cookie";

import "normalize.css/normalize.css"; // a modern alternative to CSS resets

import Element from "element-ui";
import "./styles/element-variables.scss";
// import enLang from 'element-ui/lib/locale/lang/en'// 如果使用中文语言包请默认支持，无需额外引入，请删除该依赖

import "@/styles/index.scss"; // global css

import App from "./App";
import store from "./store";
import router from "./router";

import "./icons"; // icon
import "./permission"; // permission control
import "./utils/error-log"; // error log

import * as filters from "./filters"; // global filters

import echarts from "echarts";
import { fixedWidth } from '@/components/el-table-width';
Vue.prototype.$fixedWidth = fixedWidth;
Vue.prototype.$echarts = echarts;
import { sharedText } from '@/components/pointFont/index';
Vue.prototype.$sharedText = sharedText;

/**
 * 仅开发/非生产环境启用 MockJs，生产构建不会执行。
 * 若不需要可直接删除下方调用。
 */
if (process.env.NODE_ENV !== "production") {
  const { mockXHR } = require("../mock");
  mockXHR();
}

Vue.use(Element, {
  size: Cookies.get("size") || "medium", // set element-ui default size
  // locale: enLang // 如果使用中文，无需设置，请删除
});

// register global utility filters
Object.keys(filters).forEach((key) => {
  Vue.filter(key, filters[key]);
});

Vue.config.productionTip = false;

new Vue({
  el: "#app",
  router,
  store,
  render: (h) => h(App),
  beforeCreate() {
    Vue.prototype.$bus = this;
  },
});
