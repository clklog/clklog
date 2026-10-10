import Cookies from 'js-cookie'

const USER_INFO_KEY = 'userInfo'

/**
 * 仅持久化用户名，用于登录页回填与界面展示。
 * 密码只在登录请求中使用，绝不写入 Cookie / localStorage / Vuex。
 * 兼容历史 Cookie（曾含 password 字段），读取时一律忽略。
 */
export function getRememberedUsername() {
  try {
    const raw = Cookies.get(USER_INFO_KEY)
    if (!raw) return ''
    const info = JSON.parse(raw)
    const username = info && info.username ? String(info.username) : ''
    // 历史 Cookie 可能含 password：读到后立即覆写为仅用户名
    if (info && Object.prototype.hasOwnProperty.call(info, 'password')) {
      setRememberedUsername(username)
    }
    return username
  } catch (e) {
    return ''
  }
}

export function setRememberedUsername(username) {
  Cookies.set(USER_INFO_KEY, JSON.stringify({
    username: username ? String(username).trim() : ''
  }))
}

export function clearRememberedUsername() {
  Cookies.remove(USER_INFO_KEY)
}
