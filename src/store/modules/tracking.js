const state = {
  project: '货清清'
}

const mutations = {
  SET_PROJECT: (state, project) => {
    state.project = project
  }
}

const actions = {
  setProject({ commit }, project) {
    commit('SET_PROJECT', project)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
