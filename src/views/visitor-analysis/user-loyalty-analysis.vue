<template>
  <div>
    <FilterBar @setFilterBarParams="setFilterBarParams"></FilterBar>
    <div class="userLoyalty public-hoverItem">
      <div class="public-firstHead loyaltyTitle">忠诚度分析</div>
      <visitNum ref="visitNum"></visitNum>
      <visitPage ref="visitPage"></visitPage>
      <visitTime ref="visitTime"></visitTime>
    </div>
  </div>
</template>

<script>
import { FilterBar } from "@/layout/components";
import {
  getUserVisitApi,
  getUserVisitTimeApi,
  getUserPvApi,
} from "@/api/trackingapi/uservisit";

import visitNum from "./chart-component/userLoyalty-echarts/visitNum";
import visitPage from "./chart-component/userLoyalty-echarts/visitPage";
import visitTime from "./chart-component/userLoyalty-echarts/visitTime";
import { copyObj } from "@/utils/copy";
export default {
  components: {
    FilterBar,
    visitNum,
    visitPage,
    visitTime,
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
    commonParams(val) {
      this.getUserVisit(); //频次
      this.getUserVisitTime(); //时长
      this.getUserPv(); //页数
    },
  },
  methods: {
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
    getUserVisit() {
      getUserVisitApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.visitNum.getVisitNum(res.data);
        }
      });
    },
    getUserVisitTime() {
      getUserVisitTimeApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.visitTime.getVisitTime(res.data);
        }
      });
    },
    getUserPv() {
      getUserPvApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.$refs.visitPage.getVisitPage(res.data);
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.userLoyalty {
  height: 1109px;
  margin: 20px;
  background: #fff;
  padding-top: 18px;
  padding-left: 16px;
  padding-bottom: 30px;
  // .loyaltyTitle {
  //   background-color: #fff;
  //   padding:20px;
  // }
}
</style>
