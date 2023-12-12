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
    x.src = 'assets/plugins/session-event/index.js' //引用的session-event插件路径
    x.setAttribute('charset', 'UTF-8')
    w[n].para = para
    y.parentNode.insertBefore(x, y)
    ;(x = d.createElement(s)), (y = d.getElementsByTagName(s)[0])
    x.async = 1
    x.src = 'assets/plugins/exposure/index.js' //引用的exposure插件路径
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
    sensors.use('Exposure', {
      area_rate: 1,
      stay_duration: 2,
      repeated: false
    })
    sensors.use('PageLeave', { heartbeat_interval_time: 5 })
    sensors.use('PageLoad')
    sensors.use('SessionEvent')
  })
  //sensors.quick('autoTrackSinglePage')
})({
  sdk_url: 'assets/plugins/sensorsdata.js',
  name: 'sensors',
  show_log: true,
  is_track_single_page: false,
  // send_type:'beacon',
  server_url: 'https://receiver.tracking.zcunsoft.com/api/gp?project=clklog&token=4a793ea028fc327f4f1763b7630e1438', //clklog_receiver 的接收服务地址
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
