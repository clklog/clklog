<template>
  <div class="area_container">
    <div class="mapCharts">
      <span>TOP10访问区域指标展示</span>
      <div
        id="echart_china"
        ref="echart_china"
        style="width: 100%; height: 350px"
      />
    </div>
    <!-- style="width: 100%; height: 287px;overflow-y: auto;" -->
    <div class="mapTable">
      <div style="width: 500px; height: 100%; margin-top: 62px">
        <el-table
          ref="singleTable"
          :data="apiProvinceList"
          highlight-current-row
          style="width: 100%;"
        >
          <el-table-column type="index" width="100"> </el-table-column>
          <el-table-column property="province" label="省份" width="120">
          </el-table-column>
          <el-table-column property="visitCount" label="访问次数" width="120">
          </el-table-column>
          <el-table-column
            property="visitCountRate"
            label="占比"
          ></el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import chinamap from "echarts/map/json/china.json";
import resize from "@/components/Charts/mixins/resize";
import { getAreaApi } from "@/api/trackingapi/area";
import { percentage } from "@/utils/percent";
export default {
  mixins: [resize],
  data() {
    return {
      getAreaList: null,
      myChart: null,
      provinceList: [
        { name: "上海", value: 0 },
        { name: "广东", value: 0 },
        { name: "北京", value: 0 },
        { name: "天津", value: 0 },
        { name: "重庆", value: 0 },
        { name: "河北", value: 0 },
        { name: "河南", value: 0 },
        { name: "云南", value: 0 },
        { name: "辽宁", value: 0 },
        { name: "黑龙江", value: 0 },
        { name: "湖南", value: 0 },
        { name: "安徽", value: 0 },
        { name: "山东", value: 0 },
        { name: "新疆", value: 0 },
        { name: "江苏", value: 0 },
        { name: "浙江", value: 0 },
        { name: "江西", value: 0 },
        { name: "湖北", value: 0 },
        { name: "广西", value: 0 },
        { name: "甘肃", value: 0 },
        { name: "山西", value: 0 },
        { name: "内蒙古", value: 0 },
        { name: "陕西", value: 0 },
        { name: "吉林", value: 0 },
        { name: "福建", value: 0 },
        { name: "贵州", value: 0 },
        { name: "青海", value: 0 },
        { name: "西藏", value: 0 },
        { name: "四川", value: 0 },
        { name: "宁夏", value: 0 },
        { name: "海南", value: 0 },
        { name: "台湾", value: 0 },
        { name: "香港", value: 0 },
        { name: "澳门", value: 0 },
      ],
      apiProvinceList: [],
      maxValue: 200,
    };
  },
  mounted() {},
  methods: {
    getAreaProvince(val) {
      let maxValue = [];
      // this.apiProvinceList = val.rows;
      this.apiProvinceList = val;
      for (let i = 0; i < this.provinceList.length; i++) {
        for (let j = 0; j < this.apiProvinceList.length; j++) {
          if (this.provinceList[i].name == this.apiProvinceList[j].province) {
            this.provinceList[i].value = this.apiProvinceList[j].visitCount;
            maxValue.push(this.apiProvinceList[j].visitCount);
          }
        }
      }
      if (maxValue.length > 0) {
        let max = maxValue.sort(function (a, b) {
          return b - a;
        })[0];
        this.maxValue = max;
      }else{
        this.provinceList.map(item =>{
          item.value = 0
        })
      }
      this.apiProvinceList.map((item) => {
        item.visitCountRate = percentage(item.visitCountRate);
      })
      this.showScatterInGeo();
    },
    randomData() {
      return Math.round(Math.random() * 500);
    },
    resizeMyChartContainer() {
      this.myChart.height = 100 + "px"; // 页面一半的大小
      this.myChart.width = 100 + "px"; // 页面一半的大小
    },
    showScatterInGeo() {
      this.$echarts.registerMap("china", chinamap);
      this.myChart = this.$echarts.init(
        document.getElementById("echart_china")
      );
      var option = {
        geo: {
          map: "china",
          aspectScale: 0.75,
          zoom: 1.1,
        },
        tooltip: {
          formatter: "{b}:{c}",
        },
        visualMap: {
          min: 0,
          max: this.maxValue || 200,
          left: "10%",
          top: "bottom",
          text: ["高", "低"],
          calculable: true,
          // orient: "horizontal",
          color: ["#0b50b9", "#fff"],
        },
        series: [
          {
            zoom: 1.1,
            map: "china",
            type: "map",

            itemStyle: {
              normal: {
                borderColor: "rgba(0, 0, 0, 0.2)",
              },
              emphasis: {
                shadowOffsetX: 0,
                shadowOffsetY: 0,
                shadowBlur: 20,
                borderWidth: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
            label: {
              normal: {
                show: false,
              },
              emphasis: {
                show: false,
              },
            },
            data: this.provinceList,
          },
        ],
      };
      this.myChart.setOption(option);
      window.addEventListener("resize", () => {
        this.myChart.resize();
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.area_container {
  margin: 15px;
  background-color: #fafafb;
  // min-height: 339px;
  min-height: 470px;
  display: flex;
  .mapCharts {
    width: 50%;
    padding-top: 17px;
    padding-left: 18px;
    span {
      font-size: 13px;
      font-weight: 500;
      line-height: 31px;
      color: #4d4d4d;
    }
  }
  .mapTable {
    width: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .search_table {
    height: 100%;
    padding: 18px 22px;
    span {
      font-size: 13px;
      font-weight: 500;
      line-height: 31px;
      color: #4d4d4d;
    }
  }
}
</style>
