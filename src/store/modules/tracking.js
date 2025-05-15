import { getLocalStorage } from "@/utils/localStorage";

const state = {
  // store 状态存储器 页面刷新数据还在
  projectArray: getLocalStorage("projectList") || [
    {
      projectName: "clklogapp",
      projectDisplayName: "clklog",
    },
  ],
  projectName:
    getLocalStorage("projectList") &&
      getLocalStorage("projectList").length > 0
      ? getLocalStorage("projectList")[0].projectName
      : "clklogapp",
};

const mutations = {
  SET_PROJECT: (state, projectName) => {
    state.projectName = projectName;
  },
  SET_PROJECTARRAY: (state, value) => {
    state.projectArray = value;
  },
};

const actions = {
  setProject({ commit }, projectName) {
    commit("SET_PROJECT", projectName);
  },
  setProjectArray({ commit }, value) {
    commit("SET_PROJECTARRAY", value);
  },
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
};
