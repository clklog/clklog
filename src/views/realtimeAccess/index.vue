<template>
  <div class="wrapItem">
    <FilterBar
      :ByCalendar="false"
      :ByTime="false"
      @setFilterBarParams="setFilterBarParams"
    ></FilterBar>
    <div class="public-block" style="margin-top: 0">
      <div class=" blockDiv setSpace">
        <div class=" block-main blockLeft public-hoverItem">
          <div class="block-head">
            <div class="block-title">访问量</div>
            <div class="block-head-icon">
              <!-- <img src="@/assets/images/icon.png" alt="" width="10px" /> -->
            </div>
          </div>
        </div>

        <div class="blockRight public-hoverItem">
            <div class="block-head">
            <div class="block-title">热门网页</div>
            <div class="block-head-icon">
              <!-- <img src="@/assets/images/icon.png" alt="" width="10px" /> -->
            </div>
          </div>
        </div>
      </div>
      <div class="localMap setSpace public-hoverItem"></div>
      <div class="visitLog public-hoverItem"></div>
    </div>
  </div>
</template>

<script>
import {
  getVisitUriDetailApi,
  getVisitUriTotalApi,
  getVisitUriPathTreeTotalApi,
} from "@/api/trackingapi/visituri";
import { FilterBar } from "@/layout/components";
import { copyObj } from "@/utils/copy";
import { percentage } from "@/utils/percent";
export default {
  components: {
    FilterBar,
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
      // this.getVisitUriDetail(); // 默认数据
    },
  },
  methods: {
    getVisitUriDetail(val) {},

    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
    currentPage(val) {
      this.getVisitUriDetail(val);
    },
  },
};
</script>

<style lang="scss" scoped>
.wrapItem {
  .setSpace {
    margin-bottom: 20px;
  }
  .blockDiv {
    box-sizing: border-box;
    min-height: 300px;
    display: flex;
    justify-content: space-between;
    .blockLeft {
      width: calc(50% - 10px);
      background-color: #fff;
      min-height: 502px;
      border-radius: 6px;
    }
    .blockRight {
      width: calc(50% - 10px);
      background-color: #fff;
      min-height: 502px;
      border-radius: 6px;
    }
  }
  .localMap {
    height: 514px;
    background-color: #fff;
    border-radius: 6px;
  }
  .visitLog {
    height: 585px;
    background-color: #fff;
    border-radius: 6px;
  }
}
</style>
