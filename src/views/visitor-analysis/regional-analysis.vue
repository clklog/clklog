<template>
  <div>
    <FilterBar @setFilterBarParams="setFilterBarParams" />
    <!-- 地域分析 -->
    <regionalView ref="regionalView"></regionalView>
    <regionalShow ref="regionalShow"></regionalShow>
    <regionalTable
      ref="regionalTable"
      @currentPage="currentPage"
    ></regionalTable>
  </div>
</template>

<script>
import { copyObj } from "@/utils/copy";
import { FilterBar } from "@/layout/components";
import regionalShow from "./chart-component/regional-analysis-show";
import regionalTable from "./chart-component/regional-analysis-table";
import regionalView from "./chart-component/regional-view";
import {
  getAreaDetailApi,
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
  },
  data() {
    return {
      filterBarParams: {},
      pageNum: 1,
      pageSize: 10,
    };
  },
  mounted() {
    // this.init()
  },
  computed: {
    project() {
      return this.$store.getters.project;
    },
    commonParams() {
      const { project, pageNum, pageSize } = this;
      return Object.assign({ project }, this.filterBarParams);
    },
  },
  watch: {
    commonParams(val) {
      this.getAreaDetail(val);
      this.getAreaProvinceList();
      this.getAreaDetailTop10();
    },
  },
  methods: {
    getAreaDetail(val) {
      getAreaDetailApi(val).then((res) => {
        if (res.code == 200) {
          this.$refs.regionalView.getDetailView(res.data);
        }
      });
    },
    getAreaProvinceList(val) {
      let newvalue = copyObj(this.commonParams);
      if (val) {
        newvalue.pageNum = val.page;
        newvalue.pageSize = val.size;
      } else {
        newvalue.pageNum = this.pageNum;
        newvalue.pageSize = this.pageSize;
      }

      this.$refs.regionalTable.getDetailList(newvalue);
    },
    getAreaDetailTop10() {
      getAreaDetailTop10Api(this.commonParams).then((res) => {
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

<style lang="scss" scoped></style>
