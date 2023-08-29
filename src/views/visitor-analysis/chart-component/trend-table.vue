<template>
  <div class="chartsIcon public-table-block public-hoverItem">
    <div class="public-firstHead">趋势分析</div>
    <flowPoint ref="flowPoint" @flowPoint="flowPoint"></flowPoint>
    <!-- class="public-table-block" -->
    <div>
      <div class="public-Table-minHeight">
        <el-table
          class="public-radius"
          :header-cell-style="{ textAlign: 'center', background: '#f4f8fe' }"
          :cell-style="{ textAlign: 'center' }"
          :data="
            flowTableList.slice(
              (currentPage - 1) * pageSize,
              currentPage * pageSize
            )
          "
          @sort-change="sortChange($event)"
          border
          style="width: 100%"
        >
          <el-table-column label="日期" prop="statTime" sortable>
            <template slot-scope="scope">
              {{ scope.row.statTime }}
              <!-- <span v-if="timeType && timeType == 'hour'">时</span> -->
            </template>
          </el-table-column>
          <el-table-column prop="date" label="流量基础指标" width="150">
            <el-table-column v-if="pv" prop="pv" label="浏览量(PV)" sortable />
            <el-table-column
              v-if="pvRate"
              label="浏览量占比"
              prop="pvRate"
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
          <!-- :sort-method="
                (a, b) => {
                  return a.bounceRate - b.bounceRate;
                }
              " -->
          <el-table-column prop="date" label="流量质量指标" width="150">
            <el-table-column
              v-if="bounceRate"
              prop="bounceRate"
              label="跳出率"
              sortable
            >
              <template slot-scope="scope">
                {{ scope.row.bounceRate }}%
              </template>
            </el-table-column>
            <el-table-column
              v-if="avgVisitTime"
              label="平均访问时长"
              sortable
              prop="avgVisitTime"
            >
              <template slot-scope="scope">
                {{ avgTimeEvent(scope.row.avgVisitTime) }}
              </template>
            </el-table-column>
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
        layout="total, sizes, prev, pager, next, jumper"
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
      timeType: null,
    };
  },
  mounted() {},
  methods: {
    avgTimeEvent(val) {
      return formatTime(Math.floor(val));
    },
    sortChange(column) {
      this.pageNum = 1; // return to the first page after sorting
      this.total = this.flowTableList.length;
      this.flowTableList = this.flowTableList.sort(
        this.sortFun(column.prop, column.order === "ascending")
      );
    },
    sortFun(attr, rev) {
      if (rev == undefined) {
        rev = 1;
      } else {
        rev = rev ? 1 : -1;
      }
      return function (a, b) {
        a = a[attr];
        b = b[attr];
        if (a < b) {
          return rev * -1;
        }
        if (a > b) {
          return rev * 1;
        }
        return 0;
      };
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
      return percent(val);
    },
    apiDetailList(val, time) {
      this.timeType = time;
      this.currentPage = 1;
      this.flowTableList = val;
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
        if (item.avgPv) {
          item.avgPv = Math.floor(item.avgPv);
        }
      });
      // this.total = val.detail.length;
      this.total = val.length;
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
</style>
