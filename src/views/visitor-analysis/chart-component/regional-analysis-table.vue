<template>
  <div class="chartsIcon">
    <flowPoint ref="flowPoint" @flowPoint="flowPoint"></flowPoint>
    <div class="table-content">
      <el-table
        :data="tableDetailList"
        border
        :header-cell-style="{ textAlign: 'center' }"
        :cell-style="{ textAlign: 'center' }"
      >
        <el-table-column prop="province" :show-overflow-tooltip="true" label="地域" width="200" />
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
      currentPage: 1,
      tableDetailList: [],
      total: 0,
      pageSize: 10,
      current: {
        size: 10,
        page: 1,
      },
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
            if (item.province == "未知省份") {
              item.province = item.country + "-" + item.province
            }
          });
        }
      });
    },
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
