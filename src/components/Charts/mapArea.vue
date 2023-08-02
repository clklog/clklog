<template>
  <div id="app" style="width: 100%; height: 100%">
    <div
      id="echart_china"
      ref="echart_china"
      style="width: 100%; height: 100%"
    />
  </div>
</template>

<script>
import { validLowerCase } from "@/utils/validate";
import chinamap from "echarts/map/json/china.json";
import { percentage } from "@/utils/percent";
export default {
  data() {
    return {
      maxValue: 200,
      myChart: null,
      provinceList: [
        // { name: "北京", value: this.randomData() },
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
      getAreaList: null,
    };
  },
  mounted() {
    // this.showScatterInGeo();
  },
  methods: {
    getMapChartsData(val) {
      // let result = val.reduce((resp, obj) => {
      //   var originObj = resp.find((item) => item.province === obj.province);
      //   if (originObj) {
      //     originObj.pv += obj.pv;
      //   } else {
      //     resp.push(obj);
      //   }
      //   return resp;
      // }, []);
      // 加载接口数据清除
      this.provinceList.map((item) => {
        item.value = 0;
      });
      let maxValue = [];
      let result = val;
      // 地图数据匹配
      for (let i = 0; i < this.provinceList.length; i++) {
        this.provinceList[i].visitCountRate = 0;
        this.provinceList[i].uv = 0;
        this.provinceList[i].uvRate = 0;
        for (let j = 0; j < result.length; j++) {
          // if (this.provinceList[i].name == result[j].province) {
          //   this.provinceList[i].value = result[j].pv;
          //   // this.provinceList[i]. = result[j].pv;
          //   maxValue.push(result[j].pv);
          // }
          if (this.provinceList[i].name == result[j].province) {
            this.provinceList[i].value = result[j].visitCount;
            this.provinceList[i].visitCountRate = percentage(result[j].visitCountRate) ;
            this.provinceList[i].uv = result[j].uv;
            this.provinceList[i].uvRate = percentage(result[j].uvRate) ;
            maxValue.push(result[j].visitCount);
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
        // tooltip: {
        //   formatter: "{b}:{c}",
        // },
        tooltip: {
          formatter(params, ticket, callback) {
            // params.data 就是series配置项中的data数据遍历
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

        // 省会的位置标注
        // legend: {
        //   orient: 'vertical',
        //   left: 'left',
        //   data:['']
        // },
        visualMap: {
          min: 0,
          max: this.maxValue,
          left: "10%",
          top: "bottom",
          text: ["高", "低"],
          calculable: true,
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
                // borderWidth: 1, //边框大小
                // borderColor: "black",
                // areaColor: "red", //背景颜色
              },
              emphasis: {
                shadowOffsetX: 0,
                shadowOffsetY: 0,
                shadowBlur: 20,
                borderWidth: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
                // areaColor: "pink",
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
