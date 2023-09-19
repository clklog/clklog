<template>
  <div>
    <div class="documentation-container">
      <FilterBar
        ByArea
        ByData
        bySub
        @setFilterBarParams="setFilterBarParams"
      />
    </div>
    <div class="public-block">
      <div class="Overview public-hoverItem">
        <div class="trendAnalysis public-firstHead">流量概览</div>
        <originView ref="originView" byAreaAnaly></originView>
      </div>
      <indicatorChart ref="indicatorChart" />
      <trendTable @currentPage="currentPage" ref="trendTable" />
    </div>
  </div>
</template>

<script>
import originView from "@/components/origin-view";
import { FilterBar } from "@/layout/components";
import indicatorChart from "./chart-component/IndicatorChart.vue";
import trendTable from "./chart-component/trend-table.vue";
import {
  getFlowTotalApi,
  getFlowTrendApi,
  getFlowDetailApi,
  exportFlowTrendDetailApi,
  getFlowTrendDetailApi,
} from "@/api/trackingapi/flow";
import { copyObj } from "@/utils/copy";
import { percentage } from "@/utils/percent";
export default {
  name: "TrendAnalysis",
  components: {
    FilterBar,
    indicatorChart,
    trendTable,
    originView,
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
      this.getFlowTotalView();
      this.getFlowTrendDetail(val);
      // this.getFlowEcharts();
    },
  },
  methods: {
    percentageFun(val) {
      return percentage(val);
    },
    getFlowTotalView() {
      getFlowTotalApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.originView.originEvent(res.data);
        }
      });
    },
    getFlowTrendDetail(noEchart) {
        console.log(noEchart,"noEchart");
      getFlowDetailApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          if (noEchart) {
            this.$refs.trendTable.apiDetailList(JSON.parse(JSON.stringify(res.data)),this.commonParams.timeType);
            this.$refs.indicatorChart.apiDetailList(res.data,)
          }else{
            this.$refs.trendTable.apiDetailList(JSON.parse(JSON.stringify(res.data)),this.commonParams.timeType);
          }
        }
      });
    },
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
    currentPage() {
      this.getFlowTrendDetail();
    },
  },
};
</script>
<style lang="scss" scoped>
.bid-list-page {
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
</style>
