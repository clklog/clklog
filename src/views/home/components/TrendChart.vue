<template>
  <div class="TrendChart block-main">
    <div class="block-head">
      <div class="block-title">趋势图</div>
      <el-cascader
        v-model="headLege"
        style="width: 170px; margin-left: 20px"
        placeholder="指标 | 选项"
        :options="options"
        :props="{ multiple: true, checkStrictly: true }"
        clearable
        collapse-tags
        @change="handleCheckPointer"
      ></el-cascader>
      <div style="margin-left: 20px">
        <el-radio-group
          size="small"
          v-model="specificTTime"
          style="font-size: 12px"
        >
          <el-radio-button label="time">按时</el-radio-button>
          <el-radio-button label="day">按日</el-radio-button>
          <el-radio-button label="week">按周</el-radio-button>
          <el-radio-button label="month">按月</el-radio-button>
        </el-radio-group>
      </div>
      <div
        class="block-head-icon"
        @click="$router.push('/visitorAnalysis/trendAnalysis')"
      >
        >
      </div>
    </div>
    <!-- echarts -->
    <chart
      v-if="flag"
      id="charts1"
      height="350px"
      width="100%"
      :flowTrendListed="flowTrendList"
      :defaultLege  = "headLege"
      ref = "trendChartRef"
    />
  </div>
</template>

<script>
import Chart from "@/components/Charts/LineMarker";
import { getFlowTrendApi } from "@/api/trackingapi/flow";
export default {
  name: "TrendChart",
  components: { Chart },
  props: {
    commonParams: {},
  },
  data() {
    return {
      
      options: [
        {
          value: "浏览量",
          label: "浏览量",
        },
        {
          value:"访客数",
          label: "访客数",
        },
        {
          value: "访问次数",
          label: "访问次数",
        },
        {
          value: "IP数",
          label: "IP数",
        },
        {
          value: "跳出率",
          label: "跳出率",
        },
      ],
      flowTrendList: null,
      flag: false,
      specificTTime: "time",
      headLege:["浏览量", "访问次数"],
    };
  },
  created() {},
  computed: {
    params() {
      return Object.assign({}, this.commonParams);
    },
  },
  watch: {
    specificTTime(val){
      this.getFlowTrend()
    }
  },
  methods: {
    getFlowTrend() {
      getFlowTrendApi(this.params).then((res) => {
        if (res.code == 200) {
          this.flowTrendList = res.data;
          this.flag = true;
        }
      });
    },
    handleCheckPointer(e){
      this.headLege = e.flat(Infinity);
      this.$refs.trendChartRef.changeLegend(this.headLege)
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep .el-radio-button--mini .el-radio-button__inner {
  height: 30px;
}
.TrendChart {
  .block-head {
    display: flex;
    align-items: center;
  }
  .point {
    padding-right: 10px;
  }
}
</style>
