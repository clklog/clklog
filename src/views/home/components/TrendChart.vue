<template>
  <div class="TrendChart block-main public-hoverItem">
    <div class="block-head"  @click="$router.push('/visitorAnalysis/trendAnalysis')">
      <div class="block-title">趋势图</div>
      <!-- collapse-tags -->
      <!-- <el-cascader
        v-model="headLege"
        style="width: 270px; margin-left: 20px"
        placeholder="指标 | 选项"
        :options="options"
        :props="{ multiple: true, checkStrictly: true }"
        clearable
        @change="handleCheckPointer"
      ></el-cascader> -->

      <!-- 调整功能 -->
      <el-select
        class="custom_select"
        v-model="headLege"
        multiple
        placeholder="请选择"
        style="margin-left: 20px; min-width: 280px"
        @change="handleCheckPointer"
      >
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
          :disabled="disabledSelect.includes(item.value)"
        >
          <div style="display: flex; align-items: center">
            <span class="checkbox__inner"><div class="inner-box"></div></span>
            <span style="">{{ item.label }}</span>
          </div>
        </el-option>
      </el-select>

      <div
        class="block-head-icon"
       
      >
       <img src="@/assets/images/icon.png" alt="" width="10px">
      </div>
    </div>
    <chart
      id="charts1"
      height="350px"
      width="100%"
      :flowTrendListed="flowTrendList"
      :defaultLege="headLege"
      ref="trendChartRef"
      style="padding:20px"
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
          disabled: false,
        },
        {
          value: "访客数",
          label: "访客数",
          disabled: false,
        },
        {
          value: "访问次数",
          label: "访问次数",
          disabled: false,
        },
        {
          value: "IP数",
          label: "IP数",
          disabled: false,
        },
        {
          value: "跳出率",
          label: "跳出率",
          disabled: false,
        },
      ],
      flowTrendList: null,
      flag: false,
      specificTTime: "time",
      headLege: ["IP数","访问次数",],
      disabledSelect: [],
    };
  },
  created() {},
  computed: {
    params() {
      return Object.assign({}, this.commonParams);
    },
  },
  watch: {
    specificTTime(val) {
      this.getFlowTrend();
    },
  },
  methods: {
    getFlowTrend() {
      getFlowTrendApi(this.params).then((res) => {
        if (res.code == 200) {
          this.flowTrendList = res.data;
          this.flag = true;
          this.$refs.trendChartRef.getApiTrendList(res.data,this.headLege);
        }
      });
    },
    handleCheckPointer(e) {
      let result = [];
      for (let i = 0; i < this.options.length; i++) {
        result.push(this.options[i].label);
        if (e.length > 2) {
          this.disabledSelect = result.filter((item) => {
            return e.indexOf(item) == -1;
          });
        } else {
          this.disabledSelect = [];
        }
      }
      this.headLege = e.flat(Infinity);
      this.$refs.trendChartRef.changeLegend(this.headLege);
    },
  },
};
</script>
<style lang="scss" scoped>
@import "~@/styles/components/custom-select.scss";
::v-deep {
  .el-radio-button--mini .el-radio-button__inner {
    height: 30px;
  }
}
.TrendChart {
  .block-head {
    display: flex;
    align-items: center;
  }
  .point {
    padding-right: 10px;
  }
  // .custom_select
  //     .el-select-dropdown.is-multiple
  //     .el-select-dropdown__item.selected::after {
  //     display: none !important;
  //   }
}
</style>
