/** When your routing table is too long, you can split it into small modules **/

import Layout from '@/layout'

const tableRouter = {
  path: '/behaviorAnalysis',
  component: Layout,
  redirect: '/behaviorAnalysis/user-behavior-analysis',
  name: 'behaviorAnalysis',
  alwaysShow: true,
  meta: {
    title: '行为分析',
    icon: 'table'
  },
  children: [
    {
      path: 'user-behavior-analysis',
      component: () => import('@/views/behavior-analysis/index'),
      name: 'user-behavior-analysis',
      meta: { title: '用户行为分析' }
    }
  ]
}
export default tableRouter
