<template>
  <div class="chartsIcon public-table-block public-hoverItem">
    <div class="public-firstHead">趋势分析</div>
    <flowPoint ref="flowPoint" @flowPoint="flowPoint"></flowPoint>
    <div>
      <div class="public-Table-minHeight">
        <el-table
          class="public-radius"
          :header-cell-style="{ textAlign: 'center', background: '#f7fafe ' }"
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
          <el-table-column label="日期" prop="statTime" sortable="custom">
            <template slot-scope="scope">
              <div style="display: flex;justify-content: center;">
                {{ scope.row.statTime }}
                <div v-if="scope.row.statTime">
                  {{ timeType == "hour" ? "时" : "" }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="date" label="流量基础指标" width="150">
            <el-table-column
              v-if="pv"
              prop="pv"
              label="浏览量"
              sortable="custom"
            />
            <el-table-column
              v-if="pvRate"
              prop="pvRate"
              label="浏览量占比"
              sortable="custom"
            >
              <template slot-scope="scope">
                {{ percentageFun(scope.row.pvRate) }}</template
              >
            </el-table-column>
            <el-table-column
              v-if="visitCount"
              prop="visitCount"
              label="访问次数"
              sortable="custom"
            />
            <el-table-column
              v-if="uv"
              prop="uv"
              label="访客数"
              sortable="custom"
            />
            <el-table-column
              v-if="newUv"
              prop="newUv"
              label="新访客数"
              sortable="custom"
            />
            <el-table-column
              v-if="newUvRate"
              prop="newUvRate"
              label="新访客数占比"
              sortable="custom"
            >
              <template slot-scope="scope">
                {{ percentageFun(scope.row.newUvRate) }}
              </template>
            </el-table-column>

            <el-table-column
              v-if="ipCount"
              prop="ipCount"
              label="IP数"
              sortable="custom"
            />
          </el-table-column>
          <el-table-column prop="date" label="流量质量指标" width="150">
            <el-table-column
              v-if="bounceRate"
              prop="bounceRate"
              label="跳出率"
              sortable="custom"
            >
              <template slot-scope="scope">
                {{ percentageFun(scope.row.bounceRate) }}
              </template>
            </el-table-column>
            <el-table-column
              v-if="avgVisitTime"
              label="平均访问时长"
              sortable="custom"
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
              sortable="custom"
            >
              <template slot-scope="scope">
                {{ averageRulesEvent(scope.row.avgPv) }}
              </template>
            </el-table-column>
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
import { copyObj } from "@/utils/copy";
import flowPoint from "@/components/flowPoint/index";
import { percent, percentage, averageRules } from "@/utils/percent";
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
      current: {
        sortName: "",
        sortOrder: "",
      },
    };
  },
  mounted() {},
  methods: {
    avgTimeEvent(val) {
      return formatTime(Math.floor(val));
    },
    ascDscEvent() {
      let nameAttr = this.current.sortName;
      if (nameAttr && this.current.sortOrder === "ascending") {
        this.flowTableList = this.flowTableList.sort(function (a, b) {
          return a[nameAttr] - b[nameAttr];
        });
      } else if (nameAttr && this.current.sortOrder === "descending") {
        this.flowTableList = this.flowTableList.sort(function (a, b) {
          return b[nameAttr] - a[nameAttr];
        });
      }
    },
    sortChange(e) {
      if (e.order && e.order == "ascending") {
        // 降序
        this.current.sortName = e.prop;
        this.current.sortOrder = "ascending";
      } else if (e.order && e.order == "descending") {
        // 升序
        this.current.sortName = e.prop;
        this.current.sortOrder = "descending";
      } else {
        this.current.sortName = null;
        this.current.sortOrder = null;
        this.$emit("currentPage");
      }
      if (this.current.sortName == "statTime") {
        this.$emit("currentPage");
      }
      this.ascDscEvent();
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
    averageRulesEvent(num) {
      return averageRules(num);
    },
    percentageFun(val) {
      if (val == 0) {
        return 0;
      } else {
        return percentage(val);
      }
    },
    apiDetailList(val, time, commonParams) {
      this.timeType = time;
      this.currentPage = 1;
      let toDateTime = this.$options.filters.formatDateToday();
      if (
        time == "hour" &&
        commonParams.endTime == commonParams.startTime &&
        commonParams.startTime == toDateTime
      ) {
        let currentHout = this.$options.filters.getCurrentHour();
        let newArray = copyObj(val).reverse();
        const index = newArray.findIndex(
          (item) => item.statTime == currentHout
        );
        const result = index !== -1 ? newArray.slice(index) : [];
        this.flowTableList = result;
      } else {
        this.flowTableList = val.reverse();
      }

      if (this.current.sortName && this.current.sortName == "statTime") {
        if (this.current.sortOrder == "descending") {
          this.flowTableList = this.flowTableList.reverse();
        }
      }
      this.total = this.flowTableList.length;
      this.ascDscEvent();
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
