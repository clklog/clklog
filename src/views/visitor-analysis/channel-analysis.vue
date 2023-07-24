<template>
  <div>
    <FilterBar @setFilterBarParams="setFilterBarParams"></FilterBar>
    <channelTable></channelTable>
  </div>
</template>

<script>
import { FilterBar } from "@/layout/components";
import { getChannelDetailApi } from "@/api/trackingapi/channel";
import channelTable from "./chart-component/channel-analysis-table";
import { copyObj } from "@/utils/copy";
import { percentage } from "@/utils/percent";
export default {
  components: {
    FilterBar,
    channelTable
  },
  data() {
    return {
      filterBarParams: {},
      originData: null,
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
      this.getChannelDetail();
    },
  },
  methods:{
    getChannelDetail() {
      getChannelDetailApi(this.commonParams).then((res) => {
        if (res.code == 200) {
        console.log(res.data,234);
        }
      });
    },
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
  }
};
</script>

<style lang="scss" scoped></style>
