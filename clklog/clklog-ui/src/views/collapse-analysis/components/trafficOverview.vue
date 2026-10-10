<template>
  <div class="container">
    <trafficOverview
      :labelList="labelList"
      :width="16.6"
      :overviewList="overviewList"
    ></trafficOverview>
    <div
      class="bid-list-carded"
      v-for="(item, index) in overviewList"
      :key="index"
    >
      <div
        class="bid-list-item w111"
        :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
      >
        <p>{{  item.visitCount | filterOverview }}</p>
      </div>
      <div
        class="bid-list-item w111"
        :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
      >
        <p>{{ item.crashedCount | filterOverview }}</p>
      </div>
      <div
        class="bid-list-item w111"
        :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
      >
        <p>{{ item.pvRate | percentage }}</p>
      </div>
      <div
        class="bid-list-item w111"
        :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
      >
        <p>{{ item.uv | filterOverview }}</p>
      </div>
      <div
        class="bid-list-item w111"
        :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
      >
        <p>{{ item.crashedUv | filterOverview }}</p>
      </div>
      <div
        class="bid-list-item w111"
        :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
      >
        <p>{{ item.uvRate | percentage }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import trafficOverview from "@/components/trafficOverview/index.vue";
import { sharedText } from "@/components/pointFont/index.js";
export default {
  components: {
    trafficOverview,
  },
  data() {
    return {
      overviewList: [{}],
      labelList: [
        {
          name: "访问次数",
          tip: sharedText.visitCount,
        },
        {
          name: "崩溃触发次数",
          tip: sharedText.Crashtriggercount,
        },
        {
          name: "崩溃率",
          tip: sharedText.Collapserate,
        },
        {
          name: "访问用户数",
          tip: sharedText.visitUser,
        },
        {
          name: "崩溃触发用户数",
          tip: sharedText.CrashtriggerUser,
        },
        {
          name: "崩溃触发用户数占比",
          tip: sharedText.CrashtriggerUserRate,
        },
      ],
    };
  },
  methods: {
    dataListEvent(val) {
      this.overviewList = val;
    },
  },
};
</script>

<style lang="scss" scoped>
.container {
  .bid-list-carded {
    display: flex;
    .bid-list-item {
      width: 16.6%;
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
</style>
