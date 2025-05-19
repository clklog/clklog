<template>
  <div>
    <customBar
      ByDayOrHour
      ByCollapse
      @setFilterBarParams="setFilterBarParams"
    ></customBar>
    <div class="public-block">
      <div class="public-hoverItem">
        <div
          class="public-firstHead"
          style="
            height: 151px;
            background-color: #fff;
            margin-top: 16px;
            border-radius: 6px;
          "
        >
          <div class="public-firstHead" style="padding: 15px 15px 0 15px">
            流量概览
          </div>
          <trafficOverview
            v-loading="loadingRefresh"
            ref="trafficOverview"
          ></trafficOverview>
        </div>
      </div>
    </div>
    <div class="public-block">
      <collapseChart ref="collapseChart"></collapseChart>
    </div>
    <div>
      <collapseTable ref="collapseTable"></collapseTable>
    </div>
  </div>
</template>

<script>
import { copyObj } from "@/utils/copy";
import { totalSummaryApi } from "@/api/trackingapi/collapse";
import customBar from "@/components/customBar/index.vue";
import collapseChart from "./components/collapse-chart.vue";
import collapseTable from "./components/collapse-table.vue";
import trafficOverview from "./components/trafficOverview.vue";

export default {
  name: "funnelAnalysis",
  components: {
    customBar,
    collapseTable,
    collapseChart,
    trafficOverview,
  },
  data() {
    return {
      filterBarParams: "",
      loadingRefresh: false,
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
    commonParams: {
      deep: false,
      immediate: false,
      handler(newVal, oldVal) {
        if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
          delete newVal.timeFlag;
          delete newVal.groupName;
          this.getList(newVal);
          this.$refs.collapseChart.paramsEvent(newVal);
          this.$refs.collapseTable.paramsEvent(newVal);
        }
      },
    },
  },
  methods: {
    getList: function (newVal) {
      let params = copyObj(newVal);
      params.channel = [];
      this.loadingRefresh = true;
      totalSummaryApi(params)
        .then((res) => {
          if (res.code == 200) {
            this.loadingRefresh = false;
            this.$refs.trafficOverview.dataListEvent(res.data);
          }
        })
        .catch(() => {
          this.loadingRefresh = false;
        });
    },
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
  },
};
</script>

<style lang="scss" scoped></style>
