<template>
  <div class="chartsIcon">
    <div class="flow-indicator">
      <div class="flow-item">
        <div class="flow-title" style="line-height: 30px">流量基础指标</div>
        <el-checkbox-group
          v-model="channelList"
          class="checkBoxStyle"
          @change="handelChannelList"
        >
          <el-checkbox label="pv">浏览量(PV)</el-checkbox>
          <el-checkbox label="pvRate">浏览量占比</el-checkbox>
          <el-checkbox label="visitCount">访问次数</el-checkbox>
          <el-checkbox label="uv">访客数(UV)</el-checkbox>
          <el-checkbox label="newUv">新访客数</el-checkbox>
          <el-checkbox label="newUvRate">新访客比率</el-checkbox>
          <el-checkbox label="ipCount" style="margin-right: 0"
            >IP数</el-checkbox
          >
        </el-checkbox-group>
      </div>
      <div class="flow-item setSpace">
        <div class="flow-title" style="line-height: 30px">流量质量指标</div>
        <el-checkbox-group
          v-model="flowQuality"
          class="checkBoxStyle"
          @change="handelFlowQuality"
        >
          <el-checkbox label="bounceRate">跳出率</el-checkbox>
          <el-checkbox label="avgVisitTime">平均访问时长</el-checkbox>
          <el-checkbox label="avgPv">平均访问页数</el-checkbox>
        </el-checkbox-group>
      </div>
    </div>
    <div class="table-content">
      <el-table
        :header-cell-style="{ textAlign: 'center' }"
        :cell-style="{ textAlign: 'center' }"
        :data="
          flowTableList.slice(
            (currentPage - 1) * pageSize,
            currentPage * pageSize
          )
        "
        border
        style="width: 100%"
      >
        <el-table-column prop="statTime" label="日期" width="200" />
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
            v-if="newUv"
            prop="newUv"
            label="新访客数"
            sortable
          />
          <el-table-column v-if="uv" prop="uv" label="访客数(UV)" sortable />
          <el-table-column
            v-if="newUvRate"
            prop="newUvRate"
            label="新访客比率"
            sortable
          />
          <el-table-column
            v-if="ipCount"
            prop="ipCount"
            label="IP数"
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
    <div class="block">
      <el-pagination
        next-text="下一页"
        :current-page="currentPage"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="10"
        layout=" sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        hide-on-single-page
      />
    </div>
  </div>
</template>

<script>
import Bus from "@/utils/bus";
import { percentage } from "@/utils/percent";
export default {
  data() {
    return {
      flowTableList: [],
      channelList: ["pv", "visitCount"],
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
      allPointList: [
        "pv",
        "visitCount",
        "newUv",
        "uv",
        "ipCount",
        "avgPv",
        "avgVisitTime",
        "bounceRate",
        "pvRate",
        "newUvRate",
      ],
      mergedArr: [],
      currentPage: 1,
      total: 0,
      pageSize: 10,
      pageSizes: [10, 20, 30, 40, 50],
    };
  },
  mounted() {
    this.initShowTable();
  },
  methods: {
    handelChannelList() {
      this.initShowTable();
    },
    handelFlowQuality() {
      this.initShowTable();
    },
    percentageFun(val) {
      return percentage(val);
    },
    apiDetailList(val) {
      this.flowTableList = val.detail;
      this.flowTableList.map((item) => {
        if (item.bounceRate) {
          item.bounceRate = this.percentageFun(item.bounceRate);
        }
      });
      this.total = val.detail.length;
    },
    initShowTable() {
      this.mergedArr = [];
      this.mergedArr = [...this.channelList, ...this.flowQuality];
      if (this.mergedArr.length > 0) {
        if (this.mergedArr.includes("pv")) {
          this.pv = true;
        } else {
          this.pv = false;
        }
        if (this.mergedArr.includes("visitCount")) {
          this.visitCount = true;
        } else {
          this.visitCount = false;
        }
        if (this.mergedArr.includes("newUv")) {
          this.newUv = true;
        } else {
          this.newUv = false;
        }
        if (this.mergedArr.includes("uv")) {
          this.uv = true;
        } else {
          this.uv = false;
        }
        if (this.mergedArr.includes("ipCount")) {
          this.ipCount = true;
        } else {
          this.ipCount = false;
        }
        if (this.mergedArr.includes("avgPv")) {
          this.avgPv = true;
        } else {
          this.avgPv = false;
        }
        if (this.mergedArr.includes("avgVisitTime")) {
          this.avgVisitTime = true;
        } else {
          this.avgVisitTime = false;
        }
        if (this.mergedArr.includes("bounceRate")) {
          this.bounceRate = true;
        } else {
          this.bounceRate = false;
        }
        if (this.mergedArr.includes("pvRate")) {
          this.pvRate = true;
        } else {
          this.pvRate = false;
        }
        if (this.mergedArr.includes("newUvRate")) {
          this.newUvRate = true;
        } else {
          this.newUvRate = false;
        }
      }
    },
    handleSizeChange(val) {
      this.currentPage = 1;
      this.pageSize = val;
    },
    handleCurrentChange(val) {
      this.currentPage = val;
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep {
  @import "~@/styles/components/el-checkbox.scss";
  @import "~@/styles/components/el-pagination.scss";
}
.chartsIcon {
  box-sizing: border-box;
  margin: 20px;
  padding-top: 1px;
  min-height: 461px;
  background: rgba(250, 250, 251);
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
      line-height: 30px;
      margin-top: 12px;
      margin-left: 10px;
      .flow-title {
        white-space: nowrap;
        box-sizing: border-box;
        margin-right: 21px;
        font-size: 12px;
        font-weight: 400;
        line-height: 16px;
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
      // width: 30%;
      span {
        font-size: 14px;
        padding: 0 10px;
      }
      .checkBoxStyle {
        margin-right: 10px;
      }
    }
  }
}
</style>
