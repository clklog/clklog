import Layout from '@/layout'
import yhfx from '@/assets/images/yhfx.png'
const nestedRouter = {
  path: '/userBehavior',
  component: Layout,
  redirect: '/userBehavior/index',
  name: 'userBehavior',
  alwaysShow: true,
  meta: {
    title: '用户分析',
    icon: 'userBehavior'
  },
  children: [
    {
      path: 'index',
      name: 'index',
      component: () => import('@/views/user-behavior/index'),
      meta: { title: '用户分析' }
    }
  ]
}

export default nestedRouter
