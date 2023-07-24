<template>
  <div>
    <FilterBar @setFilterBarParams="setFilterBarParams"></FilterBar>
    <equipmentTable ref="equipmentTable"></equipmentTable>
  </div>
</template>

<script>
import { FilterBar } from "@/layout/components";
import { getDeviceDetailApi } from "@/api/trackingapi/device";
import equipmentTable from "./chart-component/equipment-analysis-table";
import { copyObj } from "@/utils/copy";
import { percentage } from "@/utils/percent";
export default {
  components: {
    FilterBar,
    equipmentTable,
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
      this.getDeviceDetail();
    },
  },
  methods: {
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
    getDeviceDetail() {
      getDeviceDetailApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.equipmentTable.equipmentTableEvent(res.data)
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped></style>
