<template>
  <div class="FlowView block-main">
    <div class="block-head">
      <div class="block-title">流量概览</div>
    </div>
    <div class="bid-list-page">
      <div class="bid-list-header">
        <div class="header-name w156"></div>
        <div class="header-name w156">
          浏览量(PV)
          <img
            class="icon-question"
            src="@/assets/images/question.png"
            alt=""
          />
        </div>
        <div class="header-name w156">
          访问次数<img
            class="icon-question"
            src="@/assets/images/question.png"
            alt=""
          />
        </div>
        <div class="header-name w156">
          访问数<img
            class="icon-question"
            src="@/assets/images/question.png"
            alt=""
          />
        </div>
        <div class="header-name w156">
          IP数<img
            class="icon-question"
            src="@/assets/images/question.png"
            alt=""
          />
        </div>
        <div class="header-name w156">
          平均访问页面<img
            class="icon-question"
            src="@/assets/images/question.png"
            alt=""
          />
        </div>
        <div class="header-name w156">
          平均访问时长<img
            class="icon-question"
            src="@/assets/images/question.png"
            alt=""
          />
        </div>
        <div class="header-name w156">
          跳出率<img
            class="icon-question"
            src="@/assets/images/question.png"
            alt=""
          />
        </div>
      </div>
      <div class="bid-list-record">
        <div class="bid-list-item1 w157">
          <p>今日</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ current.pv || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ current.visit || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ current.uv || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ current.ipCount || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ current.avgPv || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ current.avgVisitTime || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ current.bounceRate || "--" }}</p>
        </div>
      </div>
      <!-- 昨日数据 -->
      <div class="bid-list-record">
        <div class="bid-list-item1 w157">
          <p>昨日</p>
        </div>
        <div class="bid-list-item w157">
          <p>{{ previous.pv || "--" }}</p>
        </div>
        <div class="bid-list-item w157">
          <p>{{ previous.visit || "--" }}</p>
        </div>
        <div class="bid-list-item w157">
          <p>{{ previous.uv || "--" }}</p>
        </div>
        <div class="bid-list-item w157">
          <p>{{ previous.ipCount || "--" }}</p>
        </div>
        <div class="bid-list-item w157">
          <p>{{ previous.avgPv || "--" }}</p>
        </div>
        <div class="bid-list-item w157">
          <p>{{ previous.avgVisitTime || "--" }}</p>
        </div>
        <div class="bid-list-item w157">
          <p>{{ previous.bounceRate || "--" }}</p>
        </div>
      </div>
      <div class="bid-list-record">
        <div class="bid-list-item1 w157">
          <p>环比（今日与昨日）</p>
        </div>
        <div class="bid-list-item w157">
          <p>{{ huanbi(current.pv, previous.pv) }}</p>
          <p v-if="huanbi(current.pv, previous.pv) != '--'">%</p>
          <img v-if="huanbi(current.pv, previous.pv) != '--' && huanbi(current.pv, previous.pv) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="huanbi(current.pv, previous.pv) != '--' && huanbi(current.pv, previous.pv) > 0" class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>
        <div class="bid-list-item w157">
          <p>{{ huanbi(current.visit, previous.visit) }}</p>
          <p v-if="huanbi(current.visit, previous.visit) != '--'">%</p>
          <img v-if="huanbi(current.visit, previous.visit) != '--' && huanbi(current.visit, previous.visit) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="huanbi(current.visit, previous.visit) != '--' && huanbi(current.visit, previous.visit) > 0" class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>
        <div class="bid-list-item w157">
          <p>{{ huanbi(current.uv, previous.uv) }} </p>
          <p v-if="huanbi(current.uv, previous.uv) != '--'">%</p>
          <img v-if="huanbi(current.uv, previous.uv) != '--' && huanbi(current.uv, previous.uv) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="huanbi(current.uv, previous.uv) != '--' && huanbi(current.uv, previous.uv) > 0"  class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>
        <div class="bid-list-item w157">
          <p>{{ huanbi(current.ipCount, previous.ipCount) }} </p>
          <p v-if="huanbi(current.ipCount, previous.ipCount) != '--'">%</p>
          <img v-if="huanbi(current.ipCount, previous.ipCount) != '--' && huanbi(current.ipCount, previous.ipCount) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="huanbi(current.ipCount, previous.ipCount) != '--' && huanbi(current.ipCount, previous.ipCount) > 0"  class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>
        <div class="bid-list-item w157">
          <p>{{ huanbi(current.avgPv, previous.avgPv) }}</p>
          <p v-if="huanbi(current.avgPv, previous.avgPv) != '--'">%</p>
          <img v-if="huanbi(current.avgPv, previous.avgPv) != '--' && huanbi(current.avgPv, previous.avgPv) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="huanbi(current.avgPv, previous.avgPv) != '--' && huanbi(current.avgPv, previous.avgPv) > 0"  class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>
        <div class="bid-list-item w157">
          <p>{{ huanbi(current.avgVisitTime, previous.avgVisitTime) }}</p>
          <p v-if="huanbi(current.avgVisitTime, previous.avgVisitTime) != '--'">%</p>
          <img v-if="huanbi(current.avgVisitTime, previous.avgVisitTime) != '--' && huanbi(current.avgVisitTime, previous.avgVisitTime) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="huanbi(current.avgVisitTime, previous.avgVisitTime) != '--' && huanbi(current.avgVisitTime, previous.avgVisitTime) > 0"  class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>
        <div class="bid-list-item w157">
          <p>{{ huanbi(current.bounceRate, previous.bounceRate) }}</p>
          <p v-if="huanbi(current.bounceRate, previous.bounceRate) != '--'">%</p>
          <img v-if="huanbi(current.bounceRate, previous.bounceRate) != '--' && huanbi(current.bounceRate, previous.bounceRate) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="huanbi(current.bounceRate, previous.bounceRate) != '--' && huanbi(current.bounceRate, previous.bounceRate) > 0"  class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>
      </div>
      <div class="bid-list-record" style="margin-bottom: 18px">
        <div class="bid-list-item1 w157">
          <p>同比（今日与同期）</p>
        </div>
        <div class="bid-list-item w157">
          <p>{{ tongbi(current.pv, samePeriod.pv) }}</p>
          <p v-if="tongbi(current.pv, samePeriod.pv) != '--'">%</p>
          <img  v-if="tongbi(current.pv, samePeriod.pv) != '--' && tongbi(current.pv, samePeriod.pv) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="tongbi(current.pv, samePeriod.pv) != '--' && tongbi(current.pv, samePeriod.pv) > 0" class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>

        <div class="bid-list-item w157">
          <p>{{ tongbi(current.visit, samePeriod.visit) }}</p>
          <p v-if="tongbi(current.visit, samePeriod.visit) != '--'">%</p>
          <img  v-if="tongbi(current.visit, samePeriod.visit) != '--' && tongbi(current.visit, samePeriod.visit) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="tongbi(current.visit, samePeriod.visit) != '--' && tongbi(current.visit, samePeriod.visit) > 0" class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>
        <div class="bid-list-item w157">
          <p>{{ tongbi(current.uv, samePeriod.uv) }}</p>
          <p v-if="tongbi(current.uv, samePeriod.uv) != '--'">%</p>
          <img  v-if="tongbi(current.uv, samePeriod.uv) != '--' && tongbi(current.uv, samePeriod.uv) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="tongbi(current.uv, samePeriod.uv) != '--' && tongbi(current.uv, samePeriod.uv) > 0" class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>
        <div class="bid-list-item w157">
          <p>{{ tongbi(current.ipCount, samePeriod.ipCount) }}</p>
          <p v-if="tongbi(current.ipCount, samePeriod.ipCount) != '--'">%</p>
          <img  v-if="tongbi(current.ipCount, samePeriod.ipCount) != '--' && tongbi(current.ipCount, samePeriod.ipCount) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="tongbi(current.ipCount, samePeriod.ipCount) != '--' && tongbi(current.ipCount, samePeriod.ipCount) > 0" class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>
        <div class="bid-list-item w157">
          <p>{{ tongbi(current.avgPv, samePeriod.avgPv) }}</p>
          <p v-if="tongbi(current.avgPv, samePeriod.avgPv) != '--'">%</p>
          <img  v-if="tongbi(current.avgPv, samePeriod.avgPv) != '--' && tongbi(current.avgPv, samePeriod.avgPv) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="tongbi(current.avgPv, samePeriod.avgPv) != '--' && tongbi(current.avgPv, samePeriod.avgPv) > 0" class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>
        <div class="bid-list-item w157">
          <p>{{ tongbi(current.avgVisitTime, samePeriod.avgVisitTime) }}</p>
          <p v-if="tongbi(current.avgVisitTime, samePeriod.avgVisitTime) != '--'">%</p>
          <img  v-if="tongbi(current.avgVisitTime, samePeriod.avgVisitTime) != '--' && tongbi(current.avgVisitTime, samePeriod.avgVisitTime) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="tongbi(current.avgVisitTime, samePeriod.avgVisitTime) != '--' && tongbi(current.avgVisitTime, samePeriod.avgVisitTime) > 0" class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>
        <div class="bid-list-item w157">
          <p>{{ tongbi(current.bounceRate, samePeriod.bounceRate) }}</p>
          <p v-if="tongbi(current.bounceRate, samePeriod.bounceRate) != '--'">%</p>
          <img  v-if="tongbi(current.bounceRate, samePeriod.bounceRate) != '--' && tongbi(current.bounceRate, samePeriod.bounceRate) < 0" class="declineIcon" src="@/assets/images/decline.png" alt="">
          <img v-if="tongbi(current.bounceRate, samePeriod.bounceRate) != '--' && tongbi(current.bounceRate, samePeriod.bounceRate) > 0" class="riseImg" src="@/assets/images/rise.png" alt="">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getFlowApi } from "@/api/trackingapi/flow";
export default {
  name: "FlowView",
  props: {
    commonParams: {},
  },
  data() {
    return {
      iconShow:true,
      flowData: null, //流量概览
    };
  },
  computed: {
    params() {
      return Object.assign({}, this.commonParams);
    },
    previous() {
      return this.flowData && this.flowData.previous
        ? this.flowData.previous
        : {};
    },
    current() {
      return this.flowData && this.flowData.current
        ? this.flowData.current
        : {};
    },
    samePeriod() {
      return this.flowData && this.flowData.samePeriod
        ? this.flowData.samePeriod
        : {};
    },
  },
  watch: {},
  methods: {
    getFlow() {
      getFlowApi(this.params).then((res) => {
        this.flowData = res.data;
      });
    },
    huanbi(c, p) {
      if (c && p) {
        return ((c - p) / p).toFixed(2) ;
      }
      return "--";
    },
    tongbi(c, s) {
      if (c && s) {
        return ((c - s) / s).toFixed(2);
      }
      return "--";
    },
  },
};
</script>

<style lang="scss" scoped>
.FlowView {
  margin: 20px;
  min-height: 200px;
  background-color: #fafafb;
  .bid-list-page {
    width: clas(100% -20px);
    display: flex;
    flex-direction: column;
    .bid-list-header {
      display: flex;
      .header-name {
        width: 12.5%;
        justify-content: center;
        height: 30px;
        display: flex;
        align-items: center;
        .icon-question {
          width: 11.44px;
          height: 11.44px;
          margin-left: 12px;
          cursor: pointer;
        }
      }
      .w156 {
        // font-size: 13px;
        // color: #3f3e3e;
        font-size: 13px;
        font-weight: 400;
        line-height: 14px;
        color: #4d4d4d;
      }
    }
    .bid-list-record {
      display: flex;
      .bid-list-item1 {
        width: 12.5%;
        justify-content: flex-start;
        height: 30px;
        display: flex;
        align-items: center;
      }
      .bid-list-item {
        width: 12.5%;
        justify-content: center;
        height: 30px;
        display: flex;
        align-items: center;
      }
      .declineIcon{
        width: 9px;
        height: 11.24px;
        margin-left: 7px;
      }
      .riseImg{
        width: 9px;
        height: 11.24px;
        margin-left: 7px;
      }
      .w157 {
        // color: #252424;
        // font-size: 14px;
        // line-height: 14px;
        font-size: 12px;
        font-weight: 400;
        line-height: 14px;
        color: #4d4d4d;
      }
      .w158 {
        font-size: 16px;
        font-weight: bold;
        color: #3d64e6;
        line-height: 17px;
      }
    }
  }
}
</style>
