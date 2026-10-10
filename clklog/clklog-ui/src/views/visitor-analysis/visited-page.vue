<template>
  <div>
    <FilterBar ByArea @setFilterBarParams="setFilterBarParams"></FilterBar>
    <div class="Overview public-hoverItem">
      <div class="public-firstHead">流量概览</div>
      <div class="bid-list-page">
        <originView ref="originView" byVisitedPage isUpdateDuration></originView>
      </div>
    </div>
    <visitedAnalysis
      @currentPage="currentPage"
      ref="visitedAnalysis"
    ></visitedAnalysis>
  </div>
</template>

<script>
import {
  getVisitUriDetailApi,
  getVisitUriTotalApi,
  getVisitUriPathTreeTotalApi,
} from "@/api/trackingapi/visituri";
import { FilterBar } from "@/layout/components";
import originView from "@/components/origin-view";
import visitedAnalysis from "./chart-component/visited-page-analysis";
import { copyObj } from "@/utils/copy";
export default {
  components: {
    FilterBar,
    visitedAnalysis,
    originView,
  },
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
    projectName() {
      return this.$store.getters.projectName;
    },
    commonParams() {
      const { projectName } = this;
      return Object.assign({ projectName }, this.filterBarParams);
    },
  },
  watch: {
    commonParams() {
      this.getVisitUriDetail(); // 默认数据
      this.getVisitUriTotal();
      this.getVisitUriPathTreeTotal();
    },
  },
  methods: {
    getVisitUriDetail(val) {
      let newvalue = copyObj(this.commonParams);
      this.pageNum = 1;
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
        this.$refs.visitedAnalysis && this.$refs.visitedAnalysis.initCurrentPage();
      }
      getVisitUriDetailApi(newvalue).then((res) => {
        if (res.code == 200) {
          this.$refs.visitedAnalysis &&
            this.$refs.visitedAnalysis.vistedAnalysis(res.data);
        }
      });
    },
    // 树状结构
    getVisitUriPathTreeTotal() {
      getVisitUriPathTreeTotalApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.visitedAnalysis &&
            this.$refs.visitedAnalysis.treeListEvent(
              res.data,
              this.commonParams
            );
        }
      });
    },
    getVisitUriTotal() {
      getVisitUriTotalApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.originView && this.$refs.originView.originEvent(res.data);
        }
      });
    },
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
    currentPage(val) {
      this.getVisitUriDetail(val);
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
    // .bid-list-header {
    //   display: flex;
    //   .header-name {
    //     width: 14%;
    //     justify-content: center;
    //     height: 30px;
    //     display: flex;
    //     align-items: center;
    //   }
    //   .w156 {
    //     font-size: 13px;
    //     font-weight: 400;
    //     line-height: 14px;
    //     color: #4d4d4d;
    //   }
    // }
  }
}
</style>