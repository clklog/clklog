<template>
  <div>
    <div class="block-head">
      <div class="block-title">
        访问日志
        <div class="innerFont">(近24小时)</div>
      </div>
      <div class="block-head-icon"></div>
    </div>
    <div style="width: 100%;min-height: 400px;">
      <el-table
        style="width: 100%"
        :data="tableData"
        class="public-radius"
        :header-cell-style="{
          textAlign: 'center',
          background: '#f7fafe ',
        }"
        :cell-style="tableHeaderColor"
        border
      >
        <el-table-column type="index" label="序号" width="100px" />
        <el-table-column
          prop="province"
          min-width="25%"
          :show-overflow-tooltip="true"
          label="访问信息"
        >
        </el-table-column>
        <el-table-column
          prop="pv"
          label="访问设备"
          min-width="25%"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          prop="pvRate"
          label="访问页面"
          min-width="25%"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          prop="pvRate"
          label="访客资料"
          min-width="23%"
          :show-overflow-tooltip="true"
        />
      </el-table>
    </div>
    <div class="block">
      <el-pagination
        next-text="下一页"
        :current-page.sync="currentPage"
        :page-sizes="[10, 20, 30, 40]"
        :page-size.sync="pageSize"
        layout=" total,sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      total: 0,
      pageSize: 10,
      currentPage: 1,
      tableData: [
        {
          pv: "2016-05-02",
          province: "上海市普陀区金沙江路 1518 弄",
          pvRate: "30%",
        },
        {
          pv: "2016-05-04",
          province: "上海市普陀区金沙江路 1518 弄",
          pvRate: "30%",
        },
        {
          pv: "2016-05-01",

          province: "上海市普陀区金沙江路 1518 弄",
          pvRate: "30%",
        },
        {
          pv: "2016-05-03",
          province: "上海市普陀区金沙江路 1518 弄",
          pvRate: "30%",
        },
      ],
    };
  },
  methods: {
    tableHeaderColor({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 1) {
        return "text-align:left";
      } else {
        return "text-align:center";
      }
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep {
  @import "~@/styles/components/el-pagination.scss";
}
.innerFont {
  font-size: 13px;
  font-weight: 400;
  color: #4d4d4d;
  margin-left: 8px;
}
</style>
