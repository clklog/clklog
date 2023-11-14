const state = {
  // projectName: '货清清'
  projectName: 'hqq'
}

const mutations = {
  SET_PROJECT: (state, projectName) => {
    state.projectName = projectName
  }
}

const actions = {
  setProject({ commit }, projectName) {
    commit('SET_PROJECT', projectName)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
