<template>
  <div class="documentation-container block-main public-hoverItem">
    <div class="block-head" @click="$router.push('/visitorAnalysis/regional')">
      <div class="block-title" >地域分布</div>
      <div
        class="block-head-icon"
       
      >
        <img src="@/assets/images/icon.png" alt="" width="10px" />
      </div>
    </div>
    <mapArea ref="mapArea" id="charts3" style="height: 100%" width="100%" />
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
        this.$refs.mapArea.getMapChartsData(res.data);
      });
    },
  },
};
</script>
