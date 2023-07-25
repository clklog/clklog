<template>
  <div>
    <FilterBar @setFilterBarParams="setFilterBarParams"  />
    <searchAnalysis  @currentPage="currentPage" ref="searchAnalysis"></searchAnalysis>
  </div>
</template>

<script>
import { FilterBar } from "@/layout/components";
import searchAnalysis from "./chart-component/search-analysis-table.vue";
import { copyObj } from "@/utils/copy";
import { getSearchWordDetailApi } from "@/api/trackingapi/searchword";
export default {
  components: {
    FilterBar,
    searchAnalysis,
  },
  data() {
    return {
      filterBarParams: "",
      pageNum: 1,
      pageSize: 10,
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
    getFlowTrendDetail(val) {
      let newvalue = copyObj(this.commonParams);
      if (val) {
        newvalue.pageNum = val.page;
        newvalue.pageSize = val.size;
      } else {
        newvalue.pageNum = this.pageNum;
        newvalue.pageSize = this.pageSize;
      }

      getSearchWordDetailApi(newvalue).then((res) => {
        if (res.code == 200) {
          this.$refs.searchAnalysis.searchTable(res.data);
        }
      });
    },
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
    currentPage(val) {
      this.getFlowTrendDetail(val);
    },
  },
};
</script>

<style lang="scss" scoped>
.search_wrappy {
  margin: 15px;
  background-color: #fafafb;
  min-height: 647px;
}
</style>
