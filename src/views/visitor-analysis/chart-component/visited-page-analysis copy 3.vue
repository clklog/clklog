<template>
  <div class="chartsIcon public-hoverItem">
    <div
      class="public-firstHead"
      style="display: flex; justify-content: space-between"
    >
      受访页面分析
      <div
        style="
          display: flex;
          font-size: 15px;
          font-weight: 600;
          line-height: 25px;
          color: #4d4d4d;
          cursor: pointer;
        "
      >
        <img
          @click="handelTable(true)"
          style="width: 35px; margin-right: 15px"
          :src="
            showTreeFlag
              ? require('@/assets/images/flat_select.png')
              : require('@/assets/images/flat.png')
          "
        />
        <img
          @click="handelTable(false)"
          style="width: 35px"
          :src="
            showTreeFlag
              ? require('@/assets/images/tree.png')
              : require('@/assets/images/tree_select.png')
          "
        />
      </div>
    </div>

    <div class="flow-indicator public_indicator">
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
    <div>
      <!-- 树状图 -->
      <div class="public-Table-minHeight" v-show="!showTreeFlag">
        <el-table
          :data="treeList"
          style="width: 100%"
          :row-key="getRowKeys"
          :tree-props="{ children: 'leafUri', hasChildren: 'hasChildren' }"
          border
          lazy
          ref="multipleTable"
          :expand-row-keys="tableExpands"
          @expand-change="expandChangeEvent"
        >
          <el-table-column
            label="页面地址"
            width="400px"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope" style="display: flex">
              <!-- 最后一级增加节点 -->
              <div
                v-if="
                  scope.row.leafUri &&
                  scope.row.leafUri.length == 0 &&
                  !scope.row.firstFlag
                "
                class="tableName"
                :style="{ 'padding-left': `${scope.row.level * 19}px` }"
                @click="handleTree(scope.row, '')"
              >
                <img
                  src="@/assets/images/add.png"
                  style="
                    width: 12px;
                    height: 12px;
                    object-fit: cover;
                    margin-right: 10px;
                    margin-top: 2px;
                    cursor: pointer;
                  "
                />
              </div>
              <div>
                <div>{{ scope.row.title ? scope.row.title : "" }}</div>
                <div>{{ scope.row.segment ? scope.row.segment : "/" }}</div>
              </div>

              <img
                v-if="scope.row.leafUri.length > 0 && !scope.row.firstFlag"
                style="
                  position: absolute;
                  right: 10px;
                  top: 8px;
                  cursor: pointer;
                  width: 17px;
                "
                src="@/assets/images/showDialog.png"
                @click="handleTree(scope.row, 'dialog')"
              />
            </template>
          </el-table-column>
          <el-table-column label="流量基础指标">
            <el-table-column v-if="pv" prop="detail.pv" label="浏览量(PV)" />
            <el-table-column v-if="uv" prop="detail.uv" label="访客数(UV)" />
            <el-table-column
              v-if="ipCount"
              prop="detail.ipCount"
              label="IP数"
            />
          </el-table-column>
          <el-table-column prop="detail.date" label="流量质量指标">
            <el-table-column
              v-if="entryCount"
              prop="detail.entryCount"
              label="入口页次数"
            />
            <el-table-column
              v-if="downPvCount"
              prop="detail.downPvCount"
              label="贡献下游浏览量"
            />
            <el-table-column
              v-if="exitCount"
              prop="detail.exitCount"
              label="退出页次数"
            />
            <el-table-column
              v-if="avgVisitTime"
              prop="detail.avgVisitTime"
              label="平均访问时长"
            >
              <template slot-scope="scope">
                {{ formatTimeEvent(scope.row.detail.avgVisitTime) }}
              </template>
            </el-table-column>
            <el-table-column
              v-if="exitRate"
              prop="detail.exitRate"
              label="退出率"
            >
              <template slot-scope="scope">
                {{ percentageFun(scope.row.detail.exitRate) }}
              </template>
            </el-table-column>
          </el-table-column>
        </el-table>
      </div>
      <!-- 默认图表 -->
      <div v-show="showTreeFlag">
        <div class="public-Table-minHeight">
          <el-table
            :data="vistedTableData"
            border
            class="public-radius"
            style="width: 100%"
            :header-cell-style="{ textAlign: 'center', background: '#f7fafe ' }"
            :cell-style="tableHeaderColor"
            @sort-change="sortChange($event)"
          >
            <el-table-column
              label="序号"
              type="index"
              width="80"
              align="center"
            >
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
              <el-table-column
                v-if="pv"
                prop="pv"
                label="浏览量(PV)"
                sortable="custom"
              />
              <el-table-column
                v-if="uv"
                prop="uv"
                label="访客数(UV)"
                sortable="custom"
              />
              <el-table-column
                v-if="ipCount"
                prop="ipCount"
                label="IP数"
                sortable="custom"
              />
            </el-table-column>
            <el-table-column prop="date" label="流量质量指标">
              <el-table-column
                v-if="entryCount"
                prop="entryCount"
                label="入口页次数"
                sortable="custom"
              />
              <el-table-column
                v-if="downPvCount"
                prop="downPvCount"
                label="贡献下游浏览量"
                sortable="custom"
              />
              <el-table-column
                v-if="exitCount"
                prop="exitCount"
                label="退出页次数"
                sortable="custom"
              />
              <el-table-column
                v-if="avgVisitTime"
                prop="avgVisitTime"
                label="平均访问时长"
                sortable="custom"
              />
              <el-table-column
                v-if="exitRate"
                prop="exitRate"
                label="退出率"
                sortable="custom"
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
      <vistedDialog ref="vistedDialog"></vistedDialog>
    </div>
  </div>
</template>

<script>
import { blobDownloads } from "@/utils/localDownloadUtil.js";
import { exportVisitUriDetailApi } from "@/api/trackingapi/download";
import { getVisitUriListOfUriPathApi } from "@/api/trackingapi/visituri";
import flowPoint from "@/components/flowPoint/index";
import vistedDialog from "./visted-dialog.vue";
import { percentage } from "@/utils/percent";
import { formatTime } from "@/utils/format";
export default {
  components: {
    flowPoint,
    vistedDialog,
  },
  data() {
    return {
      isExpand: true,
      initNum: 1,
      channelList: ["index", "uri", "uv", "ipCount", "pv", "title"],
      flowQuality: ["entryCount", "avgVisitTime"],
      current: {
        size: 10,
        page: 1,
        sortName: null,
        sortOrder: null,
      },
      showTreeFlag: false,
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
      // 测试数组
      tableData: [
        {
          id: 1,
          date: "https://app.huoqingqing.com/#",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1518 弄",
        },
        {
          id: 2,
          date: "https://app.huoqingqing.com/#",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1517 弄",
        },
        {
          id: 3,
          date: "一级-https://app.huoqingqing.com/#",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1519 弄",
          detail: {
            date: "一级-https://app.huoqingqing.com/#",
            name: "王小虎",
            address: "上海市普陀区金沙江路 1519 弄",
          },

          children: [
            {
              detail: {
                date: "一级-https://app.huoqingqing.com/#",
                name: "王小虎",
                address: "上海市普陀区金沙江路 1519 弄",
              },
              id: 31,
              date: "二级-https://app.huoqingqing.com/#",
              name: "王小虎",
              address: "上海市普陀区金沙江路 1519 弄",
            },
            {
              detail: {
                date: "一级-https://app.huoqingqing.com/#",
                name: "王小虎",
                address: "上海市普陀区金沙江路 1519 弄",
              },
              id: 32,
              date: "二级-https://app.huoqingqing.com/#",
              name: "王小虎",
              address: "上海市普陀区金沙江路 1519 弄",
            },
          ],
        },
        {
          id: 4,
          date: "https://app.huoqingqing.com/#",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1516 弄",
          detail: {
            date: "一级-https://app.huoqingqing.com/#",
            name: "王小虎",
            address: "上海市普陀区金沙江路 1519 弄",
          },
          children: [
            {
              detail: {
                date: "一级-https://app.huoqingqing.com/#",
                name: "王小虎",
                address: "上海市普陀区金沙江路 1519 弄",
              },
              id: 33,
              date: "https://app.huoqingqing.com/#",
              name: "平级状态",
              address: "平级状态 1519 弄",
            },
            {
              detail: {
                date: "一级-https://app.huoqingqing.com/#",
                name: "王小虎",
                address: "上海市普陀区金沙江路 1519 弄",
              },
              id: 34,
              date: "https://app.huoqingqing.com/#",
              name: "王小虎",
              address: "平级状态 1519 弄",
            },
          ],
        },
      ],
      treeList: [],
      commonParams: {}, //接口参数
      random: 11,
      uriListEnd: [],

      listLoading: false,
      tableExpands: [],
      list: [],
      maps: new Map(),
    };
  },
  mounted() {
    this.$bus.$on("publicEventDown", (val) => {
      this.publicEventDown(val);
    });
  },
  computed: {
    multipleToPadding(multiple) {
      // return `${multiple * 10}px`;
    },
  },
  methods: {
    // 二级标题调用接口添加动态数据
    expandChangeEvent(row, expandedRowKeys) {
      this.tableExpands = [];
      if (expandedRowKeys && row.level && !row.loadingApi) {
        console.log(row, expandedRowKeys, "234343");
        this.scopeEventApi(row.uri, "", row.detail.numRandom);
      }
    },
    everyTableEvent(row) {
      console.log(row, "row------");
    },
    percentageFun(val) {
      return percentage(val);
    },
    formatTimeEvent(val) {
      return formatTime(Math.floor(val));
    },
    getRowKeys(row) {
      let num = Math.random().toString();
      return row.detail.numRandom;
    },
    // 层级展开事件
    handleTree(scope, event) {
      if (event == "dialog") {
        console.log(scope, "判断一二级内容");
        this.scopeEventApi(scope.uri, event);
      } else {
        this.isExpand = false;
        // 动态添加节点
        this.scopeEventApi(scope.uri, "", scope.detail.numRandom);
      }
    },
    scopeEventApi(uri, event, numRandom) {
      this.commonParams.uriPath = uri;
      getVisitUriListOfUriPathApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          if (event == "dialog") {
            this.$refs.vistedDialog.vistedApiEvent(res.data);
          } else {
            this.uriListEnd = res.data;
            // 动态添加节点
            res.data.forEach((item, index) => {
              item.segment = item.uri;
              // 去除前缀
              if (item.segment.includes(".com/#")) {
                item.segment = item.uri.split(".com/#")[1];
              } else if (item.segment.includes(".com/")) {
                item.segment = item.uri.split(".com/")[1];
              }
              item.numRandom = item.uri + this.generateRandomNumber();
              item.uri = item.uri + index + 1;
              item.leafUri = [];
              item.detail = JSON.parse(JSON.stringify(item));
              item.firstFlag = "istrue";

            });
            this.treeList.forEach((item) => {
              this.insertChild(item, numRandom, res.data);
            });
            this.treeList = this.treeList;
            console.log(this.treeList, "this.treeList-----");
          }
        }
      });
    },
    // 动态增加节点
    insertChild(list, id, newChild) {
      list.leafUri.forEach((item) => {
        // let flag = item.uri == id; //过滤非匹配idz
        let flag = item.detail.numRandom == id; //过滤非匹配idz
        if (flag) {
          // item.leafUri = JSON.parse(JSON.stringify(newChild));
          item.loadingApi = true;
          let newArray = JSON.parse(JSON.stringify(newChild))
          item.leafUri = [...new Set([...item.leafUri, ...newArray])]
          this.tableExpands = [item.detail.numRandom];
          if (this.uriListEnd.length == 0) {
            item.firstFlag = "istrue";
          }
          return;
        } else {
          if (item.leafUri.length > 0) {
            this.insertChild(item, id, newChild);
          }
        }
      });

      return;
    },

    // 切换table
    handelTable(val) {
      this.showTreeFlag = val;
    },
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
      params.projectName = this.$store.getters.projectName;
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
        if (item.avgVisitTime || item.avgVisitTime == 0) {
          item.avgVisitTime = formatTime(Math.floor(item.avgVisitTime));
        }
        if (item.avgPv) {
          item.avgPv = Math.floor(item.avgPv);
        }
      });
      this.total = val.total;
      this.initShowTable();
    },

    // 动态添加节点
    treeListEvent(val, params) {
      this.commonParams = params;
      val.map((item) => {
        if (item.detail) {
          item.segment = item.detail.uri;
          item.firstFlag = "istrue";
        }
      });

      this.treeList = val;
      // 测试数据
      this.treeList.forEach((item) => {
        item.detail.numRandom = item.detail.uri + this.generateRandomNumber();
        this.insertRandom(item.leafUri);
      });
      // console.log(this.treeList, "增加随机数");
    },
    // 动态添加节点
    insertRandom(list, level = 1) {
      list.forEach((item) => {
        item.level = level;
        if (item.detail) {
          item.detail.numRandom = item.detail.uri + this.generateRandomNumber();
          if (item.leafUri && item.leafUri.length > 0) {
            this.insertRandom(item.leafUri, level + 1);
          }
        }
      });
      return;
    },
    //设置随机数
    generateRandomNumber() {
      let randomNumber = "";
      for (let i = 0; i < 16; i++) {
        randomNumber += Math.floor(Math.random() * 10);
      }
      return randomNumber.toString();
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
    // 绑在$bus上的 都要主动销毁，因为App.vue销毁之后，$bus还在，在上面注册的事件都还在占空间，所以销毁时得一起$off掉
    this.$bus.$off(["publicEventDown"]); // 同时关闭多个用数组形式放进去
  },
};
</script>

<style lang="scss" scoped>
::v-deep {
  // .el-icon-arrow-right:before {
  //   // content: '';
  //   background-image: url("../../../assets/images/reduce.png") !important;
  //   width: 20px;
  //   height: 20px;
  //   background-size: cover;
  //   background-position: center;
  //   // background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20'%3e%3cpath fill='none' stroke='%23fff' stroke-linecap='round' stroke-linejoin='round' stroke-width='3' d='m6 10 3 3 6-6'/%3e%3c/svg%3e");
  // }
  .el-table__expand-icon--expanded {
    transform: rotate(0deg);
  }
  .el-icon-arrow-right:before {
    position: relative !important;
    background: url("../../../assets/images/add.png") no-repeat 0 3px;
    content: "";
    display: block;
    width: 16px;
    height: 16px;
    font-size: 16px;
    background-size: 13px;
  }
  //有子节点 且已展开
  .el-table__expand-icon--expanded {
    position: relative !important;
    .el-icon-arrow-right:before {
      background: url("../../../assets/images/reduce.png") no-repeat 0 3px;
      content: "";
      display: block;
      width: 16px;
      height: 16px;
      font-size: 16px;
      background-size: 13px;
    }
  }
  // 没有字节
  .el-table__placeholder::before {
    background: "";
    content: "";
    display: block;
    width: 15px;
    height: 15px;
    font-size: 15px;
    background-size: 15px;
  }

  @import "~@/styles/components/el-checkbox.scss";
  @import "~@/styles/components/el-pagination.scss";
  .el-table .cell {
    display: flex;
  }
  .el-table__header {
    // 使表格兼容safari，不错位,表头宽度较小
    width: 100%;
    table-layout: fixed !important;
  }
  .el-table__body {
    width: 100%;
    // 使表格兼容safari，不错位
    table-layout: fixed !important;
  }
}
.tableName {
  position: absolute;
}
.treeStyle {
  margin-left: 15px;
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
    border: 1px solid #f0f0f5;
    border-radius: 6px;
    box-sizing: border-box;
    margin-top: 10px;
    .setSpace {
      margin-bottom: 12px;
    }
    .flow-item {
      display: flex;
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
