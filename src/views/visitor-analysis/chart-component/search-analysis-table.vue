<template>
  <div>
    <div class="search_wrappy">
      <div class="search_table">
        <span>搜索词分析</span>
        <div class="setTable">
          <el-table
            :header-cell-style="{ textAlign: 'center' }"
            :cell-style="{ textAlign: 'center' }"
            :data="searchTableList"
            border
            style="width: 100%; margin-top: 12px"
          >
            <!-- <el-table-column prop="statTime" label="日期" sortable width="180">
            </el-table-column> -->
            <el-table-column prop="searchword" :show-overflow-tooltip="true" label="搜索词" sortable width="250">
            </el-table-column>
            <el-table-column prop="pv" label="浏览量(PV)" sortable>
            </el-table-column>
            <el-table-column prop="pvRate" label="占比" sortable>
            </el-table-column>
            <el-table-column prop="avgVisitTime" label="平均访问时长" sortable>
            </el-table-column>
            <el-table-column prop="avgPv" label="平均访问页数" sortable>
            </el-table-column>
            <el-table-column prop="bounceRate" label="跳出率" sortable>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
    <div class="block">
      <el-pagination
        :pager-count="5"
        prev-text
        next-text="下一页"
        :current-page="currentPage"
        :page-sizes="[10, 20, 30, 40]"
        :page-size= pageSize
        layout=" sizes, prev, pager, next, jumper"
        :total= total
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { percentage } from "@/utils/percent";
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
      },
    };
  },
  created() {},
  methods: {
    percentageFun(val) {
      return percentage(val);
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
@import "~@/styles/components/el-pagination.scss";
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
.search_wrappy {
  position: relative;
  margin: 15px;
  background-color: #fafafb;
  // min-height: 647px;
  min-height: 447px;
}
.search_table {
  height: 100%;
  padding: 18px 22px;
  span {
    font-size: 13px;
    font-weight: 500;
    line-height: 31px;
    color: #4d4d4d;
  }
}
</style>
