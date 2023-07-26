<template>
  <div class="chartsIcon">
    <div class="flow-indicator">
      <div class="flow-item">
        <div class="flow-title">流量基础指标</div>
        <el-checkbox-group
          v-model="channelList"
          class="checkBoxStyle"
          @change="handelChannelList"
        >
          <el-checkbox label="pv">浏览量(PV)</el-checkbox>
          <el-checkbox label="uv">访客数(UV)</el-checkbox>
          <el-checkbox label="ipCount" style="margin-right: 0"
            >IP数</el-checkbox
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
          <el-checkbox label="entryRate">入口页次数</el-checkbox>
          <el-checkbox label="devoteCount">贡献下游浏览量</el-checkbox>
          <el-checkbox label="exitCount">退出页次数</el-checkbox>
          <el-checkbox label="avgDuration">平均访问时长</el-checkbox>
          <el-checkbox label="exitRate">退出率</el-checkbox>
        </el-checkbox-group>
      </div>
    </div>
    <div class="table-content">
      <el-table
        :data="vistedTableData"
        border
        style="width: 100%"
        :header-cell-style="{ textAlign: 'center' }"
        :cell-style="{ textAlign: 'center' }"
      >
        <el-table-column type="index" label="序号" width="150" />
        <el-table-column prop="uri" label="页面URL" :show-overflow-tooltip="true" width="350" />
        <el-table-column label="流量基础指标">
          <el-table-column v-if="pv" prop="pv" label="浏览量(PV)" sortable />
          <el-table-column v-if="uv" prop="uv" label="访客数(UV)" sortable />
          <el-table-column
            v-if="ipCount"
            prop="ipCount"
            label="IP数"
            sortable
          />
        </el-table-column>
        <el-table-column prop="date" label="流量质量指标">
          <el-table-column
            v-if="devoteCount"
            prop="devoteCount"
            label="贡献下游浏览量"
            sortable
          />
          <el-table-column
            v-if="entryRate"
            prop="entryRate"
            label="入口页次数"
            sortable
          />
          <el-table-column
            v-if="exitCount"
            prop="exitCount"
            label="退出页次数"
            sortable
          />
          <el-table-column
            v-if="exitRate"
            prop="exitRate"
            label="退出率"
            sortable
          />
          <el-table-column
            v-if="avgDuration"
            prop="avgDuration"
            label="平均访问时长"
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
        :page-size="pageSize"
        layout=" sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import flowPoint from "@/components/flowPoint/index";
export default {
  components: {
    flowPoint,
  },
  data() {
    return {
      channelList: ["uv","ipCount","pv"],
      flowQuality: ["entryRate","avgDuration"],
      current: {
        size: 10,
        page: 1,
      },
      mergedArr: [],
      uri: false,
      uv: false,
      pv: false,
      ipCount: false,
      exitCount: false,
      avgDuration: false,
      exitRate: false,
      entryRate: false,
      devoteCount: false,
      currentPage: 1,
      vistedTableData: [],
      total: 0,
      pageSize: 10,
    };
  },
  methods: {
    vistedAnalysis(val) {
      // this.vistedTableData = val.detail;
      // this.total = val.detail.length;
      
      this.vistedTableData = val.rows;
      this.total = val.total;

      this.initShowTable()
    },
    // 分页器
    handleSizeChange(val) {
      // this.currentPage = 1;
      // this.pageSize = val;
      this.current.size = val;
      this.$emit("currentPage", this.current);
    },
    handleCurrentChange(val) {
      // this.currentPage = val;
      this.current.page = val;
      this.$emit("currentPage", this.current);
    },
    handelChannelList() {
      this.initShowTable();
    },
    handelFlowQuality() {
      this.initShowTable();
    },
    initShowTable() {
      this.mergedArr = [];
      this.mergedArr = [...this.channelList, ...this.flowQuality];
      this.flowPoint(this.mergedArr);
    },
    flowPoint(val) {
      if (val.length > 0) {
        if (val.includes("pv")) {
          this.pv = true;
        } else {
          this.pv = false;
        }
        if (val.includes("uri")) {
          this.uri = true;
        } else {
          this.uri = false;
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
        if (val.includes("exitCount")) {
          this.exitCount = true;
        } else {
          this.exitCount = false;
        }

        if (val.includes("avgDuration")) {
          this.avgDuration = true;
        } else {
          this.avgDuration = false;
        }
        if (val.includes("avgVisitTime")) {
          this.avgVisitTime = true;
        } else {
          this.avgVisitTime = false;
        }
        if (val.includes("exitRate")) {
          this.exitRate = true;
        } else {
          this.exitRate = false;
        }
        if (val.includes("devoteCount")) {
          this.devoteCount = true;
        } else {
          this.devoteCount = false;
        }
      }
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
      align-items: center;
      margin-top: 12px;
      margin-left: 10px;
      .flow-title {
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
