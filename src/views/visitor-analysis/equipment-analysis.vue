<template>
  <div>
    <FilterBar></FilterBar>
    <equipmentTable></equipmentTable>
  </div>
</template>

<script>
import { FilterBar } from "@/layout/components";
import equipmentTable from "./chart-component/equipment-analysis-table";
export default {
  components: {
    FilterBar,
    equipmentTable
  },
};
</script>

<style lang="scss" scoped></style>
