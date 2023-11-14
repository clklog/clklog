<template>
  <div>
    <FilterBar byChnnel @setFilterBarParams="setFilterBarParams"></FilterBar>
    <channelTable ref="channelTable" @currentPage="currentPage"></channelTable>
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
    channelTable,
  },
  data() {
    return {
      filterBarParams: {},
      originData: null,
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
      this.getChannelDetail();
    },
  },
  methods: {
    getChannelDetail() {
      getChannelDetailApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.channelTable.getChannelList(res.data);
        }
      });
    },
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
    currentPage() {
      this.getChannelDetail();
    },
  },
};
</script>

<style lang="scss" scoped></style>
