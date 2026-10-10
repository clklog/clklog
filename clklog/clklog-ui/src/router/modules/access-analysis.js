import Layout from "@/layout";
const nestedRouter = {
  path: "/access",
  component: Layout,
  redirect: "/access/visitedPage",
  name: "access",
  alwaysShow: true,
  meta: {
    title: "访问分析",
    icon: "access",
  },
  children: [
    {
        path: "visitedPage",
        component: () => import("@/views/visitor-analysis/visited-page"),
        name: "visitedPage",
        meta: { title: "受访页面" },
      },
      {
        path: "search",
        component: () => import("@/views/visitor-analysis/search-analysis"),
        name: "search",
        meta: { title: "站外搜索" },
      },
  ],
};

export default nestedRouter;
