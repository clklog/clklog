import Layout from '@/layout'

const tableRouter = {
  path: '/behaviorAnalysis',
  component: Layout,
  redirect: '/behaviorAnalysis/user-behavior-analysis',
  name: 'behaviorAnalysis',
  alwaysShow: true,
  meta: {
    title: '行为分析',
    icon: 'behaviorAnaly'
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
