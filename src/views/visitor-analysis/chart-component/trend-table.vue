<template>
  <div class="chartsIcon public-table-block public-hoverItem">
    <flowPoint ref="flowPoint" @flowPoint="flowPoint"></flowPoint>
    <div class="public-table-block">
      <div class="public-Table-minHeight">
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
          <el-table-column sortable prop="statTime" label="日期" width="200" />
          <el-table-column prop="date" label="流量基础指标" width="150">
            <el-table-column v-if="pv" prop="pv" label="浏览量(PV)" sortable />
            <el-table-column
              label="浏览量占比"
              sortable
              :sort-method="
                (a, b) => {
                  return a.pvRate - b.pvRate;
                }
              "
            >
              <template slot-scope="scope"> {{ scope.row.pvRate }}% </template>
            </el-table-column>

            <el-table-column
              v-if="visitCount"
              prop="visitCount"
              label="访问次数"
              sortable
            />
            <el-table-column v-if="uv" prop="uv" label="访客数(UV)" sortable />
            <el-table-column
              v-if="newUv"
              prop="newUv"
              label="新访客数"
              sortable
            />
            <el-table-column
              v-if="newUvRate"
              prop="newUvRate"
              label="新访客数占比"
              sortable
              :sort-method="
                (a, b) => {
                  return a.newUvRate - b.newUvRate;
                }
              "
            >
              <template slot-scope="scope">
                {{ scope.row.newUvRate }}%
              </template>
            </el-table-column>

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
              :sort-method="
                (a, b) => {
                  return a.bounceRate - b.bounceRate;
                }
              "
            >
              <template slot-scope="scope">
                {{ scope.row.bounceRate }}%
              </template>
            </el-table-column>
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
import { percent } from "@/utils/percent";
import { formatTime } from "@/utils/format";
export default {
  components: { flowPoint },
  data() {
    return {
      flowTableList: [],
      channelList: ["pv", "visitCount", "newUvRate", "pvRate"],
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
      currentPage: 1,
      total: 0,
      pageSize: 10,
    };
  },
  mounted() {
    this.initShowTable();
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
    handelChannelList() {
      this.initShowTable();
    },
    handelFlowQuality() {
      this.initShowTable();
    },
    percentageFun(val) {
      return percent(val);
    },
    apiDetailList(val) {
      this.currentPage = 1;
      this.flowTableList = val.detail;
      this.flowTableList.map((item) => {
        if (item.bounceRate) {
          item.bounceRate = this.percentageFun(item.bounceRate);
        }
        if (item.newUvRate) {
          item.newUvRate = this.percentageFun(item.newUvRate);
        }
        if (item.pvRate) {
          item.pvRate = this.percentageFun(item.pvRate);
        }
        if (item.avgVisitTime) {
          item.avgVisitTime = formatTime(Math.floor(item.avgVisitTime));
        }
        if (item.avgPv) {
          item.avgPv = Math.floor(item.avgPv);
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
// .chartsIcon {
//   box-sizing: border-box;
//   margin: 20px 0;
//   padding-top: 1px;
//   min-height: 461px;
//   background: #fff;
//   border-radius: 6px;
// }
</style>
