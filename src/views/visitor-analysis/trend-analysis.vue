<template>
  <div>
    <div class="documentation-container">
      <FilterBar ByArea ByData @setFilterBarParams="setFilterBarParams" />
    </div>
    <div class="Overview" v-if="originData">
      <div class="trendAnalysis">趋势分析</div>
      <div class="bid-list-page">
        <div class="bid-list-header">
          <div class="header-name w156">
            浏览量(PV)
            <el-popover
              placement="bottom-start"
              width="200"
              trigger="hover"
              class="popImg"
            >
              <div style="padding: 5px; font-size: 12px">
                选定时间段内用户访问应用的页面总次数<br /><br>
                用户每打开一个页面被记录一次，同一页面打开多次浏览量值累计计算。
              </div>
              <img
                slot="reference"
                class="icon-question"
                src="@/assets/images/question.png"
                alt=""
              />
            </el-popover>
          </div>
          <div class="header-name w156">
            访问次数
            <el-popover
              placement="bottom-start"
              width="200"
              trigger="hover"
              class="popImg"
            >
              <div style="padding: 5px; font-size: 12px">
                选定时间段内用户访问应用的总次数。<br /><br>
                用户首次访问或距离上次访问超过30分钟再次访问会被记录为一个新的访问。
              </div>
              <img
                slot="reference"
                class="icon-question"
                src="@/assets/images/question.png"
                alt=""
              />
            </el-popover>
          </div>
          <div class="header-name w156">
            访问数<el-popover
              placement="bottom-start"
              width="200"
              trigger="hover"
              class="popImg"
            >
              <div style="padding: 5px; font-size: 12px">
                选定时间段内访问应用的独立访客数。<br /><br>
                访客数以Cookie或设备ID为依据，选定时间段内同一访客多次访问应用只计算为一个访客。
              </div>
              <img
                slot="reference"
                class="icon-question"
                src="@/assets/images/question.png"
                alt=""
              />
            </el-popover>
          </div>
          <div class="header-name w156">
            IP数<el-popover
              placement="bottom-start"
              width="200"
              trigger="hover"
              class="popImg"
            >
              <div style="padding: 5px; font-size: 12px">
                选定时间段内访问应用的独立IP数。
              </div>
              <img
                slot="reference"
                class="icon-question"
                src="@/assets/images/question.png"
                alt=""
              />
            </el-popover>
          </div>
          <div class="header-name w156">
            平均访问页面<el-popover
              placement="bottom-start"
              width="200"
              trigger="hover"
              class="popImg"
            >
              <div style="padding: 5px; font-size: 12px">
                平均访问页数 = 浏览量 / 访问次数
              </div>
              <img
                slot="reference"
                class="icon-question"
                src="@/assets/images/question.png"
                alt=""
              />
            </el-popover>
          </div>
          <div class="header-name w156">
            平均访问时长<el-popover
              placement="bottom-start"
              width="200"
              trigger="hover"
              class="popImg"
            >
              <div style="padding: 5px; font-size: 12px">
                平均访问时长 = 访问总时长 / 访问次数<br /><br>
                访客每次访问的总时长为打开第一个页面到退出或关闭最后一个页面的总时长。
              </div>
              <img
                slot="reference"
                class="icon-question"
                src="@/assets/images/question.png"
                alt=""
              />
            </el-popover>
          </div>
          <div class="header-name w156">
            跳出率<el-popover
              placement="bottom-start"
              width="200"
              trigger="hover"
              class="popImg"
            >
              <div style="padding: 5px; font-size: 12px">
                只浏览一个页面就离开或关闭应用的访问次数占总访问次数的百分比。
              </div>
              <img
                slot="reference"
                class="icon-question"
                src="@/assets/images/question.png"
                alt=""
              />
            </el-popover>
          </div>
        </div>
        <div style="max-height: 250px; overflow-y: auto">
          <div class="bid-list-record">
            <div class="bid-list-item w158">
              <p>{{ (originData.total && originData.total.pv) || "--" }}</p>
            </div>
            <div class="bid-list-item w158">
              <p>
                {{ (originData.total && originData.total.visitCount) || "--" }}
              </p>
            </div>
            <div class="bid-list-item w158">
              <p>{{ (originData.total && originData.total.uv) || "--" }}</p>
            </div>
            <div class="bid-list-item w158">
              <p>
                {{ (originData.total && originData.total.ipCount) || "--" }}
              </p>
            </div>
            <div class="bid-list-item w158">
              <p>{{ (originData.total && originData.total.avgPv) || "--" }}</p>
            </div>
            <div class="bid-list-item w158">
              <p>
                {{
                  (originData.total && originData.total.avgVisitTime) || "--"
                }}
              </p>
            </div>
            <div class="bid-list-item w158">
              <p>
                <!-- {{ (originData.total && originData.total.bounceRate) || "--" }} -->
                <p v-if="originData.total && originData.total.bounceRate">{{ percentageFun(originData.total.bounceRate) }}</p>
                <p v-else>--</p>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
    <indicatorChart ref="indicatorChart" />
    <trendTable ref="trendTable" />
  </div>
</template>

<script>
import { FilterBar } from "@/layout/components";
import indicatorChart from "./chart-component/IndicatorChart.vue";
import trendTable from "./chart-component/trend-table.vue";
import { getFlowTrendDetailApi } from "@/api/trackingapi/flow";
import { copyObj } from "@/utils/copy";
import { percentage } from "@/utils/percent";
export default {
  name: "TrendAnalysis",
  components: {
    FilterBar,
    indicatorChart,
    trendTable,
  },
  data() {
    return {
      filterBarParams: {},
      originData: null,
    };
  },
  computed: {
    project() {
      return this.$store.getters.project;
    },
    commonParams() {
      const { project } = this;
      return Object.assign({ project }, this.filterBarParams);
    },
  },
  watch: {
    commonParams(val) {
      this.getFlowTrendDetail();
    },
  },
  methods: {
    percentageFun(val){
        return percentage(val)
    },
    getFlowTrendDetail() {
      getFlowTrendDetailApi(this.commonParams).then((res) => {
        if (res.code == 200) {
          this.originData = res.data;
          this.$refs.indicatorChart.apiDetailList(res.data);
          this.$refs.trendTable.apiDetailList(res.data);
        }
      });
    },
    setFilterBarParams(val) {
      this.filterBarParams = copyObj(val);
    },
  },
};
</script>
<style lang="scss" scoped>
.Overview {
  margin: 20px;
  min-height: 118px;
  background-color: #fafafb;
  .trendAnalysis {
    font-size: 14px;
    font-weight: 500;
    color: #4d4d4d;
    padding: 14px 13px 18px 13px;
  }
  .bid-list-page {
    width: clas(100% -20px);
    display: flex;
    flex-direction: column;
    .bid-list-header {
      display: flex;
      img {
        width: 11.44px;
        height: 11.44px;
        margin-left: 12px;
        cursor: pointer;
      }
      .header-name {
        width: 14%;
        justify-content: center;
        height: 30px;
        display: flex;
        align-items: center;
        position: relative;
        .icon-question {
          width: 11.44px;
          height: 11.44px;
          margin-left: 12px;
          cursor: pointer;
          position: absolute;
          transform: translate(0, -50%);
        }
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
        color: #252424;
        font-size: 14px;
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
</style>
