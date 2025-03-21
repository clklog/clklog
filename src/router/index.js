import Vue from "vue";
import Router from "vue-router";
Vue.use(Router);
import Layout from "@/layout";
import visitorAnalysis from "./modules/visitor-analysis";
import accessAnalysis from "./modules/access-analysis";
import userBehavior from "./modules/user-behavior";

export const constantRoutes = [
  {
    path: "/redirect",
    component: Layout,
    hidden: true,
    children: [
      {
        path: "/redirect/:path(.*)",
        component: () => import("@/views/redirect/index"),
      },
    ],
  },
  {
    path: "/login",
    component: () => import("@/views/login/index"),
    hidden: true,
  },
  {
    path: "/auth-redirect",
    component: () => import("@/views/login/auth-redirect"),
    hidden: true,
  },
  {
    path: "/404",
    component: () => import("@/views/error-page/404"),
    hidden: true,
  },
  {
    path: "/401",
    component: () => import("@/views/error-page/401"),
    hidden: true,
  },
  {
    path: "/",
    component: Layout,
    redirect: "/index",
    children: [
      {
        path: "index",
        component: () => import("@/views/home/index"),
        name: "Documentation",
        meta: { title: "数据概览", icon: "homepage", affix: true },
      },
    ],
  },
  {
    path: "/trendAnalysis",
    component: Layout,
    redirect: "/trend",
    children: [
      {
        path: "trend",
        component: () => import("@/views/visitor-analysis/trend-analysis"),
        name: "trend",
        meta: { title: "趋势分析", icon: "trend", affix: true },
      },
    ],
  },
];

export const asyncRoutes = [
  visitorAnalysis,
  accessAnalysis,
  userBehavior,
  {
    path: "/pro",
    component: Layout,
    redirect: "/clklog",
    children: [
      {
        path: "https://pro.clklog.com",
        name: "clklog",
        meta: { title: "商业版功能", icon: "morefunc", affix: true },
      },
    ],
  },
  { path: "*", redirect: "/index", hidden: true },
];

const createRouter = () =>
  new Router({
    // mode: 'history', // require service support
    scrollBehavior: () => ({ y: 0 }),
    routes: constantRoutes,
  });

const router = createRouter();
export function resetRouter() {
  const newRouter = createRouter();
  router.matcher = newRouter.matcher; // reset router
}

export default router;
