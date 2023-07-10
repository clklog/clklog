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
import chinamap from 'echarts/map/json/china.json'
export default {
  data() {
    return {
      myChart: null,
      dataList: [
        { name: '北京', value: this.randomData() },
        { name: '天津', value: this.randomData() },
        { name: '上海', value: this.randomData() },
        { name: '重庆', value: this.randomData() },
        { name: '河北', value: this.randomData() },
        { name: '河南', value: this.randomData() },
        { name: '云南', value: this.randomData() },
        { name: '辽宁', value: this.randomData() },
        { name: '黑龙江', value: this.randomData() },
        { name: '湖南', value: this.randomData() },
        { name: '安徽', value: this.randomData() },
        { name: '山东', value: this.randomData() },
        { name: '新疆', value: 0 },
        { name: '江苏', value: this.randomData() },
        { name: '浙江', value: this.randomData() },
        { name: '江西', value: this.randomData() },
        { name: '湖北', value: this.randomData() },
        { name: '广西', value: this.randomData() },
        { name: '甘肃', value: this.randomData() },
        { name: '山西', value: this.randomData() },
        { name: '内蒙古', value: this.randomData() },
        { name: '陕西', value: this.randomData() },
        { name: '吉林', value: this.randomData() },
        { name: '福建', value: this.randomData() },
        { name: '贵州', value: this.randomData() },
        { name: '广东', value: this.randomData() },
        { name: '青海', value: this.randomData() },
        { name: '西藏', value: this.randomData() },
        { name: '四川', value: this.randomData() },
        { name: '宁夏', value: this.randomData() },
        { name: '海南', value: this.randomData() },
        { name: '台湾', value: this.randomData() },
        { name: '香港', value: this.randomData() },
        { name: '澳门', value: this.randomData() }
      ]
    }
  },
  mounted() {
    this.showScatterInGeo()
  },
  methods: {
    randomData() {
      return Math.round(Math.random() * 500)
    },
    /*
        geo:地理坐标系组件( https://echarts.apache.org/zh/option.html#geo)
        地理坐标系组件用于地图的绘制，支持在地理坐标系上绘制散点图
      */
    resizeMyChartContainer() {
      this.myChart.height = 100 + 'px' // 页面一半的大小
      this.myChart.width = 100 + 'px' // 页面一半的大小
    },
    showScatterInGeo() {
      // 2. 注册可用的地图，只在 geo 组件或者map图表类型中使用

      this.$echarts.registerMap('china', chinamap)
      this.myChart = this.$echarts.init(
        document.getElementById('echart_china')
      )
      var option = {
        geo: {
          // 地理坐标系组件,支持在地理坐标系上绘制散点图、线图
          map: 'china',
          aspectScale: 0.75,
          zoom: 1.1
        },
        tooltip: {
          formatter: '{b}:{c}'
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
          left: '10%',
          top: 'bottom',
          text: ['高', '低'],
          calculable: true,
          color: ['#0b50b9', '#FFFFFF']
        },
        series: [
          {
            zoom: 1.3,
            map: 'china',
            type: 'map',

            itemStyle: {
              normal: {
                borderColor: 'rgba(0, 0, 0, 0.2)'
              },
              emphasis: {
                shadowOffsetX: 0,
                shadowOffsetY: 0,
                shadowBlur: 20,
                borderWidth: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            label: {
              normal: {
                show: false
              },
              emphasis: {
                show: false
              }
            },
            data: this.dataList
          }
        ]
      }
      this.myChart.setOption(option)
      window.addEventListener("resize", () => {
        this.myChart.resize();
      });
      //   var option = {
      //     geo: {
      //       show:true,
      //       type: "map",
      //       map: "china",
      //       label: {
      //         // label 设置文本标签的显示格式，去掉不影响显示地图
      //         normal: {
      //           color: "red",
      //           show: true, //显示省份名称
      //         },
      //       },
      //     },
      //     visualMap: {
      //       min: 0,
      //       max: 1500,
      //       left: '10%',
      //       top: 'bottom',
      //       text: ['高','低'],
      //       calculable : true,
      //       color:['#0b50b9','#FFFFFF']
      //     },
      //     series: [
      //       {
      //         name: "在地图中显示散点图",
      //         type: "scatter",
      //         coordinateSystem: "geo", //设置坐标系为 geo
      //         data: [
      //           //这里放标注点的坐标[{name: "北京",value: [116.46, 39.92]}]
      //           { name: "北京", value: [116.41995, 40.18994] },
      //           { name: "郑州", value: [113.665412, 34.757975] },
      //           { name: "天津", value: [117.205126, 39.034933] },
      //           { name: "昆明", value: [102.81844, 24.906231] },
      //           { name: "广州", value: [113.26453, 23.155008] },
      //         ],
      //       },
      //     ],
      //   };
    }
  }
}
</script>
