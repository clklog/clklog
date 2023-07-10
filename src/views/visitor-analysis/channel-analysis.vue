<template>
  <div>
    <FilterBar></FilterBar>
    <channelTable></channelTable>
  </div>
</template>

<script>
import { FilterBar } from "@/layout/components";
import channelTable from "./chart-component/channel-analysis-table";
export default {
  components: {
    FilterBar,
    channelTable
  },
};
</script>

<style lang="scss" scoped></style>
