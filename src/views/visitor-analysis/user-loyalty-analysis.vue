<template>
  <div>
    <FilterBar></FilterBar>
    <userLoyalty></userLoyalty>
  </div>
</template>

<script>
// import Sticky from '@/components/Sticky'
import { FilterBar } from "@/layout/components";
import userLoyalty from "./chart-component/user-loyalty-analysis";
export default {
  components: {
    FilterBar,
    userLoyalty
  },
};
</script>

<style lang="scss" scoped></style>
