<template>
  <div>
    <div class="documentation-container">
      <!-- 流量概览 -->
      <div class="Overview">
        <div class="trafficHead" style="padding-left: 15px">渠道分析</div>
        <div class="bid-list-page">
          <originView ref="originView" byFlowView></originView>
          <div
            class="bid-list-record"
            v-for="(item, index) in channelTableData"
            :key="index"
          >
            <div class="bid-list-item w157">
              <p>{{ item.channel || "--" }}</p>
            </div>
            <div class="bid-list-item w111">
              <p>{{ item.pv || "--" }}</p>
            </div>
            <div class="bid-list-item w111">
              <p>{{ item.visitCount || "--" }}</p>
            </div>
            <div class="bid-list-item w111">
              <p>{{ item.uv || "--" }}</p>
            </div>
            <div class="bid-list-item w111">
              <p>{{ item.ipCount || "--" }}</p>
            </div>
            <div class="bid-list-item w111">
              <p>{{ item.avgPv || "--" }}</p>
            </div>
            <div class="bid-list-item w111">
              <p>{{ item.visitTime || "--" }}</p>
            </div>
            <div class="bid-list-item w111">
              <p>{{ percentageFun(item.bounceRate)  || "--" }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="chartsIcon">
      <flowPoint ref="flowPoint" @flowPoint="flowPoint" byChannel></flowPoint>

      <div class="table-content">
        <el-table
          :data="tableData"
          :header-cell-style="{ textAlign: 'center' }"
          :cell-style="{ textAlign: 'center' }"
          border
          style="width: 100%"
        >
          <el-table-column type="index" label="序号" width="150" />
          <el-table-column prop="dataTime" label="渠道类型" width="150" />
          <el-table-column prop="date" label="流量基础指标" width="150">
            <el-table-column prop="amount1" label="浏览量(PV)" sortable />
            <el-table-column prop="amount5" label="访客数(UV)" sortable />
            <el-table-column prop="amount7" label="IP数" sortable />
          </el-table-column>
          <el-table-column prop="date" label="流量质量指标" width="150">
            <el-table-column prop="amount8" label="跳出率" sortable />
            <el-table-column prop="amount9" label="平均访问时长" sortable />
            <!-- <el-table-column prop="amount10" label="平均访问页数" sortable /> -->
          </el-table-column>
        </el-table>
      </div>
      <div class="block">
        <el-pagination
          :current-page="currentPage"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="10"
          layout=" sizes, prev, pager, next, jumper"
          :total="40"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import originView from "@/components/origin-view/index";
import flowPoint from "@/components/flowPoint/index";
import { percentage } from "@/utils/percent";
export default {
  components: { originView, flowPoint },
  data() {
    return {
      channelList: ["3", "5"],
      flowQuality: ["9"],
      tableData1: [
        {
          date: "2016-05-03",
          name: "王小虎",
          province: "上海",
          city: "普陀区",
          address: "上海市普陀区金沙江路 1518 弄",
          zip: 200333,
        },
        {
          date: "2016-05-02",
          name: "王小虎",
          province: "上海",
          city: "普陀区",
          address: "上海市普陀区金沙江路 1518 弄",
          zip: 200333,
        },
        {
          date: "2016-05-04",
          name: "王小虎",
          province: "上海",
          city: "普陀区",
          address: "上海市普陀区金沙江路 1518 弄",
          zip: 200333,
        },
        {
          date: "2016-05-01",
          name: "王小虎",
          province: "上海",
          city: "普陀区",
          address: "上海市普陀区金沙江路 1518 弄",
          zip: 200333,
        },
        {
          date: "2016-05-08",
          name: "王小虎",
          province: "上海",
          city: "普陀区",
          address: "上海市普陀区金沙江路 1518 弄",
          zip: 2003233,
        },
        {
          date: "2016-05-06",
          name: "王小虎",
          province: "上海",
          city: "普陀区",
          address: "上海市普陀区金沙江路 1518 弄",
          zip: 200333,
        },
        {
          date: "2016-05-07",
          name: "王小虎",
          province: "上海",
          city: "普陀区",
          address: "上海市普陀区金沙江路 1518 弄",
          zip: 200333,
        },
      ],
      tableData: [
        {
          dataTime: "总计",
          amount1: "14",
          amount2: "3.2",
          amount3: 10,
          amount4: "234",
          amount5: "3.2",
          amount6: 10,
          amount7: "234",
          amount8: "3.2",
          amount9: "100%",
          amount10: 69,
        },
        {
          dataTime: "新访客",
          amount1: "165",
          amount2: "4.43",
          amount3: 12,
          amount4: "24",
          amount5: "3.2",
          amount6: 10,
          amount7: "234",
          amount8: "3.2",
          amount9: "100%",
          amount10: 6,
        },
        {
          dataTime: "老访客",
          amount1: "165",
          amount2: "4.43",
          amount3: 12,
          amount4: "34",
          amount5: "3.2",
          amount6: 10,
          amount7: "234",
          amount8: "3.2",
          amount9: "100%",
          amount10: 9,
        },
      ],
      currentPage: 1,
      channelTableData: [],
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
    getChannelList(val) {
      this.channelTableData = val.sort((a, b) => {
        if (a.channel === "全部") {
          return -1;
        } else if (b.channel === "全部") {
          return 1;
        }
        return 0;
      });
    },
    // 分页器
    handleSizeChange(val) {
    },
    handleCurrentChange(val) {
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
    percentageFun(val) {
      return percentage(val);
    },
  },
};
</script>
<style lang="scss" scoped>
@import "~@/styles/components/el-pagination.scss";
@import "~@/styles/components/el-checkbox.scss";
.trafficHead {
  font-size: 14px;
  font-weight: 500;
  color: #4d4d4d;
  padding: 20px 30px 20px;
}
img {
  width: 11.44px;
  height: 11.44px;
  margin-left: 12px;
  cursor: pointer;
}
.Overview {
  margin: 20px;
  min-height: 200px;
  background-color: #fafafb;
  .bid-list-page {
    width: clas(100% -20px);
    display: flex;
    flex-direction: column;
    .bid-list-header {
      display: flex;
      .header-name {
        width: 14%;
        justify-content: center;
        height: 30px;
        display: flex;
        align-items: center;
      }
      .w156 {
        font-size: 13px;
        color: #3f3e3e;
      }
    }
    .bid-list-record {
      display: flex;
      .bid-list-item {
        width: 14%;
        justify-content: center;
        height: 30px;
        display: flex;
        align-items: center;
      }
      .w157 {
        color: #252424;
        font-size: 14px;
        line-height: 14px;
      }
      .w158 {
        color: #3a98e6;
        font-size: 17px;
        line-height: 17px;
      }
    }
  }
}
.w111 {
  font-size: 10px;
  font-weight: 400;
  line-height: 8px;
  color: #4d4d4d;
}
.chartsIcon {
  position: relative;
  box-sizing: border-box;
  margin: 20px;
  padding-top: 1px;
  min-height: 461px;
  background: rgba(250, 250, 251);
  //   background: rgba(158, 158, 161, 0.39);
  border-radius: 6px;

  .flow-indicator {
    min-height: 58px;
    background: rgba(252, 252, 252, 0.39);
    border: 1px solid #f0f0f5;
    border-radius: 6px;
    box-sizing: border-box;
    margin: 12px;
    .setSpace {
      margin-bottom: 12px;
    }
    .flow-item {
      display: flex;
      align-items: center;
      margin-top: 12px;
      margin-left: 10px;
      .flow-title {
        white-space: nowrap;
        margin-right: 21px;
        font-size: 12px;
        font-weight: 400;
        line-height: 16px;
        color: #4d4d4d;
      }
      .el-checkbox {
        margin-right: 80px;
        font-size: 12px;
        font-weight: 400;
        line-height: 25px;
        color: #697195;
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
