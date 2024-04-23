; (function (para) {
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
        ; (w[n]._q = w[n]._q || []).push([a, arguments])
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
    w[n].para = para

    var scriptURLs = [
      'plugins/session-event/index.js', //引用的session-event插件路径
      p
    ];

    function loadScript(index) {
      if (index >= scriptURLs.length) {
        return false;
      }

      var x = d.createElement(s);
      y = d.getElementsByTagName(s)[0];
      x.async = 1
      x.src = scriptURLs[index];
      x.setAttribute('charset', 'UTF-8')
      x.onload = function () {
        console.log("Script loaded: ", scriptURLs[index]);
        loadScript(index + 1);
      }
      y.parentNode.insertBefore(x, y);
    }

    loadScript(0);
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
  show_log: false,
  is_track_single_page: true,
  send_type:'beacon',
  server_url: '', //请修改接收地址为接收服务地址
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
