import { login, logout, getInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { removeLocalStorage } from '@/utils/localStorage'
import router, { resetRouter } from '@/router'

const state = {
  token: getToken(),
  name: '',
  avatar: '',
  introduction: '',
  roles: [],
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_INTRODUCTION: (state, introduction) => {
    state.introduction = introduction
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  }
}

const actions = {
  // user login
  login({ commit }, response) {
    return new Promise((resolve, reject) => {
      const { data } = response;
      commit("SET_TOKEN", data.token);
      setToken(data.token);
      resolve();
    });
  },
  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo(state.token).then(response => {
        const { data } = response

        if (!data) {
          reject('Verification failed, please Login again.')
        }

        commit('SET_ROLES', ['admin'])
        commit('SET_NAME', '')
        commit('SET_AVATAR', '')
        commit('SET_INTRODUCTION', '')


        resolve(['admin'])
      }).catch(error => {
        reject(error)
      })
    })
  },

  // user logout
  logout({ commit, state, dispatch }) {
    return new Promise((resolve) => {
      const clearLoginData = () => {
        commit('SET_TOKEN', '')
        commit('SET_ROLES', [])
        removeToken()
        // 只清项目相关缓存，不影响用户名 Cookie、token Cookie、其他本地配置
        removeLocalStorage('prejectCode')
        removeLocalStorage('projectList')
        commit('tracking/SET_PROJECT', '', { root: true })
        commit('tracking/SET_PROJECTARRAY', [], { root: true })
        // Navbar watch 可能异步回写默认项目，下一拍再清一次
        Promise.resolve().then(() => {
          removeLocalStorage('prejectCode')
          removeLocalStorage('projectList')
        })
        resetRouter()
        dispatch('tagsView/delAllViews', null, { root: true })
      }

      logout(state.token)
        .then(() => {
          clearLoginData()
          resolve()
        })
        .catch(() => {
          // 退出接口失败也要清本地登录态和项目缓存
          clearLoginData()
          resolve()
        })
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      commit('SET_ROLES', [])
      removeToken()
      removeLocalStorage('prejectCode')
      removeLocalStorage('projectList')
      commit('tracking/SET_PROJECT', '', { root: true })
      commit('tracking/SET_PROJECTARRAY', [], { root: true })
      Promise.resolve().then(() => {
        removeLocalStorage('prejectCode')
        removeLocalStorage('projectList')
      })
      resolve()
    })
  },

  // dynamically modify permissions
  async changeRoles({ commit, dispatch }, role) {
    const token = role + '-token'

    commit('SET_TOKEN', token)
    setToken(token)

    const { roles } = await dispatch('getInfo')

    resetRouter()

    // generate accessible routes map based on roles
    const accessRoutes = await dispatch('permission/generateRoutes', roles, { root: true })
    // dynamically add accessible routes
    router.addRoutes(accessRoutes)

    // reset visited views and cached views
    dispatch('tagsView/delAllViews', null, { root: true })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
