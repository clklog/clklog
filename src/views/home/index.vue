<template>
  <div class="documentation-container">
    <TopFilter @setTopFilterParams="setTopFilterParams" />
    <!-- 流量概览 -->
    <FlowView ref="FlowViewRef" :commonParams="commonParams" />
    <div class="block-line">
      <TrendChart class="left" />
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
      });
    },
    setTopFilterParams(val) {
      this.topFilterParams = copyObj(val);
    },
  },
};
</script>
<style lang="scss" scoped>
.block-line {
  margin: 20px;
  width: calc(100% -20px);
  min-height: 300px;
  display: flex;
  justify-content: space-between;
  .left,
  .right {
    width: 49%;
  }
}
</style>
