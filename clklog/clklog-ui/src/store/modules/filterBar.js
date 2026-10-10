// 暂时不使用
const state = {
  checkList: {
    channelList: ["网站", "安卓", "苹果"],
    visitorValue: "新访客",
    dateList: "day",
    areaList: ["上海", "北京"],
    projectName: "",
  },
};

const mutations = {
  SET_CHECKLIST: (state, checkList) => {
    state.checkList = checkList;
  },
};

const actions = {
  setCheckList({ commit }, checkList) {
    commit("SET_CHECKLIST", checkList);
  },
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
};
