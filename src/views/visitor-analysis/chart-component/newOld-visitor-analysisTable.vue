<template>
  <div class="chartsIcon public-hoverItem">
    <flowPoint ref="flowPoint" @flowPoint="flowPoint"></flowPoint>
    <div class="table-content">
      <el-table
        :header-cell-style="{ textAlign: 'center' }"
        :cell-style="{ textAlign: 'center' }"
        :data="visitorDetailData"
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="150" />
        <el-table-column prop="visitorType" label="访客类型" width="150" />
        <el-table-column prop="date" label="流量基础指标">
          <el-table-column v-if="pv" prop="pv" label="浏览量(PV)" sortable />
          <el-table-column
            v-if="pvRate"
            prop="pvRate"
            label="浏览量占比"
            sortable
          />
          <el-table-column
            v-if="visitCount"
            prop="visitCount"
            label="访问次数"
            sortable
          />
          <el-table-column
            v-if="newUv"
            prop="newUv"
            label="新访客数"
            sortable
          />
          <el-table-column v-if="uv" prop="uv" label="访客数(UV)" sortable />
          <el-table-column
            v-if="newUvRate"
            prop="newUvRate"
            label="新访客数占比"
            sortable
          />
          <el-table-column
            v-if="ipCount"
            prop="ipCount"
            label="IP数"
            sortable
          />
        </el-table-column>
        <el-table-column prop="date" label="流量质量指标">
          <el-table-column
            v-if="bounceRate"
            prop="bounceRate"
            label="跳出率"
            sortable
          />
          <el-table-column
            v-if="avgVisitTime"
            prop="avgVisitTime"
            label="平均访问时长"
            sortable
          />
          <el-table-column
            v-if="avgPv"
            prop="avgPv"
            label="平均访问页数"
            sortable
          />
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import flowPoint from "@/components/flowPoint/index";
import { percentage } from "@/utils/percent";
import { formatTime } from "@/utils/format";
export default {
  components: { flowPoint },
  props: {
    commonParams: {},
  },
  mounted() {},
  computed: {},
  data() {
    return {
      channelList: ["pv", "visitCount", "newUvRate", "pvRate"],
      flowQuality: ["avgPv"],
      visitorDetailData: [],
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
    };
  },
  methods: {
    getVisitorDetail(val) {
      val.map((item) => {
        if (item.bounceRate) {
          item.bounceRate = percentage(item.bounceRate);
        }
        if (item.pvRate) {
          item.pvRate = percentage(item.pvRate);
        }
        if (item.avgVisitTime) {
          item.avgVisitTime = formatTime(Math.floor(item.avgVisitTime));
        }
        if (item.avgPv) {
          item.avgPv = Math.floor(item.avgPv);
        }
      });
      this.visitorDetailData = val.reverse();
    },
    flowPoint(val) {
      if (val.length > 0) {
        if (val.includes("pv")) {
          this.pv = true;
        } else {
          this.pv = false;
        }
        if (val.includes("visitCount")) {
          this.visitCount = true;
        } else {
          this.visitCount = false;
        }
        if (val.includes("newUv")) {
          this.newUv = true;
        } else {
          this.newUv = false;
        }
        if (val.includes("uv")) {
          this.uv = true;
        } else {
          this.uv = false;
        }
        if (val.includes("ipCount")) {
          this.ipCount = true;
        } else {
          this.ipCount = false;
        }
        if (val.includes("avgPv")) {
          this.avgPv = true;
        } else {
          this.avgPv = false;
        }
        if (val.includes("avgVisitTime")) {
          this.avgVisitTime = true;
        } else {
          this.avgVisitTime = false;
        }
        if (val.includes("bounceRate")) {
          this.bounceRate = true;
        } else {
          this.bounceRate = false;
        }
        if (val.includes("pvRate")) {
          this.pvRate = true;
        } else {
          this.pvRate = false;
        }
        if (val.includes("newUvRate")) {
          this.newUvRate = true;
        } else {
          this.newUvRate = false;
        }
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.chartsIcon {
  box-sizing: border-box;
  margin: 20px;
  padding-top: 1px;
  min-height: 461px;
  background: #fff;
  border-radius: 6px;

  // .flow-indicator {
  //   min-height: 58px;
  //   background: rgba(252, 252, 252, 0.39);
  //   border: 1px solid #f0f0f5;
  //   border-radius: 6px;
  //   box-sizing: border-box;
  //   margin: 12px;
  //   .setSpace {
  //     margin-bottom: 12px;
  //   }
  //   .flow-item {
  //     display: flex;
  //     align-items: center;
  //     margin-top: 12px;
  //     margin-left: 10px;
  //     .flow-title {
  //       margin-right: 21px;
  //       font-size: 12px;
  //       font-weight: 400;
  //       line-height: 16px;
  //       color: #4d4d4d;
  //     }
  //     .el-checkbox {
  //       margin-right: 80px;
  //       font-size: 12px;
  //       font-weight: 400;
  //       line-height: 15px;
  //       color: #697195;
  //     }
  //   }
  //   .check_item {
  //     background-color: #ffffff;
  //     margin-left: 20px;
  //     height: 40px;
  //     border-radius: 4px;
  //     border: 1px solid #eee;
  //     display: flex;
  //     align-items: center;
  //     // width: 30%;
  //     span {
  //       font-size: 14px;
  //       padding: 0 10px;
  //     }
  //     .checkBoxStyle {
  //       padding-right: 10px;
  //     }
  //   }
  // }
}
</style>
