<template>
  <div>
    <FilterBar @setFilterBarParams="setFilterBarParams"  />
    <searchAnalysis ref="searchAnalysis"></searchAnalysis>
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
    };
  },
  computed: {
    project() {
      return this.$store.getters.project;
    },
    commonParams() {
      const { project } = this;
      let pageNum = 1;
      let pageSize = 50;
      return Object.assign(
        { project, pageNum, pageSize },
        this.filterBarParams
      );
    },
  },
  watch: {
    commonParams(val) {
      this.getFlowTrendDetail();
    },
  },
  methods: {
    getFlowTrendDetail() {
      getSearchWordDetailApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.searchAnalysis.searchTable(res.data);
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
.search_wrappy {
  margin: 15px;
  background-color: #fafafb;
  min-height: 647px;
}
</style>
