<template>
  <div>
    <div class="chartsIcon">
      <div class="chartLeft public-hoverItem">
        <div class="trendHead">
          <div class="trafficHead">Top10来源网站</div>
          <div
            class="iconFlag"
            @click="$router.push('/visitorAnalysis/sourceWebAnalysis')"
          >
            >
          </div>
        </div>
        <div class="comePage">
          <!-- content -->
          <div class="bid-list-page">
            <div class="bid-list-header">
              <div class="header-text w156">搜索词</div>
              <div class="header-name w156"><b>浏览量(PV)</b></div>
              <div class="header-name w156"><b>占比</b></div>
            </div>

            <div
              class="bid-list-record"
              v-for="(item, index) in SourceWebsitelist"
              :key="index"
            >
              <div class="header-text w157">{{ item.website }}</div>
              <div class="bid-list-item w158">
                <p>{{ item.pv }}</p>
              </div>
              <div class="bid-list-item w158">
                <p>{{ item.percent }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="chartRight public-hoverItem">
        <div class="trendHead">
          <div class="trafficHead">地域分布</div>
        </div>
        <mapArea id="charts3" style="height: 100%" width="100%" />
      </div>
    </div>
  </div>
</template>

<script>
import mapArea from "@/components/Charts/mapArea";
// import { getSourceWebsiteApi } from "@/api/trackingapi/flow";
import { getSourceWebsiteApi } from "@/api/trackingapi/sourcewebsite";
export default {
  components: { mapArea },
  data() {
    return {
      SourceWebsitelist: null,
    };
  },
  created() {
    this.getSourceWebsite();
  },
  methods: {
    getSourceWebsite() {
      const params = {
        timeType: "day",
        channel: ["安卓", "苹果", "网站", "微信小程序"],
        startTime: "",
        endTime: "",
        startTime: "2023-06-08",
        endTime: "2023-06-10",
        projectName: "",
      };

      getSourceWebsiteApi(params).then((res) => {
        this.SourceWebsitelist = res.data;
      });
    },
  },
};
</script>

<style lang="scss" scoped>
@import "~@/styles/home.scss";
</style>
