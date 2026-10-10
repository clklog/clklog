<template>
  <div>
    <div class="chartsIcon">
      <div class="chartLeft public-hoverItem">
        <div class="trendHead">
          <div class="trafficHead">Top10受访页面</div>
          <div
            class="iconFlag"
            @click="$router.push('/access/visitedPage')"
          >
            >
          </div>
        </div>
        <div class="comePage">
          <!-- content -->
          <div class="bid-list-page">
            <div class="bid-list-header">
              <div class="header-text w156">受访页面</div>
              <div class="header-name w156"><b>浏览量</b></div>
              <div class="header-name w156"><b>占比</b></div>
            </div>
            <div
              class="bid-list-record"
              v-for="(item, index) in getVisitList"
              :key="index"
            >
              <div class="header-text w157">
                {{ item.uri }}
              </div>
              <div class="bid-list-item w158">
                <p>{{ item.pv }}</p>
              </div>
              <div class="bid-list-item w158">
                <p>{{ item.percent }}</p>
              </div>
            </div>

            <!-- <div class="bid-list-record">
              <div class="header-text w157">
                https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&
              </div>
              <div class="bid-list-item w158">
                <p>187.7%</p>
              </div>
              <div class="bid-list-item w158">
                <p>143.3%</p>
              </div>
            </div> -->
          </div>
        </div>
      </div>
      <div class="chartRight public-hoverItem">
        <div class="trendHead">
          <div class="trafficHead">Top10搜索词</div>
          <div
            class="iconFlag"
            @click="$router.push('/visitorAnalysis/search')"
          >
            >
          </div>
        </div>
        <div class="comePage public-hoverItem">
          <!-- content -->
          <div class="bid-list-page">
            <div class="bid-list-header">
              <div class="header-text w156">搜索词</div>
              <div class="header-name w156"><b>浏览量</b></div>
              <div class="header-name w156"><b>占比</b></div>
            </div>

            <div
              class="bid-list-record"
              v-for="(item, index) in searchWordList"
              :key="index"
            >
              <div class="header-text w157">{{ item.word }}</div>
              <div class="bid-list-item w158">
                <p>{{ item.pv }}</p>
              </div>
              <div class="bid-list-item w158">
                <p>{{ item.percent }}</p>
              </div>
            </div>
            <!-- 
            <div class="bid-list-record">
              <div class="header-text w157">百度统计</div>
              <div class="bid-list-item w158">
                <p>187.7%</p>
              </div>
              <div class="bid-list-item w158">
                <p>143.3%</p>
              </div>
            </div> -->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// import { getSearchWordApi, getVisitUriApi } from "@/api/trackingapi/flow";
import { getSearchWordApi } from "@/api/trackingapi/searchword";
import { getVisitUriApi } from "@/api/trackingapi/visituri";
export default {
  name: "comePage",
  data() {
    return {
      searchWordList: null,
      getVisitList: null,
    };
  },
  created() {
    this.getSearchWord();
    this.getVisitUri();
  },
  methods: {
    // 关键词搜索
    getSearchWord() {
      // 测试数据
      const params = {
        timeType: "day",
        channel: ["安卓", "苹果", "网站", "微信小程序"],
        startTime: "",
        endTime: "",
        startTime: "2023-06-08",
        endTime: "2023-06-10",
        projectName: "",
      };
      getSearchWordApi(params).then((res) => {
        if (res.code == 200) {
          this.searchWordList = res.data;
        }
      });
    },
    // Top10受访页面
    getVisitUri() {
      const params = {
        // 测试数据
        timeType: "day",
        channel: ["安卓", "苹果", "网站", "微信小程序"],
        startTime: "",
        endTime: "",
        startTime: "2023-06-08",
        endTime: "2023-06-10",
        projectName: "",
      };
      getVisitUriApi(params).then((res) => {
        this.getVisitList = res.data;
      });
    },
  },
};
</script>

<style lang="scss" scoped>
@import "~@/styles/home.css";
</style>
