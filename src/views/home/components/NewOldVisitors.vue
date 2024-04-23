<template>
  <div class="NewOldVisitors block-main public-hoverItem">
    <div
      class="block-head"
      @click="$router.push('/visitorAnalysis/newoldVisitor')"
    >
      <div class="block-title">新老访客</div>
      <div class="block-head-icon">
        <img src="@/assets/images/icon.png" alt="" width="10px" />
      </div>
    </div>
    <div class="pieShow">
      <div class="visitor">
        <!-- content -->
        <div class="bid-list-page">
          <div class="bid-list-header" style="padding-bottom: 6px">
            <div class="header-name w157"></div>
            <div class="header-name w157">老访客</div>
            <div class="header-name w157">新访客</div>
          </div>
          <div class="bid-list-record">
            <div class="bid-list-item w157"></div>
            <div class="bid-list-item w158">
              <p class="setGrey">
                {{ newVisitorCompare(oldVisitor.uv, newVisitor.uv) }}
              </p>
            </div>
            <div class="bid-list-item w158">
              <p>{{ oldVisitorCompare(newVisitor.uv, oldVisitor.uv) }}</p>
            </div>
          </div>
          <div>
            <div class="bid-list-record">
              <div class="bid-list-item w157 lettSpace">
                <p>浏览量</p>
              </div>
              <div class="bid-list-item w159 lettSpace">
                <p class="setGrey">
                  {{ oldVisitor.pv || "--" }}
                </p>
              </div>
              <div class="bid-list-item w159 lettSpace">
                <p>{{ newVisitor.pv || "--" }}</p>
              </div>
            </div>

            <div class="bid-list-record">
              <div class="bid-list-item w157 lettSpace">
                <p>访客数</p>
              </div>
              <div class="bid-list-item w159 lettSpace">
                <p class="setGrey">
                  {{ oldVisitor.uv || "--" }}
                </p>
              </div>
              <div class="bid-list-item w159 lettSpace">
                <p>{{ newVisitor.uv || "--" }}</p>
              </div>
            </div>

            <div class="bid-list-record">
              <div class="bid-list-item w157 lettSpace">
                <p>跳出率</p>
              </div>
              <div class="bid-list-item w159 lettSpace">
                <p class="setGrey" v-if="oldVisitor.bounceRate">
                  {{ percentageFun(oldVisitor.bounceRate) }}
                </p>
                <p v-else>--</p>
              </div>
              <div class="bid-list-item w159 lettSpace">
                <p v-if="newVisitor.bounceRate">
                  {{ percentageFun(newVisitor.bounceRate) }}
                </p>
                <p v-else>--</p>
              </div>
            </div>

            <div class="bid-list-record">
              <div class="bid-list-item w157 lettSpace">
                <p>平均访问时长</p>
              </div>
              <div class="bid-list-item w159 lettSpace">
                <p class="setGrey">
                  {{ formatTimeFun(oldVisitor.avgVisitTime) || "--" }}
                </p>
              </div>
              <div class="bid-list-item w159 lettSpace">
                <p>{{ formatTimeFun(newVisitor.avgVisitTime) || "--" }}</p>
              </div>
            </div>
            <div class="bid-list-record">
              <div class="bid-list-item w157 lettSpace">
                <p>平均访问页数</p>
              </div>
              <div class="bid-list-item w159 lettSpace">
                <p class="setGrey">
                  {{ Math.floor(oldVisitor.avgPv) || "--" }}
                </p>
              </div>
              <div class="bid-list-item w159 lettSpace">
                <p>{{ Math.floor(newVisitor.avgPv) || "--" }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="pieIcon">
        <pieChart
          id="charts2"
          ref="charts2"
          style="height: 350px"
          width="100%"
        />
      </div>
    </div>
  </div>
</template>

<script>
import pieChart from "@/components/Charts/pieMarker.vue";
import { getVisitorApi } from "@/api/trackingapi/visitor";
import { percentage } from "@/utils/percent";
import { formatTime } from "@/utils/format";
export default {
  name: "NewOldVisitors",
  components: { pieChart },
  props: {
    commonParams: {},
  },
  data() {
    return {
      visitorList: null,
      newVistorPercent: null,
      oldVistorPercent: null,
    };
  },
  computed: {
    params() {
      return Object.assign({}, this.commonParams);
    },
    newVisitor() {
      return this.visitorList && this.visitorList.newVisitor
        ? this.visitorList.newVisitor
        : {};
    },
    oldVisitor() {
      return this.visitorList && this.visitorList.oldVisitor
        ? this.visitorList.oldVisitor
        : {};
    },
    vistorPercent() {
      const { newVistorPercent, oldVistorPercent } = this;
      return Object.assign(
        { newVistorPercent, oldVistorPercent },
        this.commonParams
      );
    },
  },
  watch: {
    vistorPercent(val) {
      this.$refs.charts2.getPercentData(val);
    },
  },
  methods: {
    formatTimeFun(val) {
      return formatTime(Math.floor(val));
    },
    percentageFun(val) {
      return percentage(val);
    },
    getVisitor() {
      getVisitorApi(this.params).then((res) => {
        if (res.code == 200) {
          this.visitorList = res.data;
        }
      });
    },
    oldVisitorCompare(old, p) {
      if (old && p) {
        this.newVistorPercent = ((old / (old + p)) * 100).toFixed(2) + "%";
        return this.newVistorPercent;
      }
      return "--", (this.newVistorPercent = "");
    },
    newVisitorCompare(xin, s) {
      if (xin && s) {
        this.oldVistorPercent = ((xin / (xin + s)) * 100).toFixed(2) + "%";
        return this.oldVistorPercent;
      }
      return "--", (this.oldVistorPercent = "");
    },
  },
};
</script>

<style lang="scss" scoped>
.setGrey {
  // color: #9b9c9c;
  color: #fd7e14;
}
.w158 {
  color: #2c7be5;
  font-size: 17px;
  line-height: 17px;
  // font-weight: bold;
}
.NewOldVisitors {
  .pieShow {
    display: flex;
    width: 100%;
    height: calc(100% - 45px);
    .visitor {
      width: 60%;
      .bid-list-page {
        width: clas(100% -20px);
        display: flex;
        flex-direction: column;
        .bid-list-header {
          display: flex;
          .header-name {
            color: #4d4d4d;
            width: 33%;
            justify-content: center;
            height: 30px;
            display: flex;
            align-items: center;
          }
          .w156 {
            font-size: 15px;
            color: #5f5e5e;
          }
        }
        .bid-list-record {
          .lettSpace {
            margin-top: 10px;
          }
          display: flex;
          .bid-list-item {
            width: 33%;
            justify-content: center;
            height: 30px;
            display: flex;
            align-items: center;
          }
          .w157 {
            // color: #252424;
            color: #4d4d4d;
            font-size: 13px;
            line-height: 14px;
          }
          .w159 {
            color: #252424;
            font-size: 13px;
            line-height: 14px;
            color: #2c7be5;
          }
        }
      }
    }
    .pieIcon {
      width: 40%;
      height: 100%;
    }
  }
}
</style>
