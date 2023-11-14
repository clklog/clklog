<template>
  <!-- 公用流量指标 -->
  <div class="flow-indicator public_indicator">
    <div class="flow-item">
      <div class="flow-title">流量基础指标</div>
      <el-checkbox-group
        v-model="channelList"
        class="checkBoxStyle"
        @change="handelChannelList"
      >
        <el-checkbox label="pv">浏览量(PV)</el-checkbox>
        <el-checkbox label="pvRate">浏览量占比</el-checkbox>
        <el-checkbox label="visitCount">访问次数</el-checkbox>
        <el-checkbox v-if="byChannel" label="visitCountRate"
          >访问次数占比</el-checkbox
        >
        <el-checkbox label="uv">访客数(UV)</el-checkbox>
        <el-checkbox v-if="byChannel" label="uvRate">访客数占比</el-checkbox>
        <el-checkbox v-if="!byNewOldVisit" label="newUv">新访客数</el-checkbox>
        <el-checkbox v-if="!byNewOldVisit" label="newUvRate">新访客数占比</el-checkbox>
        <el-checkbox label="uvRate" v-if="byNewOldVisit">访客数占比</el-checkbox>
        <el-checkbox label="ipCount" >IP数</el-checkbox>
        <el-checkbox
          v-if="byChannel"
          label="ipCountRate"
          style="margin-right: 0"
          >IP数占比</el-checkbox
        >
      </el-checkbox-group>
    </div>
    <div class="flow-item setSpace">
      <div class="flow-title">流量质量指标</div>
      <el-checkbox-group
        v-model="flowQuality"
        class="checkBoxStyle"
        @change="handelFlowQuality"
      >
        <el-checkbox label="bounceRate">跳出率</el-checkbox>
        <el-checkbox label="avgVisitTime">平均访问时长</el-checkbox>
        <el-checkbox v-if="byChannel" label="visitTimeRate"
          >平均访问时长占比</el-checkbox
        >
        <el-checkbox label="avgPv">平均访问页数</el-checkbox>
        <!-- <el-checkbox v-if="byChannel" label="avgPvRate"
          >平均访问页数占比</el-checkbox
        > -->
      </el-checkbox-group>
    </div>
  </div>
</template>

<script>
import { percentage } from "@/utils/percent";
import { blobDownloads } from "@/utils/localDownloadUtil.js";
import {
  exportFlowTrendDetailApi,
  exportAreaDetailApi,
  exportSourceWebsiteDetailApi,
  exportVisitorDetailApi,
  exportChannelDetailApi,
  exportDeviceDetailApi,
} from "@/api/trackingapi/download";
export default {
  props: {
    byChannel: {
      type: Boolean,
      default: false,
    },
    byNewOldVisit:{
      type: Boolean,
      default: false,
    }
  },
  data() {
    return {
      flowTableList: [],
      channelList: ["pv", "uv", "ipCount"],
      flowQuality: ["avgPv"],
      pv: false,
      visitCount: false,
      newUv: false,
      uv: false,
      ipCount: false,
      avgPv: false,
      avgVisitTime: false,
      bounceRate: false,
      pvRate: false,
      newUvRate: false,
      mergedArr: [],
      dateTime: null,
    };
  },
  mounted() {
    this.initShowTable();
    this.$bus.$on("publicEventDown", (val) => {
      this.publicEventDown(val);
    });
  },
  methods: {
    publicEventDown(val) {
      let params = val;
      params.projectName = this.$store.getters.projectName;
      params.cols = [...this.channelList, ...this.flowQuality];
      let path = this.$route.path;
      // if (path.includes("trendAnalysis")) {
      //   exportFlowTrendDetailApi(params).then((res) => {
      //     let fileName = res.headers["content-disposition"] || "";
      //     let index1 = fileName.indexOf("filename=");
      //     let result = fileName.substring(index1);
      //     result = decodeURIComponent(decodeURI(result.slice(9)));
      //     // blobDownloads(res.data,result);
      //   });
      // }
      console.log(path,"路径");
      switch (path) {
        case "/visitorAnalysis/trend": {
          let regular = ["statTime"]
          params.cols = [...params.cols, ...regular]; //增加固定列
          // console.log(params,"下载参数");
          exportFlowTrendDetailApi(params).then((res) => {
            let name = this.sliceTypeFile(res);
            blobDownloads(res.data, name);
          });
          break;
        }
        case "/visitorAnalysis/regional": {
          let regular = ["index","province"]
          params.cols = [...params.cols, ...regular];
          exportAreaDetailApi(params).then((res) => {
            let name = this.sliceTypeFile(res);
            blobDownloads(res.data, name);
          });
          break;
        }
        case "/visitorAnalysis/sourceWeb": {
          let regular = ["index","sourcesite"]
          params.cols = [...params.cols, ...regular];
          exportSourceWebsiteDetailApi(params).then((res) => {
            let name = this.sliceTypeFile(res);
            blobDownloads(res.data, name);
          });
          break;
        }
        case "/visitorAnalysis/newoldVisitor": {
          let regular = ["index","visitorType"]
          params.cols = [...params.cols, ...regular];
          exportVisitorDetailApi(params).then((res) => {
            let name = this.sliceTypeFile(res);
            blobDownloads(res.data, name);
          });
          break;
        }
        case "/visitorAnalysis/channel": {
          let regular = ["index","channel"]
          params.cols = [...params.cols, ...regular];
          exportChannelDetailApi(params).then((res) => {
            let name = this.sliceTypeFile(res);
            blobDownloads(res.data, name);
          });
          break;
        }
        case "/visitorAnalysis/equipment": {
          let regular = ["index","device"]
          params.cols = [...params.cols, ...regular];
          exportDeviceDetailApi(params).then((res) => {
            let name = this.sliceTypeFile(res);
            blobDownloads(res.data, name);
          });
          break;
        }
      }
    },
    sliceTypeFile(res) {
      let fileName = res.headers["content-disposition"] || "";
      let index1 = fileName.indexOf("filename=");
      let result = fileName.substring(index1);
      result = decodeURIComponent(decodeURI(result.slice(9)));
      return result;
    },
    initDate() {
      const date = new Date();
      const year = date.getFullYear();
      const month = date.getMonth() + 1;
      const day = date.getDate();
      const hours = date.getHours();
      const minutes = date.getMinutes();
      const seconds = date.getSeconds();
      this.dateTime = month + "月" + day + "日" + hours + "时" + minutes + "分";
    },
    handelChannelList() {
      this.initShowTable();
    },
    handelFlowQuality() {
      this.initShowTable();
    },
    percentageFun(val) {
      return percentage(val);
    },
    initShowTable() {
      this.mergedArr = [];
      this.mergedArr = [...this.channelList, ...this.flowQuality];
      this.$emit("flowPoint", this.mergedArr);
    },
  },
  beforeDestroy() {
    this.$bus.$off(["publicEventDown"]); // 同时关闭多个用数组形式放进去
  },
};
</script>
<style lang="scss" scoped>
::v-deep {
  @import "~@/styles/components/el-checkbox.scss";
  @import "~@/styles/components/el-pagination.scss";
}
// .chartsIcon {
//   box-sizing: border-box;
//   margin: 20px;
//   padding-top: 1px;
//   min-height: 461px;
//   background: #fff;
//   border-radius: 6px;
// }
.chartsIcon {
  box-sizing: border-box;
  margin: 20px;
  padding-top: 1px;
  min-height: 461px;
  background: #fff;
  border-radius: 6px;
  padding: 22px;
  .flow-indicator {
    margin-top: 10px;
    min-height: 58px;
    // background: rgba(252, 252, 252, 0.39);
    // background: red;
    border: 1px solid #f0f0f5;
    border-radius: 6px;
    box-sizing: border-box;
    // margin: 12px;
    .setSpace {
      margin-bottom: 12px;
    }
    .flow-item {
      display: flex;
      // align-items: center;
      margin-top: 12px;
      margin-left: 10px;
      .flow-title {
        white-space: nowrap;
        margin-right: 21px;
        font-size: 13px;
        font-weight: 400;
        line-height: 32px;
        color: #4d4d4d;
      }
    }
    .check_item {
      background-color: #ffffff;
      margin-left: 20px;
      height: 40px;
      border-radius: 4px;
      border: 1px solid #eee;
      display: flex;
      align-items: center;
      span {
        font-size: 14px;
        padding: 0 10px;
      }
      .checkBoxStyle {
        padding-right: 10px;
      }
    }
  }
}
</style>
