<template>
  <div>
    <div class="documentation-container">
      <div class="Overview public-hoverItem">
        <div class="public-firstHead">流量概览</div>
        <div class="bid-list-page">
          <originView ref="originView" byChannel></originView>
          <div
            class="bid-list-record"
            v-for="(item, index) in filterChannelList"
            :key="index"
          >
            <div
              class="bid-list-item w111"
              :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
            >
              <p>{{ item.pv || "--" }}</p>
            </div>
            <div
              class="bid-list-item w111"
              :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
            >
              <p>{{ item.visitCount || "--" }}</p>
            </div>
            <div
              class="bid-list-item w111"
              :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
            >
              <p>{{ item.uv || "--" }}</p>
            </div>
            <div
              class="bid-list-item w111"
              :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
            >
              <p>{{ item.ipCount || "--" }}</p>
            </div>
            <div
              class="bid-list-item w111"
              :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
            >
              <p>{{ averageRulesEvent(item.avgPv) || "--" }}</p>
            </div>
            <div
              class="bid-list-item w111"
              :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
            >
              <p>{{ formatTimeEvent(item.avgVisitTime) || "--" }}</p>
            </div>
            <div
              class="bid-list-item w111"
              :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
            >
              <p>{{ item.bounceRate || "--" }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="chartsIcon public-hoverItem">
      <div class="public-firstHead">渠道分析</div>
      <flowPoint ref="flowPoint" @flowPoint="flowPoint" byChannel></flowPoint>
      <div>
        <div class="public-Table-minHeight">
          <el-table
            class="public-radius"
            :data="
              channelTableData.slice(
                (currentPage - 1) * pageSize,
                currentPage * pageSize
              )
            "
            @sort-change="sortChange($event)"
            :header-cell-style="{ textAlign: 'center', background: '#f7fafe ' }"
            :cell-style="{ textAlign: 'center' }"
            border
            style="width: 100%"
          >
            <el-table-column type="index" label="序号" width="80" />
            <el-table-column prop="channel" label="渠道类型" width="150" />
            <el-table-column prop="date" label="流量基础指标" width="150">
              <el-table-column
                v-if="pv"
                prop="pv"
                label="浏览量"
                sortable
              />
              <el-table-column
                v-if="pvRate"
                prop="pvRate"
                label="浏览量占比"
                sortable
              />
              <el-table-column
                v-if="visitCount"
                prop="visitCount"
                label="访问次数"
                sortable
              />
              <el-table-column
                v-if="visitCountRate"
                prop="visitCountRate"
                label="访问次数占比"
                sortable
              />
              <el-table-column
                v-if="uv"
                prop="uv"
                label="访客数"
                sortable
              />
              <el-table-column
                v-if="uvRate"
                prop="uvRate"
                label="访客数占比"
                sortable
              />
              <el-table-column
                v-if="newUv"
                prop="newUv"
                label="新访客数"
                sortable
              />
              <el-table-column
                v-if="newUvRate"
                prop="newUvRate"
                label="新访客数占比"
                sortable
              />
              <el-table-column
                v-if="ipCount"
                prop="ipCount"
                label="IP数"
                sortable
              />
              <el-table-column
                v-if="ipCountRate"
                prop="ipCountRate"
                label="IP数占比"
                sortable
              />
            </el-table-column>
            <el-table-column prop="date" label="流量质量指标" width="150">
              <el-table-column
                v-if="bounceRate"
                prop="bounceRate"
                label="跳出率"
                sortable
              />
              <el-table-column
                prop="avgVisitTime"
                label="平均访问时长"
                sortable
                v-if="avgVisitTime"
              >
                <template slot-scope="scope">
                  {{ formatTimeEvent(scope.row.avgVisitTime) }}
                </template>
              </el-table-column>
              <el-table-column
                v-if="visitTimeRate"
                prop="visitTimeRate"
                label="平均访问时长占比"
                sortable
              />
              <el-table-column
                v-if="avgPv"
                prop="avgPv"
                label="平均访问页数"
                sortable
              >
                <template slot-scope="scope">
                  {{ averageRulesEvent(scope.row.avgPv) }}
                </template>
              </el-table-column>
            </el-table-column>
          </el-table>
        </div>
        <!-- <div class="block">
          <el-pagination
            :current-page="currentPage"
            :page-sizes="[10, 20, 30, 40]"
            :page-size="pageSize"
            layout=" sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div> -->
      </div>
    </div>
  </div>
</template>

<script>
import originView from "@/components/origin-view/index";
import flowPoint from "@/components/flowPoint/index";
import { percentage, averageRules } from "@/utils/percent";
import { formatTime } from "@/utils/format";
export default {
  components: { originView, flowPoint },
  data() {
    return {
      filterChannelList: [{}],
      currentPage: 1,
      channelTableData: [],
      channelList: ["pv", "visitCount"],
      flowQuality: ["bounceRate"],
      equipmentList: [],
      pv: false,
      visitCount: false,
      newUv: false,
      newUvRate: false,
      uv: false,
      ipCount: false,
      avgPv: false,
      avgVisitTime: false,
      bounceRate: false,
      pvRate: false,
      newUvRate: false,
      visitTimeRate: false,
      avgPvRate: false,
      pvRate: false,
      visitCount: false,
      visitCountRate: false,
      uvRate: false,
      ipCountRate: false,
      currentPage: 1,
      total: 0,
      pageSize: 10,
      current: {
        sortName: "",
        sortOrder: "",
      },
    };
  },
  methods: {
    sortChange(e) {
      if (e.order && e.order == "ascending") {
        // 降序
        this.current.sortName = e.prop;
        this.current.sortOrder = "ascending";
      } else if (e.order && e.order == "descending") {
        // 升序
        this.current.sortName = e.prop;
        this.current.sortOrder = "descending";
      }else {
        this.current.sortName = null;
        this.current.sortOrder = null;
        this.$emit("currentPage");
      }
      this.ascDscEvent();
    },
    ascDscEvent() {
      let nameAttr = this.current.sortName;
      if (nameAttr && this.current.sortOrder === "ascending") {
        this.channelTableData = this.channelTableData.sort(function (a, b) {
          return a[nameAttr] - b[nameAttr];
        });
      } else if (nameAttr && this.current.sortOrder === "descending") {
        this.channelTableData = this.channelTableData.sort(function (a, b) {
          return b[nameAttr] - a[nameAttr];
        });
      }
    },
    averageRulesEvent(num) {
      return averageRules(num);
    },
    getChannelList(val) {
      this.channelTableData = val.sort((a, b) => {
        if (a.channel === "全部") {
          return -1;
        } else if (b.channel === "全部") {
          return 1;
        }
        return 0;
      });
      this.channelTableData.map((item) => {
        if (item.bounceRate) {
          item.bounceRate = percentage(item.bounceRate);
        }
        if (item.uvRate) {
          item.uvRate = percentage(item.uvRate);
        }
        if (item.newUvRate) {
          item.newUvRate = percentage(item.newUvRate);
        }
        if (item.visitCountRate) {
          item.visitCountRate = percentage(item.visitCountRate);
        }
        if (item.ipCountRate) {
          item.ipCountRate = percentage(item.ipCountRate);
        }

        if (item.pvRate) {
          item.pvRate = percentage(item.pvRate);
        }
        if (item.visitTimeRate) {
          item.visitTimeRate = percentage(item.visitTimeRate);
        }
      });
      const filteredArr = this.channelTableData.filter(
        // (item) => item.channel !== "网站"
        (item) => item.channel == "全部"
      );
      if (filteredArr && filteredArr.length > 0) {
        this.filterChannelList = filteredArr;
      }
    },
    formatTimeEvent(val) {
      return formatTime(Math.floor(val));
    },
    // 分页器
    handleSizeChange(val) {
      this.currentPage = 1;
      this.pageSize = val;
    },
    handleCurrentChange(val) {
      this.currentPage = val;
    },
    flowPoint(val) {
      if (val.length > 0) {
        if (val.includes("pv")) {
          this.pv = true;
        } else {
          this.pv = false;
        }
        if (val.includes("visitCount")) {
          this.visitCount = true;
        } else {
          this.visitCount = false;
        }
        if (val.includes("newUv")) {
          this.newUv = true;
        } else {
          this.newUv = false;
        }
        if (val.includes("newUvRate")) {
          this.newUvRate = true;
        } else {
          this.newUvRate = false;
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
        if (val.includes("avgPv")) {
          this.avgPv = true;
        } else {
          this.avgPv = false;
        }
        if (val.includes("avgVisitTime")) {
          this.avgVisitTime = true;
        } else {
          this.avgVisitTime = false;
        }
        if (val.includes("bounceRate")) {
          this.bounceRate = true;
        } else {
          this.bounceRate = false;
        }
        if (val.includes("pvRate")) {
          this.pvRate = true;
        } else {
          this.pvRate = false;
        }
        if (val.includes("newUvRate")) {
          this.newUvRate = true;
        } else {
          this.newUvRate = false;
        }

        if (val.includes("pvRate")) {
          this.pvRate = true;
        } else {
          this.pvRate = false;
        }

        if (val.includes("visitCount")) {
          this.visitCount = true;
        } else {
          this.visitCount = false;
        }
        if (val.includes("visitCountRate")) {
          this.visitCountRate = true;
        } else {
          this.visitCountRate = false;
        }
        if (val.includes("uvRate")) {
          this.uvRate = true;
        } else {
          this.uvRate = false;
        }
        if (val.includes("ipCountRate")) {
          this.ipCountRate = true;
        } else {
          this.ipCountRate = false;
        }
        if (val.includes("visitTimeRate")) {
          this.visitTimeRate = true;
        } else {
          this.visitTimeRate = false;
        }
        if (val.includes("avgPvRate")) {
          this.avgPvRate = true;
        } else {
          this.avgPvRate = false;
        }
      }
    },
    percentageFun(val) {
      return percentage(val);
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep {
  @import "~@/styles/components/el-pagination.scss";
  @import "~@/styles/components/el-checkbox.scss";
}

.trafficHead {
  font-size: 16px;
  font-weight: 600;
  color: #4d4d4d;
  padding: 20px 30px 20px;
}
img {
  width: 11.44px;
  height: 11.44px;
  margin-left: 12px;
  cursor: pointer;
}
.Overview {
  margin: 20px;
  min-height: 118px;
  background-color: #fff;
  .bid-list-page {
    width: clas(100% -20px);
    display: flex;
    flex-direction: column;
    .bid-list-header {
      display: flex;
      .header-name {
        width: 14%;
        justify-content: center;
        height: 30px;
        display: flex;
        align-items: center;
      }
      .w156 {
        font-size: 13px;
        color: #3f3e3e;
      }
    }
    .bid-list-record {
      display: flex;
      .bid-list-item {
        width: 14%;
        justify-content: center;
        height: 30px;
        display: flex;
        align-items: center;
      }
      .w157 {
        color: #4d4d4d;
        font-size: 12px;
        line-height: 14px;
      }
      .w158 {
        color: #3a98e6;
        font-size: 17px;
        line-height: 17px;
      }
    }
  }
}
.w111 {
  font-size: 16px;
  font-weight: 600;
  line-height: 16px;
  color: #2c7be5;
}
.chartsIcon {
  position: relative;
  box-sizing: border-box;
  margin: 20px;
  min-height: 461px;
  background: #fff;
  padding: 22px;
  border-radius: 6px;
}
</style>
