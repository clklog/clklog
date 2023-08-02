<template>
  <div class="documentation-container">
   
    <div style="height: 51px;">
      <TopFilter @setTopFilterParams="setTopFilterParams" @timeTypeCheck = "timeTypeCheck"/>
    </div>
    <div class="public-block">
      <FlowView ref="FlowViewRef" :commonParams="commonParams" :timeTypeFlag = "timeTypeFlag"/>
      <div class="block-line">
        <TrendChart ref="TrendChart" :commonParams="commonParams" class="left" />
        <NewOldVisitors ref="NewOldVisitorsRef" :commonParams="commonParams" class="right" />
      </div>
      <div class="block-line">
        <PageView ref="PageViewRef" class="left" :commonParams="commonParams" />
        <SearchWord
          ref="SearchWordRef"
          class="right"
          :commonParams="commonParams"
        />
      </div>
      <div class="block-line">
        <SourceWebsite
          ref="SourceWebsiteRef"
          class="left"
          :commonParams="commonParams"
        />
        <LocalDistribution ref="LocalDistributionRef" :commonParams="commonParams" class="right" />
      </div>
    </div>
   
  </div>
</template>

<script>
import {
  TopFilter,
  FlowView,
  TrendChart,
  NewOldVisitors,
  PageView,
  SearchWord,
  SourceWebsite,
  LocalDistribution,
} from "./components/index";
import { copyObj } from "@/utils/copy";
export default {
  name: "Home",
  components: {
    TopFilter,
    FlowView,
    TrendChart,
    NewOldVisitors,
    PageView,
    SearchWord,
    SourceWebsite,
    LocalDistribution,
  },
  data() {
    return {
      topFilterParams: {},
      timeTypeFlag:"",
    };
  },
  computed: {
    project() {
      return this.$store.getters.project;
    },
    commonParams() {
      const { project } = this;
      return Object.assign({ project }, this.topFilterParams);
    },
  },
  watch: {
    commonParams(val) {
      this.getIndexData();
    },
  },
  methods: {
    getIndexData() {
      this.$nextTick(() => {
        this.$refs.FlowViewRef.getFlow();
        this.$refs.PageViewRef.getVisitUri();
        this.$refs.SearchWordRef.getSearchWord();
        this.$refs.SourceWebsiteRef.getSourceWebsite();
        this.$refs.NewOldVisitorsRef.getVisitor();
        this.$refs.LocalDistributionRef.getArea();
        this.$refs.TrendChart.getFlowTrend();
      });
    },
    timeTypeCheck(val){
      this.timeTypeFlag = val;
    },
    setTopFilterParams(val) {
      this.topFilterParams = copyObj(val);
    },
  },
};
</script>
<style lang="scss" scoped>
// .public-block{
//   margin: 20px;
// }
.block-line {
  margin: 20px 0 20px 0;
  width: calc(100% -20px);
  min-height: 300px;
  display: flex;
  justify-content: space-between;
  .left,
  .right {
    width: 49.5%;
  }
}
</style>
