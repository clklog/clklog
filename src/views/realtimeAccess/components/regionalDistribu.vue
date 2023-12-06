<template>
  <div>
    <div class="block-head">
      <div class="block-title">
        地域分布图
        <div class="innerFont">(近24小时)</div>
      </div>
      <div class="block-head-icon"></div>
    </div>
    <div style="display: flex">
      <div id="chinaChart" style="width: 50%; height: 400px"></div>
      <div style="width: 50%">
        <div style="width: 100%">
          <el-table
            :data="tableData"
            class="public-radius"
            :header-cell-style="{
              textAlign: 'center',
              background: '#f7fafe ',
            }"
            :cell-style="tableHeaderColor"
            style="width: 100%"
            border
          >
            <el-table-column type="index" label="序号" min-width="20%" />
            <el-table-column
              prop="province"
              min-width="30%"
              :show-overflow-tooltip="true"
              label="省份"
            >
            </el-table-column>
            <el-table-column
              prop="pv"
              label="访问次数"
              min-width="25%"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              prop="pvRate"
              label="占比"
              min-width="25%"
              :show-overflow-tooltip="true"
            />
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import chinamap from "echarts/map/json/china.json";
export default {
  data() {
    return {
      areaMapChart: null,
      maxValue: 0,
      tableData: [
        {
          pv: "2016-05-02",
          province: "上海市普陀区金沙江路 1518 弄",
          pvRate: "30%",
        },
        {
          pv: "2016-05-04",
          province: "上海市普陀区金沙江路 1518 弄",
          pvRate: "30%",
        },
        {
          pv: "2016-05-01",

          province: "上海市普陀区金沙江路 1518 弄",
          pvRate: "30%",
        },
        {
          pv: "2016-05-03",
          province: "上海市普陀区金沙江路 1518 弄",
          pvRate: "30%",
        },
      ],
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
    };
  },
  mounted() {
    this.showScatterInGeo();
  },
  methods: {
    showScatterInGeo() {
      this.$echarts.registerMap("china", chinamap);
      this.areaMapChart = this.$echarts.init(document.getElementById("chinaChart"));
      var option = {
        geo: {
          map: "china",
          aspectScale: 0.75,
          zoom: 1.1,
        },
        grid: {
          left: "13%",
          right: "14%",
          top: "25%",
          bottom: "10%",
          containLabel: true,
        },

        tooltip: {
          formatter(params, ticket, callback) {
            let visitCountRate, uv, uvRate;
            if (params.data) {
              visitCountRate = params.data.visitCountRate;
              uv = params.data.uv;
              uvRate = params.data.uvRate;
            } else {
              visitCountRate = 0;
              uv = 0;
              uvRate = 0;
            }
            let htmlStr = `
              <div style="padding:10px;">
                <div style='font-size:14px;'> ${params.name}</div>
                <p style='text-align:left;margin-top:10px;'>
                  访问次数：${params.value}(占比：${visitCountRate})<br/>
                  访客数：${uv}(占比：${uvRate})<br/>
                </p>
              </div>
               
              `;
            return htmlStr;
          },
        },

        visualMap: {
          min: 0,
          max: this.maxValue || 200,
          left: "10%",
          top: "bottom",
          text: ["高", "低"],
          calculable: true,
          color: ["#2c7be5", "#fff"],
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
      this.areaMapChart.setOption(option);
      window.addEventListener("resize", () => {
        this.areaMapChart.resize();
      });
    },
    tableHeaderColor({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 1) {
        return "text-align:left";
      } else {
        return "text-align:center";
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.innerFont {
  font-size: 13px;
  font-weight: 400;
  color: #4d4d4d;
  margin-left: 8px;
}
</style>
