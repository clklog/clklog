<template>
  <div id="app">
    <router-view />
    <clklog-letter ref="ClklogLetter"></clklog-letter>
     <dialog-page ref="dialogPage"></dialog-page>
    <modifyPsw />
  </div>
</template>

<script>
import ClklogLetter from "@/components/ClklogLetter/index";
import { profileApi } from "@/api/trackingapi/subscribe.js";
import modifyPsw from "@/components/ModifyPsw/index";
import dialogPage from "@/views/sys-manage/component/dialog";
export default {
  name: "App",
  components: { ClklogLetter, modifyPsw ,dialogPage },
  mounted() {
    this.$bus.$off("$subscription");
    this.$bus.$on("$subscription", (res) => {
      this.initClklog(res);
    });
    this.$bus.$on("$demo_event", () => {
      this.handleDemoEvent();
    });
    console.log("App mounted");
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
     handleDemoEvent() {
      this.$refs.dialogPage.dialogEvent();
    },
  },
  beforeDestroy() {
    this.$bus.$off("$subscription");
    this.$bus.$off("$demo_event");
  },
};
</script>
<style>
.el-dialog {
  border-radius: 6px
}
</style>
