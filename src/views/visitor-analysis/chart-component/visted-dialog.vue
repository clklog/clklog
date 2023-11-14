<template>
  <div>
    <el-dialog
      title="资源路径Top10"
      :visible.sync="dialogTableVisible"
      width="80%"
    >
      <el-table :data="gridData" border>
       
        <el-table-column label="序号" type="index" width="80" align="center">
         
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
          <el-table-column prop="pv" label="浏览量(PV)" sortable="custom" />
          <el-table-column prop="uv" label="访客数(UV)" sortable="custom" />
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
              {{ formatTimeEvent(scope.row.avgVisitTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="exitRate" label="退出率" sortable="custom">
            <template slot-scope="scope">
              {{ percentageFun(scope.row.exitRate) }}
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { percentage } from "@/utils/percent";
import { formatTime } from "@/utils/format";
export default {
  data() {
    return {
      gridData: [
        {
          date: "2016-05-02",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1518 弄",
        },
        {
          date: "2016-05-04",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1518 弄",
        },
        {
          date: "2016-05-01",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1518 弄",
        },
        {
          date: "2016-05-03",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1518 弄",
        },
      ],
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
    };
  },
  methods: {
    percentageFun(val) {
      return percentage(val);
    },
    formatTimeEvent(val) {
      return formatTime(Math.floor(val));
    },
    vistedApiEvent(val) {
      // console.log(val);
      this.gridData = val;
      this.dialogTableVisible = true;
    },
  },
};
</script>

<style lang="scss" scoped></style>
