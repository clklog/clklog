<template>
  <div
    v-loading="loadingRefresh"
    class="documentation-container block-main public-hoverItem local-distribution"
  >
    <div class="block-head" @click="$router.push('/visitorAnalysis/regional')">
      <div class="block-title" >地域分布</div>
      <div
        class="block-head-icon"
      >
        <img src="@/assets/images/icon.png" alt="" width="10px" />
      </div>
    </div>
    <mapArea ref="mapArea" id="charts3" class="map-wrap" width="100%" />
  </div>
</template>

<script>
import mapArea from "@/components/Charts/mapArea";
import { getAreaApi } from "@/api/trackingapi/area";
export default {
  name: "LocalDistribution",
  props: {
    commonParams: {},
  },
  components: { mapArea },
  data() {
    return {
      getAreaList: null,
      loadingRefresh: false,
    };
  },
  computed: {
    params() {
      return Object.assign({}, this.commonParams);
    },
  },
  watch: {},
  methods: {
    getArea() {
      this.loadingRefresh = true;
      getAreaApi(this.params)
        .then((res) => {
          this.loadingRefresh = false;
          this.getAreaList = res.data;
          this.$refs.mapArea.getMapChartsData(res.data);
        })
        .catch(() => {
          this.loadingRefresh = false;
        });
    },
  },
};
</script>
<style lang="scss" scoped>
.local-distribution {
  min-height: 480px;
  .map-wrap {
    height: 420px;
  }
}
</style>
