import Layout from "@/layout";
const nestedRouter = {
  path: "/userBehavior",
  component: Layout,
  redirect: "/userBehavior/portrait",
  name: "userBehavior",
  alwaysShow: true,
  meta: {
    title: "用户分析",
    icon: "userBehavior",
  },
  children: [
    {
      path: "portrait",
      component: () => import("@/views/user-behavior/index"),
      name: "portrait",
      meta: { title: "用户画像" },
    },
    {
      path: "userLoyalty",
      component: () => import("@/views/visitor-analysis/user-loyalty-analysis"),
      name: "userLoyalty",
      meta: { title: "忠诚度分析" },
    },
  ],
};

export default nestedRouter;
