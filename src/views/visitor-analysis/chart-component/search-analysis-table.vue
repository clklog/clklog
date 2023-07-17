<template>
  <div>
    <div class="search_wrappy">
      <div class="search_table">
        <span>搜索词分析</span>
        <div class="setTable">
          <el-table
            :header-cell-style="{ textAlign: 'center' }"
            :cell-style="{ textAlign: 'center' }"
            :data="searchWordDetailList"
            style="width: 100%; margin-top: 12px; height: 98%"
            :default-sort="{ prop: 'date', order: 'descending' }"
          >
            <el-table-column prop="statDate" label="日期" sortable width="180">
            </el-table-column>
            <el-table-column prop="word" label="搜索词" sortable width="180">
            </el-table-column>
            <el-table-column prop="pv" label="浏览量(PV)" sortable>
            </el-table-column>
            <el-table-column prop="pvRate" label="占比" sortable>
            </el-table-column>
            <el-table-column prop="avgPv" label="平均访问时长" sortable>
            </el-table-column>
            <el-table-column prop="avgVisitTime" label="平均访问页数" sortable>
            </el-table-column>
            <el-table-column prop="bounceRate" label="跳出率" sortable>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
    <div class="block">
      <el-pagination
        hide-on-single-page
        :pager-count="5"
        prev-text
        next-text="下一页"
        :current-page="currentPage4"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="10"
        layout=" sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { getSearchWordDetailApi } from "@/api/trackingapi/searchword";
export default {
  data() {
    return {
      currentPage4: 4,
      tableData: [
        {
          date: "2023/5/30",
          word: "女士衣服",
          visitor: "86",
          user: "15%",
          timeLegth: "695",
          pageNum: "56",
          out: "61",
        },
        {
          date: "2023/5/30",
          word: "女士衣服",
          visitor: "86",
          user: "15%",
          timeLegth: "695",
          pageNum: "56",
          out: "61",
        },
        {
          date: "2023/5/31",
          word: "NA士衣服",
          visitor: "86",
          user: "15%",
          timeLegth: "69",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/3",
          word: "衣服",
          visitor: "86",
          user: "15%",
          timeLegth: "695",
          pageNum: "86",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
        {
          date: "2023/5/13",
          word: "士衣服",
          visitor: "186",
          user: "151%",
          timeLegth: "695",
          pageNum: "856",
          out: "61",
        },
      ],
      searchWordDetailList: null,
      total: null,
    };
  },
  created() {
    // this.getSearchWordDetail();
  },
  methods: {
    // 关键词详情页
    getSearchWordDetail() {
      // 测试数据
      const params = {
        pageNum: 1,
        pageSize: 50,
        startTime: "",
        endTime: "",
      };
      getSearchWordDetailApi(params).then((res) => {
        if (res.code == 200) {
          this.searchWordDetailList = res.data.rows;
          this.total = res.data.total;
        }
      });
    },
    formatter(row, column) {
      return row.address;
    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
    },
  },
};
</script>
<style lang="scss">
.setTable {
  .el-table--border {
    border: none;
  }
  .el-table th {
    background-color: transparent !important;
  }

  .el-table,
  .el-table__expanded-cell {
    background-color: transparent;
  }

  .setTable .el-table th,
  .el-table tr {
    background-color: transparent;
  }
}
</style>

<style lang="scss" scoped>
.search_wrappy {
  position: relative;
  margin: 15px;
  background-color: #fafafb;
  min-height: 647px;
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
  .block {
    margin: 20px 12px;
  }
}
::v-deep {
  .el-pagination {
    position: relative;
    width: 100%;
    display: flex;
    justify-content: flex-end;
    padding-right: 20px;
  }
  .el-pagination__jump {
    position: absolute;
    left: 0;
  }
}
</style>
