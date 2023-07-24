<template>
  <div class="SearchWord block-main">
    <div class="block-head">
      <div class="block-title">Top10搜索词</div>
      <div
        class="block-head-icon"
        @click="$router.push('/visitorAnalysis/searchAnalysis')"
      >
       <img src="@/assets/images/icon.png" alt="" width="10px"> 
      </div>
    </div>
    <div class="block-index-form">
      <!-- content -->
      <div class="form-list-page">
        <div class="form-list-header">
          <div class="header-text w156">搜索词</div>
          <div class="header-name w156"><b>浏览量(PV)</b></div>
          <div class="header-name w156"><b>占比</b></div>
        </div>

        <div
          class="form-list-record"
          v-for="(item, index) in searchWordList"
          :key="index"
        >
          <div class="header-text w157">{{ item.word || "--" }}</div>
          <div class="form-list-item w158">
            <p>{{ item.pv }}</p>
          </div>
          <div class="form-list-item w158">
            <p>{{ percentageFun(item.percent) }}</p>
          </div>
        </div>
        <!--
            <div class="form-list-record">
              <div class="header-text w157">百度统计</div>
              <div class="form-list-item w158">
                <p>187.7%</p>
              </div>
              <div class="form-list-item w158">
                <p>143.3%</p>
              </div>
            </div> -->
      </div>
    </div>
  </div>
</template>

<script>
import { getSearchWordApi } from "@/api/trackingapi/searchword";
import { percentage } from "@/utils/percent";
export default {
  name: "SearchWord",
  props: {
    commonParams: {},
  },
  data() {
    return {
      searchWordList: null,
    };
  },
  computed: {
    params() {
      return Object.assign({}, this.commonParams);
    },
  },
  watch: {},
  methods: {
    // 关键词搜索
    getSearchWord() {
      getSearchWordApi(this.params).then((res) => {
        if (res.code == 200) {
          this.searchWordList = res.data;
        }
      });
    },
    percentageFun(val){
        return percentage(val)
    }
  },
};
</script>

<style lang="scss" scoped>
</style>
