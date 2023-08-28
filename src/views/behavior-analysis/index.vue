<template>
  <div>
    <FilterBar @setFilterBarParams="setFilterBarParams" ByArea></FilterBar>
    <div class="Overview">
      <div class="public-firstHead">用户概览</div>
      <div class="bid-list-page">
        <originView ref="originView" byVisit></originView>
      </div>
    </div>
    <behaviorChart ref="behaviorChart" @currentPage="currentPage"></behaviorChart>
  </div>
</template>

<script>
import { FilterBar } from "@/layout/components";
import behaviorChart from "./components/behavior-analysis-charts";
import { copyObj } from "@/utils/copy";
import {
  getVisitorTotalApi,
  getVisitorChannelApi,
  getVisitorListApi,
} from "@/api/trackingapi/visitor.js";
import originView from "@/components/origin-view/index";
export default {
  components: {
    FilterBar,
    behaviorChart,
    originView,
  },
  data() {
    return {
      filterBarParams: null,
      pageNum: 1,
      pageSize: 10,
      visitorTotal: null,
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
    commonParams() {
      this.getVisitorTotal();
      this.getVisitorChannel();
      this.getVisitorList();
    },
  },
  mounted() {},
  methods: {
    currentPage(val) {
      this.getVisitorList(val);
    },
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
    getVisitorTotal() {
      getVisitorTotalApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          // this.visitorTotal = res.data;
          this.$refs.originView.originEvent(res.data);
        }
      });
    },
    getVisitorChannel() {
      getVisitorChannelApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.behaviorChart.getChannelList(res.data);
        }
      });
    },
    getVisitorList(val) {
      let newvalue = copyObj(this.commonParams);
      this.pageNum= 1;
      newvalue.pageNum = this.pageNum;
      newvalue.pageSize = this.pageSize;
      if (val) {
        newvalue.pageNum = val.page;
        newvalue.pageSize = val.size;
        this.pageNum = val.page;
        this.pageSize = val.size;
        newvalue.sortName = val.sortName;
        newvalue.sortOrder = val.sortOrder;
        this.sortName = val.sortName;
        this.sortOrder = val.sortOrder;
      } else {
        newvalue.sortName = this.sortName;
        newvalue.sortOrder = this.sortOrder;
        newvalue.pageNum = this.pageNum;
        newvalue.pageSize = this.pageSize;
        // newvalue.sortName = "pv";
        // newvalue.country=["全球"]
        newvalue.sortOrder = "desc";
        this.$refs.behaviorChart.initCurrentPage()
        // newvalue.timeType = "day";
        // newvalue["province"] = newvalue["area"];
        // delete newvalue["area"];
      }

      getVisitorListApi(newvalue).then((res) => {
        if (res.code == 200) {
          this.$refs.behaviorChart.getUserListEvent(res.data);
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
  border-radius: 6px;
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
