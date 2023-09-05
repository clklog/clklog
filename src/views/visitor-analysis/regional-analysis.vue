<template>
  <div>
    <FilterBar @setFilterBarParams="setFilterBarParams" />
    <div class="public-block">
      <div class="Overview public-hoverItem">
        <div class="public-firstHead">流量概览</div>
        <originView ref="originView" byAreaAnaly></originView>
      </div>
      <regionalShow ref="regionalShow"></regionalShow>
      <regionalTable
        ref="regionalTable"
        @currentPage="currentPage"
      ></regionalTable>
    </div>
  </div>
</template>

<script>
import { copyObj } from "@/utils/copy";
import { FilterBar } from "@/layout/components";
import regionalShow from "./chart-component/regional-analysis-show";
import regionalTable from "./chart-component/regional-analysis-table";
import regionalView from "./chart-component/regional-view";
import originView from "@/components/origin-view/index";
import {
  getAreaDetailTotalApi,
  getAreaApi,
  getAreaDetailTop10Api,
} from "@/api/trackingapi/area.js";
export default {
  name: "regionalAnalysis",
  components: {
    FilterBar,
    regionalView,
    regionalShow,
    regionalTable,
    originView,
  },
  data() {
    return {
      filterBarParams: {},
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
      this.getAreaDetailTotal(val);
      this.getAreaProvinceList();
      this.getArea();
    },
  },
  mounted() {},
  methods: {
    getAreaDetailTotal(val) {
      getAreaDetailTotalApi(val).then((res) => {
        if (res.code == 200) {
          this.$nextTick(() => {
            this.$refs.originView.originEvent(res.data);
          });
        }
      });
    },
    getAreaProvinceList(val) {
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
        this.$refs.regionalTable.initCurrentPage();
      }
      this.$refs.regionalTable.getDetailList(newvalue);
    },
    // getAreaDetailTop10() {
    //   getAreaDetailTop10Api(this.commonParams).then((res) => {
    //     if (res.code == 200) {
    //       // this.$refs.regionalShow.getAreaProvince(res.data);
    //     }
    //   });
    // },
    // map地图
    getArea() {
      getAreaApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.regionalShow.getAreaProvince(res.data);
        }
      });
    },
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
    currentPage(val) {
      this.getAreaProvinceList(val);
    },
  },
};
</script>

<style lang="scss" scoped>
.Overview {
  // margin: 20px;
  box-sizing: border-box;
  margin-bottom: 20px;
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
}
</style>
