<template>
  <div class="SourceWebsite block-main">
    <div class="block-head">
      <div class="block-title">Top10来源网站</div>
      <div
        class="block-head-icon"
        @click="$router.push('/visitorAnalysis/sourceWebAnalysis')"
      >
        >
      </div>
    </div>
    <div class="block-index-form">
      <!-- content -->
      <div class="form-list-page">
        <div class="form-list-header">
          <div class="header-text w156">来源网站</div>
          <div class="header-name w156"><b>浏览量(PV)</b></div>
          <div class="header-name w156"><b>占比</b></div>
        </div>

        <div
          class="form-list-record"
          v-for="(item, index) in SourceWebsitelist"
          :key="index"
        >
          <div class="header-text w157">{{ item.website || "--" }}</div>
          <div class="form-list-item w158">
            <p>{{ item.pv }}</p>
          </div>
          <div class="form-list-item w158">
            <p>{{ percentageFun(item.percent) }}</p>
          </div>
        </div>

        <!-- <div class="form-list-record">
              <div class="header-text w157">直接访问</div>
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
import { percentage } from "@/utils/percent";
import { getSourceWebsiteApi } from "@/api/trackingapi/sourcewebsite";
export default {
  name: "SourceWebsite",
  props: {
    commonParams: {},
  },
  data() {
    return {
      SourceWebsitelist: null,
    };
  },
  computed: {
    params() {
      return Object.assign({}, this.commonParams);
    },
  },
  watch: {},
  methods: {
    getSourceWebsite() {
      getSourceWebsiteApi(this.params).then((res) => {
        this.SourceWebsitelist = res.data;
      });
    },
    percentageFun(val){
        return percentage(val)
    }
  },
};
</script>

<style lang="scss" scoped>
.SourceWebsite {
}
</style>
