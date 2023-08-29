<template>
  <div class="chartsIcon">
    <div class="public-firstHead">受访页面分析</div>
    <div class="flow-indicator">
      <div class="flow-item">
        <div class="flow-title">流量基础指标</div>
        <el-checkbox-group
          v-model="channelList"
          class="checkBoxStyle"
          @change="handelChannelList"
        >
          <el-checkbox label="pv">浏览量(PV)</el-checkbox>
          <el-checkbox label="uv">访客数(UV)</el-checkbox>
          <el-checkbox label="ipCount" style="margin-right: 0"
            >IP数</el-checkbox
          >
        </el-checkbox-group>
      </div>
      <div class="flow-item setSpace">
        <div class="flow-title">流量质量指标</div>
        <el-checkbox-group
          v-model="flowQuality"
          class="checkBoxStyle"
          @change="handelFlowQuality"
        >
          <el-checkbox label="entryCount">入口页次数</el-checkbox>
          <el-checkbox label="downPvCount">贡献下游浏览量</el-checkbox>
          <el-checkbox label="exitCount">退出页次数</el-checkbox>
          <el-checkbox label="avgVisitTime">平均访问时长</el-checkbox>
          <el-checkbox label="exitRate">退出率</el-checkbox>
        </el-checkbox-group>
      </div>
    </div>
    <!-- class="public-table-block" -->
    <div>
      <div class="public-Table-minHeight public-hoverItem">
        <el-table
          :data="vistedTableData"
          border
          class="public-radius"
          style="width: 100%"
          :header-cell-style="{ textAlign: 'center', background: '#f4f8fe' }"
          :cell-style="tableHeaderColor"
          @sort-change="sortChange($event)"
        >
          <el-table-column label="序号" type="index" width="150" align="center">
            <template slot-scope="scope">
              <span v-text="getIndex(scope.$index)"> </span>
            </template>
          </el-table-column>
          <el-table-column prop="uri" label="页面URL" width="400" :show-overflow-tooltip="true">
            <!-- <template slot-scope="scope">
              <el-popover trigger="hover" placement="top">
                <div>{{ scope.row.uri }}</div>
                <div
                  slot="reference"
                  style="
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                  "
                >
                  {{ scope.row.uri }}
                </div>
              </el-popover>
            </template> -->
          </el-table-column>
          <el-table-column label="流量基础指标">
            <el-table-column v-if="pv" prop="pv" label="浏览量(PV)" sortable />
            <el-table-column v-if="uv" prop="uv" label="访客数(UV)" sortable />
            <el-table-column
              v-if="ipCount"
              prop="ipCount"
              label="IP数"
              sortable
            />
          </el-table-column>
          <el-table-column prop="date" label="流量质量指标">
           
            <el-table-column
              v-if="entryCount"
              prop="entryCount"
              label="入口页次数"
              sortable
            />
            <el-table-column
              v-if="downPvCount"
              prop="downPvCount"
              label="贡献下游浏览量"
              sortable
            />
            <el-table-column
              v-if="exitCount"
              prop="exitCount"
              label="退出页次数"
              sortable
            />
            <el-table-column
              v-if="avgVisitTime"
              prop="avgVisitTime"
              label="平均访问时长"
              sortable
            />
            <el-table-column
              v-if="exitRate"
              prop="exitRate"
              label="退出率"
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
import { blobDownloads } from "@/utils/localDownloadUtil.js";
import { exportVisitUriDetailApi } from "@/api/trackingapi/download";
import flowPoint from "@/components/flowPoint/index";
import { percentage } from "@/utils/percent";
import { formatTime } from "@/utils/format";
export default {
  components: {
    flowPoint,
  },
  data() {
    return {
      channelList: ["index","uri","uv", "ipCount", "pv",],
      flowQuality: ["entryCount", "avgVisitTime"],
      current: {
        size: 10,
        page: 1,
        sortName: null,
        sortOrder: null,
      },
      mergedArr: [],
      uri: false,
      uv: false,
      pv: false,
      ipCount: false,
      exitCount: false,
      avgVisitTime: false,
      exitRate: false,
      entryCount: false,
      downPvCount: false,
      currentPage: 1,
      vistedTableData: [],
      total: 0,
      pageSize: 10,
    };
  },
  mounted() {
    this.$bus.$on("publicEventDown", (val) => {
      this.publicEventDown(val);
    });
  },
  methods: {
    tableHeaderColor({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 1) {
        return "text-align:left";
      } else {
        return "text-align:center";
      }
    },
    // 分页
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
    publicEventDown(val) {
      let params = val;
      params.project = this.$store.getters.project;
      params.cols = [...this.channelList, ...this.flowQuality];
      exportVisitUriDetailApi(params).then((res) => {
        let name = this.sliceTypeFile(res);
        blobDownloads(res.data, name);
      });
    },
    sliceTypeFile(res) {
      let fileName = res.headers["content-disposition"] || "";
      let index1 = fileName.indexOf("filename=");
      let result = fileName.substring(index1);
      result = decodeURIComponent(decodeURI(result.slice(9)));
      return result;
    },
    vistedAnalysis(val) {
      this.vistedTableData = val.rows;
      this.vistedTableData.map((item) => {
        if (item.exitRate) {
          item.exitRate = percentage(item.exitRate);
        }
        if (item.avgVisitTime) {
          item.avgVisitTime = formatTime(Math.floor(item.avgVisitTime));
        }
        if (item.avgPv) {
          item.avgPv = Math.floor(item.avgPv);
        }
        // if (item.avgPv) {
        //   item.avgPv = Math.floor(item.avgPv);
        // }
      });
      this.total = val.total;

      this.initShowTable();
    },
    // 分页器
    handleSizeChange(val) {
      this.current.size = val;
      this.$emit("currentPage", this.current);
    },
    handleCurrentChange(val) {
      this.current.page = val;
      this.$emit("currentPage", this.current);
    },
    handelChannelList() {
      this.initShowTable();
    },
    handelFlowQuality() {
      this.initShowTable();
    },
    initCurrentPage() {
      this.currentPage = 1;
    },
    initShowTable() {
      this.mergedArr = [];
      this.mergedArr = [...this.channelList, ...this.flowQuality];
      this.flowPoint(this.mergedArr);
    },
    flowPoint(val) {
      if (val.length > 0) {
        if (val.includes("pv")) {
          this.pv = true;
        } else {
          this.pv = false;
        }
        if (val.includes("uri")) {
          this.uri = true;
        } else {
          this.uri = false;
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
        if (val.includes("exitCount")) {
          this.exitCount = true;
        } else {
          this.exitCount = false;
        }

        if (val.includes("avgVisitTime")) {
          this.avgVisitTime = true;
        } else {
          this.avgVisitTime = false;
        }
        if (val.includes("avgVisitTime")) {
          this.avgVisitTime = true;
        } else {
          this.avgVisitTime = false;
        }
        if (val.includes("exitRate")) {
          this.exitRate = true;
        } else {
          this.exitRate = false;
        }
        if (val.includes("downPvCount")) {
          this.downPvCount = true;
        } else {
          this.downPvCount = false;
        }
        if (val.includes("entryCount")) {
          this.entryCount = true;
        } else {
          this.entryCount = false;
        }
      }
    },
  },
  beforeDestroy() {
    console.log("执行了");
    // 绑在$bus上的 都要主动销毁，因为App.vue销毁之后，$bus还在，在上面注册的事件都还在占空间，所以销毁时得一起$off掉
    this.$bus.$off(["publicEventDown"]); // 同时关闭多个用数组形式放进去
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
  margin: 20px;
  padding-top: 1px;
  min-height: 461px;
  background: #fff;
  border-radius: 6px;
  padding: 22px;
  .flow-indicator {
    min-height: 58px;
    background: rgba(252, 252, 252, 0.39);
    border: 1px solid #f0f0f5;
    border-radius: 6px;
    box-sizing: border-box;
    margin-top: 10px;
    .setSpace {
      margin-bottom: 12px;
    }
    .flow-item {
      display: flex;
      // align-items: center;
      margin-top: 12px;
      margin-left: 10px;
      .flow-title {
        margin-right: 21px;
        font-size: 13px;
        font-weight: 400;
        line-height: 32px;
        color: #4d4d4d;
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
      span {
        font-size: 14px;
        padding: 0 10px;
      }
      .checkBoxStyle {
        padding-right: 10px;
      }
    }
  }
}
</style>
