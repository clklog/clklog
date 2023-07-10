<template>
  <div class="PageView block-main">
    <div class="block-head">
      <div class="block-title">Top10受访页面</div>
      <div
        class="block-head-icon"
        @click="$router.push('/visitorAnalysis/visitedPage')"
      >
        >
      </div>
    </div>
    <div class="block-index-form">
      <!-- content -->
      <div class="form-list-page">
        <div class="form-list-header">
          <div class="header-text w156">受访页面</div>
          <div class="header-name w156"><b>浏览量(PV)</b></div>
          <div class="header-name w156"><b>占比</b></div>
        </div>
        <div
          class="form-list-record"
          v-for="(item, index) in getVisitList"
          :key="index"
        >
          <div class="header-text w157">
            {{ item.uri || "--" }}
          </div>
          <div class="form-list-item w158">
            <p>{{ item.pv }}</p>
          </div>
          <div class="form-list-item w158">
            <!-- <p>{{ item.percent }}</p> -->
            <!-- <p>{{ percentage(item.percent) }}</p> -->
            <p>{{ percentageFun(item.percent) }}</p>
          </div>
        </div>

        <!-- <div class="form-list-record">
              <div class="header-text w157">
                https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&
              </div>
              <div class="form-list-item w158">
                <p>187.7%</p>
              </div>
              <div class="form-list-item w158">
                <p>143.3%</p>
              </div>
            </div> -->
      </div>
    </div>
  </div>
</template>

<script>
import { getVisitUriApi } from "@/api/trackingapi/visituri";
import { percentage } from "@/utils/percent";
export default {
  name: "PageView",
  props: {
    commonParams: {},
  },
  data() {
    return {
      getVisitList: null,
    };
  },
  computed: {
    params() {
      return Object.assign({}, this.commonParams);
    },
    // percentageFun(val){
    //   return function (val) {
    //      return percentage(val)
    //    }
    // }
  },
  watch: {},
  created(){
  },
  methods: {
    // Top10受访页面
    getVisitUri() {
      getVisitUriApi(this.params).then((res) => {
        this.getVisitList = res.data;
      });
    },
    percentageFun(val){
        return percentage(val)
    }
  },
};
</script>

<style lang="scss" scoped>
.PageView {
}
</style>
