<template>
  <div class="TopFilter">
    <div class="radio-com">
      <span class="radio-com-label">时间：</span>
      <el-radio-group v-model="timeType" class="radio-com-el">
        <el-radio label="day">今日</el-radio>
        <el-radio label="week">本周</el-radio>
        <el-radio label="month">本月</el-radio>
        <el-radio label="year">本年</el-radio>
      </el-radio-group>
    </div>

    <div class="radio-com">
      <span class="radio-com-label">渠道：</span>
      <el-radio-group class="radio-com-el" v-model="channelValue">
        <el-radio label="">全部</el-radio>
        <el-radio label="安卓">安卓</el-radio>
        <el-radio label="苹果">苹果</el-radio>
        <el-radio label="网站">网站</el-radio>
        <el-radio label="微信小程序">微信小程序</el-radio>
      </el-radio-group>
    </div>
  </div>
</template>

<script>
export default {
  name: "TopFilter",
  data() {
    return {
      timeType: "day",
      channelValue: "",
      startTime: "2023-06-07",
      endTime: "2023-06-08",
    };
  },
  computed: {
    channel() {
      return [this.channelValue];
    },
    filterParams() {
      const { timeType, channel, startTime, endTime } = this;
      return { timeType, channel, startTime, endTime };
    },
  },
  mounted() {
    this.setTopFilterParams(this.filterParams);
  },
  watch: {
    filterParams(val) {
      return this.setTopFilterParams(val);
    },
  },
  methods: {
    setTopFilterParams(val) {
      this.$emit("setTopFilterParams", val);
    },
  },
};
</script>

<style lang="scss" scoped>
@import "~@/styles/components/TopFilter.scss";
</style>
