<template>
  <div class="PageView block-main">
    <div class="block-head">
      <div class="block-title">Top10受访页面</div>
      <div
        class="block-head-icon"
        @click="$router.push('/visitorAnalysis/visitedPage')"
      >
        <img src="@/assets/images/icon.png" alt="" width="10px" />
      </div>
    </div>
    <div class="block-index-form">
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
            <el-popover
              placement="top-start"
              trigger="hover"
              v-if="item.uri && item.uri.length > 70"
            >
              <div style="font-size: 12px">
                {{ item.uri }}
              </div>
              <div slot="reference" class="overItem" style="cursor: pointer">
                {{ item.uri || "--" }}
              </div>
            </el-popover>
            <div v-else class="overItem">
              {{ item.uri || "--" }}
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
  },
  watch: {},
  created() {},
  methods: {
    getVisitUri() {
      getVisitUriApi(this.params).then((res) => {
        this.getVisitList = res.data;
      });
    },
    percentageFun(val) {
      return percentage(val);
    },
  },
};
</script>

<style lang="scss" scoped></style>
