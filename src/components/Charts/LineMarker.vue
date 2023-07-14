<template>
  <div :id="id" :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import echarts from "echarts";
import resize from "./mixins/resize";

export default {
  mixins: [resize],
  props: {
    defaultLege:{

    },
    flowTrendListed: {
      // default: [],
    },
    className: {
      type: String,
      default: "chart",
    },
    id: {
      type: String,
      default: "id",
    },
    width: {
      type: String,
      default: "200px",
    },
    height: {
      type: String,
      default: "200px",
    },
  },
  data() {
    return {
      chart: null,
      pv: [],
      uv: [],
      visit: [],
      ipCount: [],
      bounceRate: [],
      time: [],
      // headLege: ["浏览量", "访问次数"],
      headLege: [],
      flowTrendList:[],
    };
  },
  watch :{
    flowTrendListed(val){
      if (val && val.length > 0) {
        this.flowTrendList = val
        this.checkSearchValue(this.defaultLege)
      }else {
        this.checkSearchValue([])
      }
    }
  },
  mounted() {
    this.initChart();
    this.flowTrendList = this.flowTrendListed
    this.headLege = this.defaultLege;
    this.checkSearchValue(this.defaultLege)
   
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
  },
  methods: {
    changeLegend(val) {
      this.headLege = val;
      // console.log(val, "传递的值");
      this.checkSearchValue(val)
    },
    checkSearchValue(val){
     this.time = []
      const { time, pv, uv, visit, ipCount, bounceRate } = this;
      // 时间
      
      // this.flowTrendList.map((item) => {
      //   if (item.time) {
      //     return time.push(item.time.slice(11, 13));
      //   } else {
      //     time.push(0);
      //   }
      // });
      this.flowTrendList.map((item) => {
        if (item.statHour) {
          return time.push(item.statHour);
        } else {
          time.push(0);
        }
      });
      if (val.includes("浏览量")) {
        this.flowTrendList.map((item) => {
          if (item.pv) {
            return pv.push(item.pv);
          } else {
            pv.push(0);
          }
        });
      } else {
        this.pv = [];
      }
      if (val.includes("访客数")) {
        this.flowTrendList.map((item) => {
          if (item.uv) {
            return uv.push(item.uv);
          } else {
            uv.push(0);
          }
        });
      } else {
        this.uv = [];
      }
      if (val.includes("IP数")) {
         this.flowTrendList.map((item) => {
          if (item.ipCount) {
            return ipCount.push(item.ipCount);
          } else {
            ipCount.push(0);
          }
        });
      } else {
        this.ipCount = [];
      }
      if (val.includes("访问次数")) {
        this.flowTrendList.map((item) => {
          if (item.visit) {
            return visit.push(item.visit);
          } else {
            visit.push(0);
          }
        });
      } else {
        this.visit = [];
      }

      if (val.includes("跳出率")) {
        this.flowTrendList.map((item) => {
          if (item.bounceRate) {
            return bounceRate.push(item.bounceRate);
          } else {
            bounceRate.push(0);
          }
        });
      } else {
        this.bounceRate = [];
      }
      this.initChart();
    },
    initChart() {
      this.chart = echarts.init(document.getElementById(this.id));

      this.chart.setOption({
        backgroundColor: "#FAFAFB",
        // title: {
        //   top: 20,
        //   text: "Requests",
        //   textStyle: {
        //     fontWeight: "normal",
        //     fontSize: 16,
        //     color: "red",
        //   },
        //   left: "1%",
        // },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            lineStyle: {
              color: "#C0CADB",
              width: 3,
            },
          },
        },
        legend: {
          top: 20,
          icon: "rect",
          itemWidth: 14,
          itemHeight: 5,
          itemGap: 13,
          data: this.headLege,
          right: "4%",
          textStyle: {
            fontSize: 12,
            color: "black",
          },
        },
        grid: {
          top: 100,
          left: "2%",
          right: "2%",
          bottom: "2%",
          containLabel: true,
        },
        xAxis: [
          {
            type: "category",
            boundaryGap: false,
            // data: [0, 4, 8, 12, 16, 20],
            data:this.time,
            nameTextStyle: {
              fontWeight: 600,
              fontSize: 18,
            },
            axisTick: {
              show: false,
            },
          },
        ],
        yAxis: [
          {
            type: "value",
            nameTextStyle: {
              fontWeight: 600,
              fontSize: 18,
            },
            axisTick: {
              show: false,
            },
            // data: [50000, 100000, 150000, 200000, 250000],
          },
        ],
        series: [
          {
            name: "浏览量",
            type: "line",
            itemStyle: {
              normal: {
                color: "#A4C4FE",
              },
            },
            lineStyle: {
              width: 3,
            },
            // data: [120152, 132120, 265235, 144225, 178212, 168885],
            data: this.pv,
          },
          {
            name: "访客数",
            type: "line",
            itemStyle: {
              normal: {
                color: "yellow",
              },
            },
            lineStyle: {
              width: 3,
            },
            // data: [120152, 132120, 265235, 144225, 178212, 168885],
            data: this.uv,
          },
          {
            name: "访问次数",
            type: "line",
            itemStyle: {
              normal: {
                color: "#3D64E6",
                // borderColor: "#5fb4db",
                // borderWidth: 12,
              },
            },
            lineStyle: {
              width: 3,
            },
            data: this.visit,
          },
          {
            name: "IP数",
            type: "line",
            itemStyle: {
              normal: {
                color: "red",
                // borderColor: "#5fb4db",
                // borderWidth: 12,
              },
            },
            lineStyle: {
              width: 3,
            },
            data: this.ipCount,
          },
          {
            name: "跳出率",
            type: "line",
            itemStyle: {
              normal: {
                color: "pink",
                // borderColor: "#5fb4db",
                // borderWidth: 12,
              },
            },
            lineStyle: {
              width: 3,
            },
            data: this.bounceRate,
          },
        ],
      });
    },
     // 处理接口数据
     processData() {
      const { time, pv, uv, visit, ipCount, bounceRate } = this;
      // 时间
      this.flowTrendList.map((item) => {
        if (item.time) {
          return time.push(item.time.slice(11, 13));
        } else {
          time.push(0);
        }
      });
      // console.log(time, 234);
      this.flowTrendList.map((item) => {
        if (item.pv) {
          return pv.push(item.pv);
        } else {
          pv.push(0);
        }
      });
      this.flowTrendList.map((item) => {
        if (item.uv) {
          return uv.push(item.uv);
        } else {
          uv.push(0);
        }
      });
      this.flowTrendList.map((item) => {
        if (item.visit) {
          return visit.push(item.visit);
        } else {
          visit.push(0);
        }
      });
      this.flowTrendList.map((item) => {
        if (item.ipCount) {
          return ipCount.push(item.ipCount);
        } else {
          ipCount.push(0);
        }
      });
      this.flowTrendList.map((item) => {
        if (item.bounceRate) {
          return uv.push(item.bounceRate);
        } else {
          bounceRate.push(0);
        }
      });
      // console.log(this.bounceRate, "跳出了");
    },
  },
};
</script>
