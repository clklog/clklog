<template>
  <div class="chartsIcon">
    <div class="flow-indicator">
      <div class="flow-item">
        <div class="flow-title">流量基础指标</div>
        <el-checkbox-group v-model="channelList" class="checkBoxStyle">
          <el-checkbox label="1">浏览量(PV)</el-checkbox>
          <el-checkbox label="2">浏览量占比</el-checkbox>
          <el-checkbox label="3">访问次数</el-checkbox>
          <el-checkbox label="4">访客数(UV)</el-checkbox>
          <el-checkbox label="5">新访客数</el-checkbox>
          <el-checkbox label="6">新访客比率</el-checkbox>
          <el-checkbox label="7" style="margin-right: 0">IP数</el-checkbox>
        </el-checkbox-group>
      </div>
      <div class="flow-item setSpace">
        <div class="flow-title">流量质量指标</div>
        <el-checkbox-group v-model="flowQuality" class="checkBoxStyle">
          <el-checkbox label="8">跳出率</el-checkbox>
          <el-checkbox label="9">平均访问时长</el-checkbox>
          <el-checkbox label="10">平均访问页数</el-checkbox>
        </el-checkbox-group>
      </div>
    </div>

    <div class="table-content">
      <!-- <el-table
        :header-cell-style="{ textAlign: 'center' }"
        :cell-style="{ textAlign: 'center' }"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="150" />
        <el-table-column prop="dataTime" label="访客类型" width="150" />
        <el-table-column prop="date" label="流量基础指标" width="150">
          <el-table-column prop="amount1" label="浏览量(PV)" sortable />
          <el-table-column prop="amount5" label="访客数(UV)" sortable />
          <el-table-column prop="amount7" label="IP数" sortable />
        </el-table-column>
        <el-table-column prop="date" label="流量质量指标" width="150">
          <el-table-column prop="amount8" label="跳出率" sortable />
          <el-table-column prop="amount9" label="平均访问时长" sortable />
        </el-table-column>
      </el-table> -->
      <el-table
        :header-cell-style="{ textAlign: 'center' }"
        :cell-style="{ textAlign: 'center' }"
        :data="visitorDetailData"
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="150" />
        <el-table-column prop="visitorType" label="访客类型" width="150" />
        <el-table-column label="流量基础指标" width="150">
          <el-table-column prop="pv" label="浏览量(PV)" sortable />
          <el-table-column prop="uv" label="访客数(UV)" sortable />
          <el-table-column prop="ipCount" label="IP数" sortable />
        </el-table-column>
        <el-table-column label="流量质量指标" width="150">
          <el-table-column prop="bounceRate" label="跳出率" sortable />
          <el-table-column prop="avgVisitTime" label="平均访问时长" sortable />
        </el-table-column>
      </el-table>
      
    </div>
    <div class="block">
      <el-pagination
        next-text="下一页"
        :current-page="currentPage4"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="10"
        layout=" sizes, prev, pager, next, jumper"
        :total="40"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { getVisitorDetailApi } from "@/api/trackingapi/visitor";
export default {
  props: {
    commonParams: {},
  },
  mounted() {},
  computed: {
    params() {
      // delete this.commonParams.project;
      // this.commonParams.startTime = ''
      // this.commonParams.endTime = ''
      return Object.assign({}, this.commonParams);
    },
  },
  data() {
    return {
      channelList: ["3", "5"],
      flowQuality: ["9"],
      visitorDetailData: [],
      tableData: [
        {
          dataTime: "总计",
          amount1: "14",
          amount2: "3.2",
          amount3: 10,
          amount4: "234",
          amount5: "3.2",
          amount6: 10,
          amount7: "234",
          amount8: "3.2",
          amount9: "100%",
          amount10: 69,
        },
        {
          dataTime: "新访客",
          amount1: "165",
          amount2: "4.43",
          amount3: 12,
          amount4: "24",
          amount5: "3.2",
          amount6: 10,
          amount7: "234",
          amount8: "3.2",
          amount9: "100%",
          amount10: 6,
        },
        {
          dataTime: "老访客",
          amount1: "165",
          amount2: "4.43",
          amount3: 12,
          amount4: "34",
          amount5: "3.2",
          amount6: 10,
          amount7: "234",
          amount8: "3.2",
          amount9: "100%",
          amount10: 9,
        },
      ],
      currentPage4: 4,
    };
  },
  methods: {
    getVisitorDetail() {
      getVisitorDetailApi(this.params).then((res) => {
        if (res.data) {
          this.visitorDetailData = []
          Object.keys(res.data).forEach((key) => {
            if (key == "newVisitor") {
              res.data[key].visitorType = "新访客";
            } else if (key == "oldVisitor") {
              res.data[key].visitorType = "老访客";
            } else if (key == "total") {
              res.data[key].visitorType = "总计";
            }
            this.visitorDetailData.push(res.data[key]);
          });
        }
      });
    },
    // 分页器
    handleSizeChange(val) {},
    handleCurrentChange(val) {},
  },
};
</script>
<style>
.chartsIcon .flow-indicator .flow-item .el-checkbox {
  width: 100px !important;
}
</style>

<style lang="scss" scoped>
.chartsIcon {
  box-sizing: border-box;
  margin: 20px;
  padding-top: 1px;
  min-height: 461px;
  background: rgba(250, 250, 251);
  //   background: rgba(158, 158, 161, 0.39);
  border-radius: 6px;

  .flow-indicator {
    min-height: 58px;
    background: rgba(252, 252, 252, 0.39);
    border: 1px solid #f0f0f5;
    border-radius: 6px;
    box-sizing: border-box;
    margin: 12px;
    .setSpace {
      margin-bottom: 12px;
    }
    .flow-item {
      display: flex;
      align-items: center;
      margin-top: 12px;
      margin-left: 10px;
      .flow-title {
        margin-right: 21px;
        font-size: 12px;
        font-weight: 400;
        line-height: 16px;
        color: #4d4d4d;
      }
      .el-checkbox {
        margin-right: 80px;
        font-size: 12px;
        font-weight: 400;
        line-height: 15px;
        color: #697195;
      }
    }
    .check_item {
      background-color: #ffffff;
      margin-left: 20px;
      height: 40px;
      border-radius: 4px;
      border: 1px solid #eee;
      display: flex;
      align-items: center;
      // width: 30%;
      span {
        font-size: 14px;
        padding: 0 10px;
      }
      .checkBoxStyle {
        padding-right: 10px;
      }
    }
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
    padding-right: 10px;
  }
  .el-pagination__jump {
    position: absolute;
    left: 0;
  }
}
</style>
