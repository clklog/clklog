<template>
  <div class="area_container">
    <div class="mapCharts">
      <span>TOP10访问区域指标展示</span>
      <div
        id="echart_china"
        ref="echart_china"
        style="width: 100%; height: 270px"
      />
    </div>
    <div class="mapTable">
      <div style="width: 500px;height:287px;">
        <el-table
          ref="singleTable"
          :data="getAreaList"
          highlight-current-row
          style="width: 100%; height: 287px;overflow-y: auto;"
        >
          <el-table-column type="index" width="100"> </el-table-column>
          <el-table-column property="province" label="省份" width="120">
          </el-table-column>
          <el-table-column property="pv" label="访问次数" width="120">
          </el-table-column>
          <el-table-column property="percent" label="占比"></el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import chinamap from "echarts/map/json/china.json";
import resize from "@/components/Charts/mixins/resize";
import { getAreaApi } from "@/api/trackingapi/area";
export default {
  mixins: [resize],
  data() {
    return {
      getAreaList: null,
      myChart: null,
      dataList: [
        { name: "北京", value: this.randomData() },
        { name: "天津", value: this.randomData() },
        { name: "上海", value: this.randomData() },
        { name: "重庆", value: this.randomData() },
        { name: "河北", value: this.randomData() },
        { name: "河南", value: this.randomData() },
        { name: "云南", value: this.randomData() },
        { name: "辽宁", value: this.randomData() },
        { name: "黑龙江", value: this.randomData() },
        { name: "湖南", value: this.randomData() },
        { name: "安徽", value: this.randomData() },
        { name: "山东", value: this.randomData() },
        { name: "新疆", value: 0 },
        { name: "江苏", value: this.randomData() },
        { name: "浙江", value: this.randomData() },
        { name: "江西", value: this.randomData() },
        { name: "湖北", value: this.randomData() },
        { name: "广西", value: this.randomData() },
        { name: "甘肃", value: this.randomData() },
        { name: "山西", value: this.randomData() },
        { name: "内蒙古", value: this.randomData() },
        { name: "陕西", value: this.randomData() },
        { name: "吉林", value: this.randomData() },
        { name: "福建", value: this.randomData() },
        { name: "贵州", value: this.randomData() },
        { name: "广东", value: this.randomData() },
        { name: "青海", value: this.randomData() },
        { name: "西藏", value: this.randomData() },
        { name: "四川", value: this.randomData() },
        { name: "宁夏", value: this.randomData() },
        { name: "海南", value: this.randomData() },
        { name: "台湾", value: this.randomData() },
        { name: "香港", value: this.randomData() },
        { name: "澳门", value: this.randomData() },
      ],
      tableData: [
        {
          date: "江苏",
          name: "5456",
          address: "78%",
        },
        {
          date: "广东",
          name: "323",
          address: "0.9%",
        },
        {
          date: "上海",
          name: "2334",
          address: "133%",
        },
        {
          date: "南京",
          name: "2132",
          address: "12%",
        },
      ],
    };
  },
  mounted() {
    this.showScatterInGeo();
    this.getAreaApi();
  },
  methods: {
    getAreaApi() {
      const params = {
        timeType: "day",
        channel: ["安卓", "苹果", "网站", "微信小程序"],
        startTime: "2023-06-08",
        endTime: "2023-06-10",
        projectName: "",
      };
      getAreaApi(params).then((res) => {
        this.getAreaList = res.data;
      });
    },
    randomData() {
      return Math.round(Math.random() * 500);
    },
    resizeMyChartContainer() {
      this.myChart.height = 100 + "px"; // 页面一半的大小
      this.myChart.width = 100 + "px"; // 页面一半的大小
    },
    showScatterInGeo() {
      // 2. 注册可用的地图，只在 geo 组件或者map图表类型中使用

      this.$echarts.registerMap("china", chinamap);
      this.myChart = this.$echarts.init(
        document.getElementById("echart_china")
      );
      var option = {
        geo: {
          // 地理坐标系组件,支持在地理坐标系上绘制散点图、线图
          map: "china",
          aspectScale: 0.75,
          zoom: 1.1,
        },
        tooltip: {
          formatter: "{b}:{c}",
        },
        // 省会的位置标注
        // legend: {
        //   orient: 'vertical',
        //   left: 'left',
        //   data:['']
        // },
        visualMap: {
          min: 0,
          max: 1500,
          left: "10%",
          top: "bottom",
          text: ["高", "低"],
          calculable: true,
          orient: "horizontal",
          color: ["#0b50b9", "#FFFFFF"],
        },
        series: [
          {
            zoom: 1.3,
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
            data: this.dataList,
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
  height: 339px;
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
