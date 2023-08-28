<template>
  <div>
    <div class="chartsIcon">
      <div class="public-firstHead">设备分析</div>
      <flowPoint ref="flowPoint" @flowPoint="flowPoint"></flowPoint>
      <div>
        <div class="public-Table-minHeight public-hoverItem">
          <el-table
            :data="equipmentList"
            :header-cell-style="{ textAlign: 'center' }"
            :cell-style="{ textAlign: 'center' }"
            border
            class="public-radius"
            @sort-change="sortChange($event)"
            style="width: 100%"
          >
            <!-- <el-table-column type="index" label="序号" width="150" /> -->
            <el-table-column
              label="序号"
              type="index"
              width="150"
              align="center"
            >
              <template slot-scope="scope">
                <span v-text="getIndex(scope.$index)"> </span>
              </template>
            </el-table-column>
            <el-table-column prop="device" label="设备类型" width="150" />

            <el-table-column prop="date" label="流量基础指标">
              <el-table-column
                v-if="pv"
                prop="pv"
                label="浏览量(PV)"
                sortable
              />
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
              <el-table-column
                v-if="uv"
                prop="uv"
                label="访客数(UV)"
                sortable
              />
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
            :current-page.sync="currentPage"
            :page-sizes="[10, 20, 30, 40]"
            :page-size="pageSize"
            layout=" total,sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import flowPoint from "@/components/flowPoint/index";
import { percentage } from "@/utils/percent";
import { formatTime } from "@/utils/format";
export default {
  components: { flowPoint },
  data() {
    return {
      channelList: ["pv", "visitCount", "newUvRate", "pvRate"],
      flowQuality: ["avgPv"],
      equipmentList: [],
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
      current: {
        size: 10,
        page: 1,
        sortName: null,
        sortOrder: null,
      },
      currentPage: 1,
      total: 0,
      pageSize: 10,
    };
  },
  methods: {
    getIndex($index) {
      return (
        (this.currentPage - 1) * this.pageSize + $index + 1
      );
    },
    equipmentTableEvent(val) {
      this.equipmentList = val.rows;
      this.equipmentList.map((item) => {
        if (item.bounceRate) {
          item.bounceRate = percentage(item.bounceRate);
        }
        if (item.newUvRate) {
          item.newUvRate = percentage(item.newUvRate);
        }
        if (item.pvRate) {
          item.pvRate = percentage(item.pvRate);
        }
        if (item.avgVisitTime) {
          item.avgVisitTime = formatTime(Math.floor(item.avgVisitTime));
        }
        if (item.avgPv) {
          item.avgPv = Math.floor(item.avgPv);
        }
      });
      this.total = val.total;
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
    handleSizeChange(val) {
      this.current.size = val;
      this.$emit("currentPage", this.current);
    },
    handleCurrentChange(val) {
      this.current.page = val;
      this.$emit("currentPage", this.current);
    },
    initCurrentPage() {
      this.currentPage = 1;
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
::v-deep {
  @import "~@/styles/components/el-pagination.scss";
}
.chartsIcon {
  box-sizing: border-box;
  margin: 20px;
  padding-top: 1px;
  min-height: 461px;
  background: #fff;
  border-radius: 6px;
  padding: 22px;
}
</style>
