<template>
  <div class="chartsIcon">
    <!-- <div class="flow-indicator">
      <div class="flow-item">
        <div class="flow-title">流量基础指标</div>
        <el-checkbox-group v-model="channelList" class="checkBoxStyle">
          <el-checkbox label="1">浏览量(PV)</el-checkbox>
          <el-checkbox label="2">浏览量占比</el-checkbox>
          <el-checkbox label="3">访问次数</el-checkbox>
          <el-checkbox label="4">访客数(UV)</el-checkbox>
          <el-checkbox label="5">新访客数</el-checkbox>
          <el-checkbox label="6">新访客比率</el-checkbox>
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
      </div>
    </div> -->
    <flowPoint ref="flowPoint" @flowPoint="flowPoint"></flowPoint>
    <div class="table-content">
      <el-table
        :data="tableDetailList"
        border
        :header-cell-style="{ textAlign: 'center' }"
        :cell-style="{ textAlign: 'center' }"
      >
        <el-table-column prop="province" label="地域" width="150" />
        <!-- <el-table-column prop="date" label="流量基础指标" width="150">
          <el-table-column prop="pv" label="浏览量(PV)" sortable />
          <el-table-column prop="pvRate" label="浏览量占比" sortable />
          <el-table-column prop="visit" label="访问次数" sortable />
          <el-table-column prop="newUv" label="新访客数" sortable />
          <el-table-column prop="uv" label="访客数(UV)" sortable />
          <el-table-column prop="newUvRate" label="新访客比率" sortable />
          <el-table-column prop="ipCount" label="IP数" sortable />
        </el-table-column>
        <el-table-column prop="date" label="流量质量指标" width="150">
          <el-table-column prop="bounceRate" label="跳出率" sortable />
          <el-table-column prop="avgVisitTime" label="平均访问时长" sortable />
          <el-table-column prop="avgPv" label="平均访问页数" sortable />
        </el-table-column> -->
        <el-table-column prop="date" label="流量基础指标" width="150">
          <el-table-column v-if="pv" prop="pv" label="浏览量(PV)" sortable />
          <el-table-column
            v-if="pvRate"
            prop="pvRate"
            label="浏览量占比"
            sortable
          />
          <el-table-column
            v-if="visit"
            prop="visit"
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
import { percentage } from "@/utils/percent";
import flowPoint from "@/components/flowPoint/index";
import { getAreaDetailListApi } from "@/api/trackingapi/area.js";
export default {
  components: { flowPoint },
  data() {
    return {
      // channelList: ["3", "5"],
      // flowQuality: ["9"],
      currentPage: 1,
      tableDetailList: [],
      total: 0,
      pageSize: 10,
      current: {
        size: 10,
        page: 1,
      },

      // 后面拆分使用
      flowTableList: [],
      channelList: ["pv", "visit","newUvRate","pvRate"],
      flowQuality: ["avgPv"],
      pv: false,
      visit: false,
      newUv: false,
      uv: false,
      ipCount: false,
      avgPv: false,
      avgVisitTime: false,
      bounceRate: false,
      pvRate: false,
      newUvRate: false,
      mergedArr: [],
    };
  },
  methods: {
    flowPoint(val) {
      if (val.length > 0) {
        if (val.includes("pv")) {
          this.pv = true;
        } else {
          this.pv = false;
        }
        if (val.includes("visitCount")) {
          this.visit = true;
        } else {
          this.visit = false;
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
    // 分页器
    handleSizeChange(val) {
      this.current.size = val;
      this.$emit("currentPage", this.current);
    },
    handleCurrentChange(val) {
      this.current.page = val;
      this.$emit("currentPage", this.current);
    },
    getDetailList(val) {
      getAreaDetailListApi(val).then((res) => {
        if (res.code == 200) {
          this.tableDetailList = res.data.rows;
          this.total = res.data.total;
          this.tableDetailList.map((item) => {
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
        }
      });
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
}
</style>
