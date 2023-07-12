<template>
  <div class="LocalDistribution block-main">
    <div class="block-head">
      <div class="block-title">地域分布</div>
    </div>
    <mapArea id="charts3" style="height: 100%" width="100%" />
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
      getAreaList:null,
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
      getAreaApi(this.params).then((res) => {
        this.getAreaList = res.data;
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.LocalDistribution {
}
</style>
