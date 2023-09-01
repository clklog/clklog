<template>
  <div>
    <FilterBar @setFilterBarParams="setFilterBarParams"></FilterBar>
    <div class="Overview public-hoverItem">
      <div class="public-firstHead">流量概览</div>
      <div class="bid-list-page">
        <originView ref="originView" byVisit></originView>
      </div>
    </div>
    <newOldAnalysis ref="newOldAnalysis"></newOldAnalysis>
  </div>
</template>

<script>
import originView from "@/components/origin-view/index";
import { FilterBar } from "@/layout/components";
import newOldAnalysis from "./chart-component/newOld-visitor-analysisTable";
import { copyObj } from "@/utils/copy";
import {
  getVisitorTotalApi,
  getVisitorDetailApi,
} from "@/api/trackingapi/visitor";
export default {
  components: {
    FilterBar,
    newOldAnalysis,
    originView
  },
  data() {
    return {
      filterBarParams: null,
      pageNum: 1,
      pageSize: 10,
      visitorTotal:null,
    };
  },
  mounted() {},
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
      // this.getVisitorList();
      this.getVisitorDetail()
    },
  },

  methods: {
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
    getVisitorTotal() {
      getVisitorTotalApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.visitorTotal = res.data;
          this.$refs.originView.originEvent(res.data)
        }
      });
    },
    getVisitorDetail() {
      getVisitorDetailApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.newOldAnalysis.getVisitorDetail(res.data)
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
    font-size: 16px;
    font-weight: 600;
    line-height: 16px;
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
        color: #2c7be5;
        line-height: 17px;
      }
    }
  }
}
</style>
