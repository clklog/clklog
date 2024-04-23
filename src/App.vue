<template>
  <div id="app">
    <router-view />
    <clklog-letter ref="ClklogLetter"></clklog-letter>
  </div>
</template>

<script>
import ClklogLetter from "@/components/ClklogLetter/index";
import { profileApi, subscribeApi } from "@/api/trackingapi/subscribe.js";
export default {
  name: "App",
  components: { ClklogLetter },
  mounted() {
    this.$bus.$off("$subscription");
    this.$bus.$on("$subscription", (res) => {
      this.initClklog(res);
    });
  },
  methods: {
    initClklog(flag) {
      profileApi().then((res) => {
        if (res.code == 200) {
          setTimeout(() => {
            this.$nextTick(() => {
              this.$bus.$emit("$letter", res.data);
            });
          }, 500);
        }
      });
    },
  },
};
</script>
<style>
.el-dialog {
  border-radius: 6px
}
</style>
