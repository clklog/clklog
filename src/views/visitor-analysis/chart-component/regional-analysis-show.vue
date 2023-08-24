<template>
  <div class="area_container public-hoverItem">
    <div class="mapCharts">
      <span class="public-firstHead" style="padding:0 0;">TOP10访问区域指标展示</span>
      <div
        id="echart_china"
        ref="echart_china"
        style="width: 100%; height: 350px"
      />
    </div>
    <div class="mapTable">
      <div
        style="
          width: 500px;
          height: 405px;
          margin-top: 62px;
          overflow: hidden;
          margin-bottom: 20px;
        "
      >
        <el-table
          ref="singleTable"
          :data="apiProvinceList"
          highlight-current-row
          style="width: 100%"
        >
          <el-table-column type="index" width="80"> </el-table-column>
          <el-table-column
            :show-overflow-tooltip="true"
            property="province"
            label="省份"
            width="200"
          >
          </el-table-column>
          <el-table-column property="visitCount" label="访问次数">
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
      } else {
        this.provinceList.map((item) => {
          item.value = 0;
        });
      }
      // this.apiProvinceList.map((item) => {
      //   item.visitCountRate = percentage(item.visitCountRate);
      //   if (item.province == "未知省份") {
      //     // item.province = item.country + "-" + item.province;
      //   }
      // });
      for (let i = 0; i < this.apiProvinceList.length; i++) {
        this.apiProvinceList[i].visitCountRate = percentage(
          this.apiProvinceList[i].visitCountRate
        );
        if (this.apiProvinceList[i].province == "未知省份") {
          this.apiProvinceList.splice(i--, 1);
        }
      }
     
      let result = this.apiProvinceList;
      for (let i = 0; i < this.provinceList.length; i++) {
        this.provinceList[i].visitCountRate = 0;
        this.provinceList[i].uv = 0;
        this.provinceList[i].uvRate = 0;
        for (let j = 0; j < result.length; j++) {
          if (this.provinceList[i].name == result[j].province) {
            this.provinceList[i].value = result[j].visitCount;
            this.provinceList[i].visitCountRate = result[j].visitCountRate
            this.provinceList[i].uv = result[j].uv;
            this.provinceList[i].uvRate = percentage(result[j].uvRate);
            maxValue.push(result[j].visitCount);
          }
        }
      }
      this.apiProvinceList = this.apiProvinceList.splice(0, 10);
      this.showScatterInGeo();
    },
    randomData() {
      return Math.round(Math.random() * 500);
    },
    resizeMyChartContainer() {
      this.myChart.height = 100 + "px";
      this.myChart.width = 100 + "px";
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
        // tooltip: {
        //   formatter: "{b}:{c}",
        // },
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
          // backgroundColor:"#ff7f50",//提示标签背景颜色
          // textStyle:{color:"#fff"} //提示标签字体颜色
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
  background-color: #fff;
  min-height: 470px;
  margin-bottom: 20px;
  display: flex;
  .mapCharts {
    width: 50%;
    padding-top: 17px;
    padding-left: 18px;
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
