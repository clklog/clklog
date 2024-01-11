;(function (para) {
  var p = para.sdk_url,
    n = para.name,
    w = window,
    d = document,
    s = 'script',
    x = null,
    y = null
  if (typeof w['sensorsDataAnalytic201505'] !== 'undefined') {
    return false
  }
  w['sensorsDataAnalytic201505'] = n
  w[n] =
    w[n] ||
    function (a) {
      return function () {
        ;(w[n]._q = w[n]._q || []).push([a, arguments])
      }
    }
  var ifs = [
    'track',
    'quick',
    'register',
    'registerPage',
    'registerOnce',
    'trackSignup',
    'trackAbtest',
    'setProfile',
    'setOnceProfile',
    'appendProfile',
    'incrementProfile',
    'deleteProfile',
    'unsetProfile',
    'identify',
    'login',
    'logout',
    'trackLink',
    'clearAllRegister',
    'getAppStatus'
  ]
  for (var i = 0; i < ifs.length; i++) {
    w[n][ifs[i]] = w[n].call(null, ifs[i])
  }
  if (!w[n]._t) {
    ;(x = d.createElement(s)), (y = d.getElementsByTagName(s)[0])
    x.async = 1
    x.src = 'plugin/session-event/index.js' //引用的session-event插件路径
    x.setAttribute('charset', 'UTF-8')
    w[n].para = para
    y.parentNode.insertBefore(x, y)
    ;(x = d.createElement(s)), (y = d.getElementsByTagName(s)[0])
    x.async = 1
    x.src = p
    x.setAttribute('charset', 'UTF-8')
    w[n].para = para
    y.parentNode.insertBefore(x, y)
  }
  sensors.quick('isReady', function () {
    sensors.use('PageLeave', { heartbeat_interval_time: 5 })
    sensors.use('PageLoad')
    sensors.use('SessionEvent')
  })
  sensors.quick('autoTrackSinglePage')
})({
  sdk_url: 'sensorsdata.js',
  name: 'sensors',
  show_log: true,
  is_track_single_page: true,
  // send_type:'beacon',
  server_url: 'http://10.10.222.21/clklog_receiver/api/gp?project=clklogapp&token=gfdsg325432gfsgfds', //请修改接收地址为clklog_receiver 的接收服务地址，必须传入project和token参数
  heatmap: {
    clickmap: 'default',
    scroll_notice_map: 'default',
    collect_tags: {
      div: true,
      img: true
    }
  },
  preset_properties: { latest_referrer_host: true }
})