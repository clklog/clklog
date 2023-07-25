<template>
  <div>
    <div class="documentation-container">
      <FilterBar ByArea ByData @setFilterBarParams="setFilterBarParams" />
    </div>
    <div class="Overview">
      <div class="trendAnalysis">趋势分析</div>
      <originView ref="originView" byAreaAnaly></originView>
    </div>
    <indicatorChart ref="indicatorChart" />
    <trendTable ref="trendTable" />
  </div>
</template>

<script>
import originView from "@/components/origin-view";
import { FilterBar } from "@/layout/components";
import indicatorChart from "./chart-component/IndicatorChart.vue";
import trendTable from "./chart-component/trend-table.vue";
import { getFlowTrendDetailApi } from "@/api/trackingapi/flow";
import { copyObj } from "@/utils/copy";
import { percentage } from "@/utils/percent";
export default {
  name: "TrendAnalysis",
  components: {
    FilterBar,
    indicatorChart,
    trendTable,
    originView
  },
  data() {
    return {
      filterBarParams: {},
      originData: null,
    };
  },
  computed: {
    project() {
      return this.$store.getters.project;
    },
    commonParams() {
      const { project } = this;
      return Object.assign({ project }, this.filterBarParams);
    },
  },
  watch: {
    commonParams(val) {
      this.getFlowTrendDetail();
    },
  },
  methods: {
    percentageFun(val){
        return percentage(val)
    },
    getFlowTrendDetail() {
      getFlowTrendDetailApi(this.commonParams).then((res) => {
        if (res.code == 200) {
         
          this.originData = res.data;
          this.$refs.originView.originEvent(res.data.total)
          this.$refs.indicatorChart.apiDetailList(res.data);
          this.$refs.trendTable.apiDetailList(res.data);
         
        }
      });
    },
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
  },
};
</script>
<style lang="scss" scoped>
.Overview {
  margin: 20px;
  min-height: 118px;
  background-color: #fafafb;
  .trendAnalysis {
    font-size: 14px;
    font-weight: 500;
    color: #4d4d4d;
    padding: 14px 13px 18px 13px;
  }
  .bid-list-page {
    width: clas(100% -20px);
    display: flex;
    flex-direction: column;
    .bid-list-header {
      display: flex;
      img {
        width: 11.44px;
        height: 11.44px;
        margin-left: 12px;
        cursor: pointer;
      }
      .header-name {
        width: 14%;
        justify-content: center;
        height: 30px;
        display: flex;
        align-items: center;
        position: relative;
        .icon-question {
          width: 11.44px;
          height: 11.44px;
          margin-left: 12px;
          cursor: pointer;
          position: absolute;
          transform: translate(0, -50%);
        }
      }
      .w156 {
        font-size: 13px;
        color: #3f3e3e;
      }
    }
    .bid-list-record {
      display: flex;
      .bid-list-item {
        width: 14%;
        justify-content: center;
        height: 30px;
        display: flex;
        align-items: center;
      }
      .w157 {
        color: #252424;
        font-size: 14px;
        line-height: 14px;
      }
      .w158 {
        color: #3a98e6;
        font-size: 17px;
        line-height: 17px;
      }
    }
  }
}
</style>
