<template>
  <div class="chartsIcon">
    <div class="flow-indicator">
      <!-- <div class="flow-item">
        <div class="flow-title">流量基础指标</div>
        <el-checkbox-group v-model="channelList" class="checkBoxStyle">
          <el-checkbox label="1">浏览量(PV)</el-checkbox>
          <el-checkbox label="2">浏览量占比</el-checkbox>
          <el-checkbox label="3">访问次数</el-checkbox>
          <el-checkbox label="4">访客数(UV)</el-checkbox>
          <el-checkbox label="5">新访客数</el-checkbox>
          <el-checkbox label="6">新访客数占比</el-checkbox>
          <el-checkbox label="7" style="margin-right: 0">IP数</el-checkbox>
        </el-checkbox-group>
       
      </div>
      <div class="flow-item setSpace">
        <div class="flow-title">流量质量指标</div>
        <el-checkbox-group v-model="flowQuality" class="checkBoxStyle">
          <el-checkbox label="8">跳出率</el-checkbox>
          <el-checkbox label="9">平均访问时长</el-checkbox>
          <el-checkbox label="10">平均访问页数</el-checkbox>
        </el-checkbox-group>
      </div> -->
      <flowPoint ref="flowPoint" @flowPoint="flowPoint"></flowPoint>
    </div>

    <div class="table-content">
      <el-table
        :header-cell-style="{ textAlign: 'center' }"
        :cell-style="{ textAlign: 'center' }"
        :data="getSourceSiteList"
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="150" />
        <el-table-column
          prop="sourcesite"
          width="250"
          :show-overflow-tooltip="true"
          label="来源网站"
        />
        <el-table-column label="流量基础指标">
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
import { percentage } from "@/utils/percent";
export default {
  components: { flowPoint },
  data() {
    return {
      current: {
        size: 10,
        page: 1,
      },
      channelList: ["pv", "visitCount", "newUvRate", "pvRate"],
      flowQuality: ["avgPv"],
      total: 0,
      pageSize: 10,
      currentPage: 1,
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
      getSourceSiteList: [],
    };
  },
  methods: {
    getSourceSite(val) {
      val.rows.map((item) => {
        if (item.bounceRate) {
          item.bounceRate = percentage(item.bounceRate);
        }
        if (item.newUvRate) {
          item.newUvRate = percentage(item.newUvRate);
        }
        if (item.pvRate) {
          item.pvRate = percentage(item.pvRate);
        }
      });
      this.getSourceSiteList = val.rows;
      this.total = val.total;
    },
    handleSizeChange(val) {
      this.current.size = val;
      this.$emit("currentPage", this.current);
    },
    handleCurrentChange(val) {
      this.current.page = val;
      this.$emit("currentPage", this.current);
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
@import "~@/styles/components/el-pagination.scss";
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
      .el-checkbox {
        margin-right: 80px;
        font-size: 12px;
        font-weight: 400;
        line-height: 15px;
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
      // width: 30%;
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
