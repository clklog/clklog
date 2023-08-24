<template>
  <div>
    <div class="search_wrappy public-table-block public-hoverItem">
      <!-- <div class="search_table"> -->
      <span class="public-firstHead">搜索词分析</span>
      <div class="public-Table-minHeight">
        <el-table
          :header-cell-style="{ textAlign: 'center', background: '#F5F7FA' }"
          :cell-style="{ textAlign: 'center' }"
          :data="searchTableList"
          border
          @sort-change="sortChange($event)"
          style="width: 100%; margin-top: 12px"
        >
          <!-- <el-table-column prop="statTime" label="日期" sortable width="180">
            </el-table-column> -->
          <el-table-column
            prop="searchword"
            :show-overflow-tooltip="true"
            label="搜索词"
            width="250"
          >
          </el-table-column>
          <el-table-column
            prop="pv"
            label="浏览量(PV)"
            :sort-orders="['descending', 'ascending']"
            :sortable="true"
          >
          </el-table-column>
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
          <!-- “访问次数”，“访问次数占比”，“访客数”，“新访客数”， “IP数”，“IP数占比” -->
          <el-table-column prop="visitCount" label="访问次数" sortable>
          </el-table-column>
          <el-table-column prop="visitCountRate" label="访问次数占比" sortable>
          </el-table-column>
          <el-table-column prop="uv" label="访客数" sortable>
          </el-table-column>
          <el-table-column prop="newUv" label="新访客数" sortable>
          </el-table-column>
          <el-table-column prop="ipCount" label="IP数" sortable>
          </el-table-column>
          <el-table-column prop="ipCountRate" label="IP数占比" sortable>
          </el-table-column>

          <el-table-column prop="avgVisitTime" label="平均访问时长" sortable>
          </el-table-column>
          <el-table-column prop="avgPv" label="平均访问页数" sortable>
          </el-table-column>
          <el-table-column
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
        </el-table>
      </div>
      <div class="block">
        <el-pagination
          :pager-count="5"
          prev-text
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
    <!-- </div> -->
  </div>
</template>


<script>
import { percent } from "@/utils/percent";
import { formatTime } from "@/utils/format";
export default {
  data() {
    return {
      currentPage: 1,
      searchTableList: [],
      total: null,
      total: 0,
      pageSize: 10,
      current: {
        size: 10,
        page: 1,
        sortName: null,
        sortOrder: null,
      },
    };
  },
  created() {},
  methods: {
    percentageFun(val) {
      return percent(val);
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
    searchTable(val) {
      this.currentPage = 1;
      this.searchTableList = val.rows;
      this.searchTableList.map((item) => {
        if (item.bounceRate) {
          item.bounceRate = this.percentageFun(item.bounceRate);
        }
        if (item.pvRate) {
          item.pvRate = this.percentageFun(item.pvRate);
        }
        if (item.visitCountRate) {
          item.visitCountRate = this.percentageFun(item.visitCountRate);
        }
        if (item.ipCountRate) {
          item.ipCountRate = this.percentageFun(item.ipCountRate);
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
    formatter(row, column) {
      return row.address;
    },
    handleSizeChange(val) {
      this.current.size = val;
      this.$emit("currentPage", this.current);
    },
    handleCurrentChange(val) {
      this.current.page = val;
      this.$emit("currentPage", this.current);
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep {
  @import "~@/styles/components/el-pagination.scss";
}
.search_wrappy {
  position: relative;
}
.setTable {
  min-height: 410px;
}
// .search_table {
//   // min-height: 410px;
//   padding: 18px 22px;
// }
// ::v-deep {
//   .setTable {
//     .el-table--border {
//       border: none;
//     }
//     .el-table th {
//       background-color: transparent !important;
//     }

//     .el-table,
//     .el-table__expanded-cell {
//       background-color: transparent;
//     }

//     .setTable .el-table th,
//     .el-table tr {
//       background-color: transparent;
//     }
//     .el-table th.is-leaf, .el-table td{
//       border: 0 !important;
//     }
//     .el-table::before{
//       height: 0;
//     }
//   }
// }
</style>
