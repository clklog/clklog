<template>
  <div>
    <div class="search_wrappy public-table-block public-hoverItem">
      <span class="public-firstHead">站外搜索词分析</span>
      <div class="public-Table-minHeight">
        <el-table
          class="public-radius"
          :header-cell-style="{ background: '#f7fafe ' }"
          :cell-style="tableHeaderColor"
          :data="searchTableList"
          border
          @sort-change="sortChange($event)"
          style="width: 100%; margin-top: 12px"
        >
          <el-table-column label="序号" type="index" width="80" align="center">
            <template slot-scope="scope">
              <span v-text="getIndex(scope.$index)"> </span>
            </template>
          </el-table-column>

          <el-table-column
            prop="searchword"
            align="left"
            :show-overflow-tooltip="true"
            label="搜索词"
            width="250"
          >
          </el-table-column>
          <el-table-column
            prop="pv"
            align="center"
            label="搜索次数"
            sortable="custom"
          >
          </el-table-column>
          <el-table-column
            align="center"
            label="搜索次数占比"
            sortable="custom"
            prop="pvRate"
          >
            <template slot-scope="scope"> {{ scope.row.pvRate }}</template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="visitCount"
            label="访问次数"
            sortable="custom"
          >
          </el-table-column>
          <el-table-column
            align="center"
            prop="visitCountRate"
            label="访问次数占比"
            sortable="custom"
          >
            <template slot-scope="scope">
              {{ scope.row.visitCountRate }}
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="uv"
            label="访客数"
            sortable="custom"
          >
          </el-table-column>
          <el-table-column
            align="center"
            prop="newUv"
            label="新访客数"
            sortable="custom"
          >
          </el-table-column>
          <el-table-column
            align="center"
            prop="ipCount"
            label="IP数"
            sortable="custom"
          >
          </el-table-column>
          <el-table-column
            align="center"
            prop="ipCountRate"
            label="IP数占比"
            sortable="custom"
          >
            <template slot-scope="scope">
              {{ scope.row.ipCountRate }}
            </template>
          </el-table-column>
          <el-table-column
            prop="bounceRate"
            align="center"
            label="跳出率"
            sortable="custom"
          >
            <template slot-scope="scope">
              {{ scope.row.bounceRate }}
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="avgVisitTime"
            label="平均访问时长"
            sortable="custom"
          >
          </el-table-column>
          <el-table-column
            align="center"
            prop="avgPv"
            label="平均访问页数"
            sortable="custom"
          >
            <template slot-scope="scope">
              {{ averageRulesEvent(scope.row.avgPv) }}
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
import { percent, percentage, averageRules } from "@/utils/percent";
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
    averageRulesEvent(num) {
      return averageRules(num);
    },
    getIndex($index) {
      return (this.currentPage - 1) * this.pageSize + $index + 1;
    },
    tableHeaderColor({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 1) {
        return "text-align:left";
      } else {
        return "text-align:center";
      }
    },
    percentageFun(val) {
      return percentage(val);
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
        if (item.avgVisitTime || item.avgVisitTime == 0) {
          item.avgVisitTime = formatTime(Math.floor(item.avgVisitTime));
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
