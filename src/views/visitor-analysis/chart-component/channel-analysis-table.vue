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
              <p>{{ percentageFun(item.bounceRate) || "--" }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="chartsIcon">
      <flowPoint ref="flowPoint" @flowPoint="flowPoint" byChannel></flowPoint>

      <div class="table-content">
        <el-table
          :data="
            channelTableData.slice(
              (currentPage - 1) * pageSize,
              currentPage * pageSize
            )
          "
          :header-cell-style="{ textAlign: 'center' }"
          :cell-style="{ textAlign: 'center' }"
          border
          style="width: 100%"
        >
          <el-table-column type="index" label="序号" width="150" />
          <el-table-column prop="channel" label="渠道类型" width="150" />
          <el-table-column prop="date" label="流量基础指标" width="150">
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
              v-if="visitCountRate"
              prop="visitCountRate"
              label="访问次数占比"
              sortable
            />
            <el-table-column v-if="uv" prop="uv" label="访客数(UV)" sortable />
            <el-table-column
              v-if="uvRate"
              prop="uvRate"
              label="访客数占比"
              sortable
            />
            <el-table-column
              v-if="newUv"
              prop="newUv"
              label="新访客数"
              sortable
            />
            <el-table-column
              v-if="newUvRate"
              prop="newUvRate"
              label="访客数占比"
              sortable
            />
            <el-table-column
              v-if="ipCount"
              prop="ipCount"
              label="IP数"
              sortable
            />
            <el-table-column
              v-if="ipCountRate"
              prop="ipCountRate"
              label="IP数占比"
              sortable
            />
          </el-table-column>
          <el-table-column prop="date" label="流量质量指标" width="150">
            <el-table-column
              v-if="bounceRate"
              prop="bounceRate"
              label="跳出率"
              sortable
            />
            <el-table-column
              prop="avgVisitTime"
              label="平均访问时长"
              sortable
              v-if="avgVisitTime"
            />
            <el-table-column
              v-if="avgVisitTimeRate"
              prop="avgVisitTimeRate"
              label="平均访问时长占比"
              sortable
            />
            <el-table-column
              v-if="avgPv"
              prop="avgPv"
              label="平均访问页数"
              sortable
            />
            <el-table-column
              v-if="avgPvRate"
              prop="avgPvRate"
              label="平均访问页数占比"
              sortable
            />
            <!-- <el-table-column prop="amount10" label="平均访问页数" sortable /> -->
          </el-table-column>
        </el-table>
      </div>
      <div class="block">
        <el-pagination
          :current-page="currentPage"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="pageSize"
          layout=" sizes, prev, pager, next, jumper"
          :total="total"
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
      currentPage: 1,
      channelTableData: [],
      channelList: ["pv", "visitCount"],
      flowQuality: ["bounceRate"],
      equipmentList: [],
      pv: false,
      visitCount: false,
      newUv: false,
      newUvRate: false,
      uv: false,
      ipCount: false,
      avgPv: false,
      avgVisitTime: false,
      bounceRate: false,
      pvRate: false,
      newUvRate: false,
      avgVisitTimeRate: false,
      avgPvRate: false,
      // pvRate visitCount visitCountRate uvRate ipCountRate
      pvRate: false,
      visitCount: false,
      visitCountRate: false,
      uvRate: false,
      ipCountRate: false,
      currentPage: 1,
      total: 0,
      pageSize: 10,
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
      this.currentPage = 1;
      this.pageSize = val;
    },
    handleCurrentChange(val) {
      this.currentPage = val;
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
        if (val.includes("newUvRate")) {
          this.newUvRate = true;
        } else {
          this.newUvRate = false;
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

        if (val.includes("pvRate")) {
          this.pvRate = true;
        } else {
          this.pvRate = false;
        }

        if (val.includes("visitCount")) {
          this.visitCount = true;
        } else {
          this.visitCount = false;
        }
        if (val.includes("visitCountRate")) {
          this.visitCountRate = true;
        } else {
          this.visitCountRate = false;
        }
        if (val.includes("uvRate")) {
          this.uvRate = true;
        } else {
          this.uvRate = false;
        }
        if (val.includes("ipCountRate")) {
          this.ipCountRate = true;
        } else {
          this.ipCountRate = false;
        }
        if (val.includes("avgVisitTimeRate")) {
          this.avgVisitTimeRate = true;
        } else {
          this.avgVisitTimeRate = false;
        }
        if (val.includes("avgPvRate")) {
          this.avgPvRate = true;
        } else {
          this.avgPvRate = false;
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
