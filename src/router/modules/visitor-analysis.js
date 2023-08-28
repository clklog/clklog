import Layout from '@/layout'

const componentsRouter = {
  path: '/visitorAnalysis',
  component: Layout,
  redirect: 'noRedirect',
  name: 'visitorAnalysis',
  meta: {
    title: '访客分析',
    icon: 'visitorAnaly'
  },
  children: [
    {
      path: 'trendAnalysis',
      component: () => import('@/views/visitor-analysis/trend-analysis'),
      name: 'trendAnalysis',
      meta: { title: '趋势分析' }
    },
    {
      path: 'searchAnalysis',
      component: () => import('@/views/visitor-analysis/search-analysis'),
      name: 'searchAnalysis',
      meta: { title: '站外搜索' }
    },
    {
      path: 'regionalAnalysis',
      component: () => import('@/views/visitor-analysis/regional-analysis'),
      name: 'regionalAnalysis',
      meta: { title: '地域分析' }
    },
    {
      path: 'visitedPage',
      component: () => import('@/views/visitor-analysis/visited-page'),
      name: 'visitedPage',
      meta: { title: '受访页面' }
    },
    {
      path: 'sourceWebAnalysis',
      component: () => import('@/views/visitor-analysis/sourceWeb-analysis'),
      name: 'sourceWebAnalysis',
      meta: { title: '来源网站' }
    },
    {
      path: 'newOld-visitor-analysis',
      component: () => import('@/views/visitor-analysis/newOld-visitor-analysis'),
      name: 'newOld-visitor-analysis',
      meta: { title: '新老访客' }
    },
    {
      path: 'userLoyaltyAnalysis',
      component: () => import('@/views/visitor-analysis/user-loyalty-analysis'),
      name: 'userLoyaltyAnalysis',
      meta: { title: '用户忠诚度' }
    },
    {
      path: 'equipment-analysis',
      component: () => import('@/views/visitor-analysis/equipment-analysis'),
      name: 'equipment-analysis',
      meta: { title: '设备' }
    },
    {
      path: 'channel-analysis',
      component: () => import('@/views/visitor-analysis/channel-analysis'),
      name: 'channel-analysis',
      meta: { title: '渠道' }
    }
  ]
}

export default componentsRouter
