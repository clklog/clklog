<template>
  <div class="TrendChart block-main">
    <div class="block-head">
      <div class="block-title">趋势图</div>
      <!-- <span class="point">指标</span> -->
      <el-cascader
        v-model="emptyList"
        style="width: 170px; margin-left: 20px"
        placeholder="指标 | 选项"
        :options="options"
        :props="{ multiple: true, checkStrictly: true }"
        clearable
        collapse-tags
      ></el-cascader>
      <div
        class="block-head-icon"
        @click="$router.push('/visitorAnalysis/trendAnalysis')"
      >
        >
      </div>
    </div>
    <!-- echarts -->
    <chart id="charts1" height="350px" width="100%" />
  </div>
</template>

<script>
import Chart from "@/components/Charts/LineMarker";
export default {
  name: "TrendChart",
  components: { Chart },
  props: {
    commonParams: {},
  },
  data() {
    return {
      emptyList: "",
      options: [
        {
          value: 1,
          label: "浏览量",
        },
        {
          value: 2,
          label: "访客数",
        },
        {
          value: 3,
          label: "访问次数",
        },
        {
          value: 4,
          label: "IP数",
        },
        {
          value: 5,
          label: "跳出率",
        },
      ],
    };
  },
  computed: {
    params() {
      return Object.assign({}, this.commonParams);
    },
  },
  watch: {},
  methods: {},
};
</script>

<style lang="scss" scoped>
.TrendChart {
  .point {
    padding-right: 10px;
  }
}
</style>
