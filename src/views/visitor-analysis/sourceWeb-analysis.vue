<template>
  <div>
    <FilterBar @setFilterBarParams="setFilterBarParams" ByArea></FilterBar>
    <div class="Overview public-hoverItem">
      <div class="public-firstHead">来源网站分析</div>
      <originView ref="originView" byAreaAnaly></originView>
    </div>
    <sourceWebChart ref="sourceWebChart"></sourceWebChart>
    <sourceWebTable
      ref="sourceWebTable"
      @currentPage="currentPage"
    ></sourceWebTable>
  </div>
</template>

<script>
import originView from "@/components/origin-view";
import { FilterBar } from "@/layout/components";
import sourceWebChart from "./chart-component/sourceWeb-analysis-chart";
import sourceWebTable from "./chart-component/sourceWeb-analysis-table";
import {
  getSourceWebSiteTotalApi,
  getSourceWebsiteApi,
  getSourceSiteDetailApi,
  getSourceWebSiteDetailTop10Api,
} from "@/api/trackingapi/sourcewebsite";
import { copyObj } from "@/utils/copy";
export default {
  components: {
    FilterBar,
    sourceWebChart,
    sourceWebTable,
    originView,
  },
  mounted() {},
  data() {
    return {
      filterBarParams: {},
      originData: null,
      pageNum: 1,
      pageSize: 10,
      sortName: null,
      sortOrder: null,
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
      this.getSourceSiteDetail();
      this.getSourceWebSiteTop10();
      this.getSourceWebSiteTotal();
    },
  },
  methods: {
    currentPage(val) {
      this.getSourceSiteDetail(val);
    },
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
    getSourceWebSiteTotal() {
      getSourceWebSiteTotalApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.originView.originEvent(res.data);
        }
      });
    },
    // table data
    getSourceSiteDetail(val) {
      let newvalue = copyObj(this.commonParams);
      this.pageNum= 1;
      if (val) {
        newvalue.pageNum = val.page;
        newvalue.pageSize = val.size;
        newvalue.sortName = val.sortName;
        newvalue.sortOrder = val.sortOrder;
        this.sortName = val.sortName;
        this.sortOrder = val.sortOrder;
        this.pageNum = val.page;
        this.pageSize = val.size;
      } else {
        newvalue.sortName = this.sortName;
        newvalue.sortOrder = this.sortOrder;
        newvalue.pageNum = this.pageNum;
        newvalue.pageSize = this.pageSize;
        this.$refs.sourceWebTable.initCurrentPage()
      }
      getSourceSiteDetailApi(newvalue).then((res) => {
        if (res.code == 200) {
          // this.$refs.originView.originEvent(res.data.summary);
          this.$refs.sourceWebTable.getSourceSite(res.data);
        }
      });
    },
    // echarts
    getSourceWebSiteTop10() {
      // getSourceWebSiteDetailTop10Api(this.commonParams).then((res) => {
      getSourceWebsiteApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.sourceWebChart.getSourceChart(res.data);
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
  background-color: #fff;
  img {
    width: 11.44px;
    height: 11.44px;
    margin-left: 12px;
    cursor: pointer;
  }
  .trafficHead {
    font-size: 14px;
    font-weight: 500;
    line-height: 14px;
    color: #4d4d4d;
    padding: 15px 30px 15px;
    padding-left: 0;
  }
  .bid-list-page {
    width: clas(100% -20px);
    display: flex;
    flex-direction: column;
    .bid-list-header {
      display: flex;
      .header-name {
        width: 14%;
        justify-content: center;
        height: 30px;
        display: flex;
        align-items: center;
      }
      .w156 {
        font-size: 13px;
        font-weight: 400;
        line-height: 14px;
        color: #4d4d4d;
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
        font-size: 12px;
        font-weight: 400;
        line-height: 14px;
        color: #4d4d4d;
      }
      .w158 {
        font-size: 16px;
        font-weight: bold;
        color: #3d64e6;
        line-height: 17px;
      }
    }
  }
}
</style>
