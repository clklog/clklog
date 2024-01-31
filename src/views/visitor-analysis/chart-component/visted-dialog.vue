<template>
  <div>
    <!-- title="资源路径Top10" -->
    <el-dialog
      title="访问页面分析"
      :visible.sync="dialogTableVisible"
      width="80%"
    >
      <el-table :data="gridData" border  class="public-radius">
        <el-table-column label="序号" type="index" width="80" align="center">
          <template slot-scope="scope">
            <span v-text="getIndex(scope.$index)"> </span>
          </template>
        </el-table-column>
        <el-table-column
          label="页面URL"
          width="400"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            {{ scope.row.title }}<br />
            {{ scope.row.uri }}
          </template>
        </el-table-column>
        <el-table-column label="流量基础指标">
          <el-table-column prop="pv" label="浏览量" sortable="custom" />
          <el-table-column prop="uv" label="访客数" sortable="custom" />
          <el-table-column prop="ipCount" label="IP数" sortable="custom" />
        </el-table-column>
        <el-table-column prop="date" label="流量质量指标">
          <el-table-column
            prop="entryCount"
            label="入口页次数"
            sortable="custom"
          />
          <el-table-column
            prop="downPvCount"
            label="贡献下游浏览量"
            sortable="custom"
          />
          <el-table-column
            prop="exitCount"
            label="退出页次数"
            sortable="custom"
          />
          <el-table-column
            prop="avgVisitTime"
            label="平均访问时长"
            sortable="custom"
          >
            <template slot-scope="scope">
              {{ scope.row.avgVisitTime | formatTime }}
            </template>
          </el-table-column>
          <el-table-column prop="exitRate" label="退出率" sortable="custom">
            <template slot-scope="scope">
              {{ scope.row.exitRate | percenTable }}
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          next-text="下一页"
          :current-page.sync="pageNum"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getVisitUriDetailApi } from "@/api/trackingapi/visituri";
export default {
  data() {
    return {
      gridData: [],
      dialogTableVisible: false,
      dialogFormVisible: false,
      form: {
        name: "",
        region: "",
        date1: "",
        date2: "",
        delivery: false,
        type: [],
        resource: "",
        desc: "",
      },
      formLabelWidth: "120px",
      pageNum: 1,
      total: 0,
      pageSize: 10,
      commonParams: {},
    };
  },
  methods: {
    getIndex($index) {
      return (this.pageNum - 1) * this.pageSize + $index + 1;
    },
    handleSizeChange(val) {
      this.pageNum = 1;
      this.pageSize = val;
      this.initApiEvent(this.commonParams);
    },
    handleCurrentChange(val) {
      this.pageNum = val;
      this.initApiEvent(this.commonParams);
    },
    vistedApiEvent(params) {
      this.pageSize = 10;
      this.pageNum = 1;
      this.commonParams = params;
      this.initApiEvent(params);
    },
    initApiEvent(params) {
      params.pageSize = this.pageSize;
      params.pageNum = this.pageNum;
      this.dialogTableVisible = true;
      getVisitUriDetailApi(params).then((res) => {
        if (res.code == 200) {
          this.gridData = res.data.rows;
          this.total = res.data.total;
        }
      });
    },
  },
  beforeDestroy() {
    this.gridData = [];
  },
};
</script>

<style lang="scss" scoped></style>
