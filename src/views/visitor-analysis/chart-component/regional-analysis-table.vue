<template>
  <div class="chartsIcon public-hoverItem">
    <div class="public-firstHead">地域分析</div>
    <flowPoint ref="flowPoint" @flowPoint="flowPoint"></flowPoint>
    <div>
      <div class="public-Table-minHeight">
        <el-table
          class="public-radius"
          :data="tableDetailList"
          border
          :header-cell-style="{ textAlign: 'center', background: '#f7fafe ' }"
          :cell-style="{ textAlign: 'center' }"
          @sort-change="sortChange($event)"
        >
          <el-table-column label="序号" type="index" width="80" align="center">
            <template slot-scope="scope">
              <span v-text="getIndex(scope.$index)"> </span>
            </template>
          </el-table-column>
          <el-table-column
            prop="province"
            :show-overflow-tooltip="true"
            label="地域"
            width="200"
          />
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
            />
            <el-table-column
              v-if="visit"
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
            />
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
            />
            <el-table-column
              v-if="avgVisitTime"
              prop="avgVisitTime"
              label="平均访问时长"
              sortable="custom"
            />
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
      <div class="block">
        <el-pagination
          next-text="下一页"
          :current-page.sync="currentPage"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { percentage, averageRules } from "@/utils/percent";
import { formatTime } from "@/utils/format";
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
      flowTableList: [],
      channelList: ["pv", "visit", "newUvRate", "pvRate"],
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
      current: {
        size: 10,
        page: 1,
        sortName: null,
        sortOrder: null,
      },
    };
  },
  methods: {
    averageRulesEvent(num) {
      return averageRules(num);
    },
    getIndex($index) {
      return (this.currentPage - 1) * this.pageSize + $index + 1;
    },
    sortChange(e) {
      if (e.order && e.order == "ascending") {
        // 降序
        this.current.sortName = e.prop;
        this.current.sortOrder = "asc";
        this.$emit("currentPage", this.current);
      } else if (e.order && e.order == "descending") {
        // 升序
        this.current.sortName = e.prop;
        this.current.sortOrder = "desc";
        this.$emit("currentPage", this.current);
      } else {
        this.current.sortName = null;
        this.current.sortOrder = null;
        this.$emit("currentPage", this.current);
      }
    },
    initCurrentPage() {
      this.currentPage = 1;
    },
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
            if (
              item.province == "未知省份" ||
              (item.country && item.country != "中国")
            ) {
              item.province = item.country + "-" + item.province;
            }
            if (item.avgVisitTime || item.avgVisitTime == 0) {
              item.avgVisitTime = formatTime(Math.floor(item.avgVisitTime));
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
  padding-top: 1px;
  min-height: 461px;
  background: #fff;
  border-radius: 6px;
  padding: 22px;
}
</style>
