<template>
  <div>
    <FilterBar @getFilterBar="getFilterBar" areaFlag = 0 />
    <searchAnalysis></searchAnalysis>
  </div>
</template>

<script>
// import MarkdownEditor from '@/components/MarkdownEditor'
import { FilterBar } from "@/layout/components";
import searchAnalysis from "./chart-component/search-analysis-table.vue";
export default {
  components: {
    FilterBar,
    searchAnalysis,
  },
  data() {
    return {

    };
  },
  methods: {
    getFilterBar(val){
      console.log(val,"filterBar1");
    }
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
