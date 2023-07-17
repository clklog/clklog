<template>
  <div>
    <div class="documentation-container">
      <FilterBar @setFilterBarParams="setFilterBarParams" />
    </div>
    <div class="Overview">
      <div class="trendAnalysis">趋势分析</div>
      <div class="bid-list-page">
        <div class="bid-list-header">
          <!-- <div class="header-name w156" /> -->
          <div class="header-name w156">
            浏览量(PV)
            <img src="@/assets/images/question.png" alt="" />
          </div>
          <div class="header-name w156">
            访问次数<img src="@/assets/images/question.png" alt="" />
          </div>
          <div class="header-name w156">
            访问数<img src="@/assets/images/question.png" alt="" />
          </div>
          <div class="header-name w156">
            IP数<img src="@/assets/images/question.png" alt="" />
          </div>
          <div class="header-name w156">
            平均访问页面<img src="@/assets/images/question.png" alt="" />
          </div>
          <div class="header-name w156">
            平均访问时长<img src="@/assets/images/question.png" alt="" />
          </div>
          <div class="header-name w156">
            跳出率<img src="@/assets/images/question.png" alt="" />
          </div>
        </div>
        <div style="max-height: 250px; overflow-y: auto">
          <div class="bid-list-record">
            <div class="bid-list-item w158">
              <p>87234</p>
            </div>
            <div class="bid-list-item w158">
              <p>23</p>
            </div>
            <div class="bid-list-item w158">
              <p>234</p>
            </div>
            <div class="bid-list-item w158">
              <p>23</p>
            </div>
            <div class="bid-list-item w158">
              <p>23.44</p>
            </div>
            <div class="bid-list-item w158">
              <p>23:54:12</p>
            </div>
            <div class="bid-list-item w158">
              <p>50.5%</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    <indicatorChart
      v-if="flowTrendList"
      :flowTrendListed="flowTrendList"
      ref="indicatorChart"
    />
    <trendTable />
  </div>
</template>

<script>
// import Tinymce from '@/components/Tinymce'
import { FilterBar } from "@/layout/components";
import indicatorChart from "./chart-component/IndicatorChart.vue";
import trendTable from "./chart-component/trend-table.vue";
import { getFlowTrendApi, getFlowTrendDetailApi } from "@/api/trackingapi/flow";
import Bus from "@/utils/bus";
import { copyObj } from "@/utils/copy";
export default {
  name: "TrendAnalysis",
  components: {
    FilterBar,
    indicatorChart,
    trendTable,
  },
  data() {
    return {
      filterBarValue: {},
      flowTrendList: null,
      filterBarParams: null,
    };
  },
  created() {
    this.filterBarValue = this.checkList;
    this.filterBarValue.projectName = this.$store.state.tracking.project;
  },
  computed: {
    checkList() {
      return this.$store.state.filterBar.checkList;
    },
    commonParams() {
      return Object.assign({}, this.filterBarParams);
    },
  },
  watch: {
    commonParams(val) {
      this.getFlowTrend();
      this.getFlowTrendDetail()
    },
  },
  methods: {
    getFlowTrendDetail() {
      getFlowTrendDetailApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          // this.flowTrendList = res.data;
          console.log(res.data,"详情数据");
        }
      });
    },
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
      console.log(this.filterBarParams)
    },
    getFlowTrend() {
      getFlowTrendApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.flowTrendList = res.data;
          // this.$refs.indicatorChart.changeChartValue(this.flowTrendList)
        }
      });
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
    font-size: 16px;
    font-weight: 400;
    // padding: 20px 30px 20px;
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
