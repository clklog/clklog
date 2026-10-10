<template>
  <div>
    <FilterBar @setFilterBarParams="setFilterBarParams"></FilterBar>
    <equipmentTable
      ref="equipmentTable"
      @currentPage="currentPage"
    ></equipmentTable>
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
      pageNum: 1,
      pageSize: 10,
      sortName: null,
      sortOrder: null,
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
      this.getDeviceDetail();
    },
  },
  methods: {
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
    getDeviceDetail(val) {
      this.pageNum = 1;
      let newvalue = copyObj(this.commonParams);
      if (val) {
        newvalue.pageNum = val.page;
        newvalue.pageSize = val.size;
        this.pageNum = val.page;
        this.pageSize = val.size;
        newvalue.sortName = val.sortName;
        newvalue.sortOrder = val.sortOrder;
        this.sortName = val.sortName;
        this.sortOrder = val.sortOrder;
      } else {
        newvalue.sortName = this.sortName;
        newvalue.sortOrder = this.sortOrder;
        newvalue.pageNum = this.pageNum;
        newvalue.pageSize = this.pageSize;
        this.$refs.equipmentTable.initCurrentPage()
      }
      getDeviceDetailApi(newvalue).then((res) => {
        if (res.code == 200) {
          this.$refs.equipmentTable.equipmentTableEvent(res.data);
        }
      });
    },
    currentPage(val) {
      this.getDeviceDetail(val);
    },
  },
};
</script>

<style lang="scss" scoped></style>
