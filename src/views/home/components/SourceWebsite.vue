<template>
  <div class="SourceWebsite block-main public-hoverItem">
    <div class="block-head">
      <div class="block-title">Top10来源网站</div>
      <div
        class="block-head-icon"
        @click="$router.push('/visitorAnalysis/sourceWebAnalysis')"
      >
        <img src="@/assets/images/icon.png" alt="" width="10px" />
      </div>
    </div>
    <div class="block-index-form">
      <div class="form-list-page">
        <div class="form-list-header">
          <div class="header-text w156">来源网站</div>
          <div class="header-name w156">浏览量(PV)</div>
          <div class="header-name w156">
            占比
          </div>
        </div>
        <div
          class="form-list-record"
          v-for="(item, index) in SourceWebsitelist"
          :key="index"
        >
          <div class="header-text w157">
            <el-popover
              placement="top-start"
              trigger="hover"
              v-if="item.website && item.website.length > 70"
            >
              <div style="font-size: 12px">
                {{ item.website }}
              </div>
              <div slot="reference" class="overItem" style="cursor: pointer">
                {{ item.website || "--" }}
              </div>
            </el-popover>
            <div v-else class="overItem">
              {{ item.website || "--" }}
            </div>
          </div>
          <div class="form-list-item w158">
            <p>{{ item.pv }}</p>
          </div>
          <div class="form-list-item w158">
            <p>{{ percentageFun(item.percent) }}</p>
          </div>
        </div>
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
      console.log(this.params,"hqq");
      getSourceWebsiteApi(this.params).then((res) => {
        this.SourceWebsitelist = res.data;
      });
    },
    percentageFun(val) {
      return percentage(val);
    },
  },
};
</script>
<style lang="scss" scoped></style>
