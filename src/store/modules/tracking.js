import { getLocalStorage, setLocalStorage } from "@/utils/localStorage";

const projectList = getLocalStorage("projectList");
const savedProjectCode = getLocalStorage("prejectCode");
const hasProjectList = Array.isArray(projectList) && projectList.length > 0;
// 缓存的项目名必须在项目列表中，否则取列表第一项
const validProjectCode =
  hasProjectList &&
  savedProjectCode &&
  projectList.some((item) => item.projectName === savedProjectCode)
    ? savedProjectCode
    : hasProjectList
      ? projectList[0].projectName
      : savedProjectCode || "clklogapp";

const state = {
  // store 状态存储器 页面刷新数据还在
  projectArray: projectList || [
    {
      projectName: "clklogapp",
      projectDisplayName: "clklog",
    },
  ],
  projectName: validProjectCode,
};

const mutations = {
  SET_PROJECT: (state, projectName) => {
    state.projectName = projectName;
    setLocalStorage("prejectCode", projectName);
  },
  SET_PROJECTARRAY: (state, value) => {
    state.projectArray = value;
    setLocalStorage("projectList", value);
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