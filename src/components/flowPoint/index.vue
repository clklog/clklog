<template>
  <!-- 公用流量指标 -->
  <div class="flow-indicator">
    <div class="flow-item">
      <div class="flow-title">流量基础指标</div>
      <el-checkbox-group
        v-model="channelList"
        class="checkBoxStyle"
        @change="handelChannelList"
      >
        <el-checkbox label="pv">浏览量(PV)</el-checkbox>
        <el-checkbox label="pvRate">浏览量占比</el-checkbox>
        <el-checkbox label="visitCount">访问次数</el-checkbox>
        <el-checkbox v-if="byChannel" label="visitCountRate"
          >访问次数占比</el-checkbox
        >
        <el-checkbox label="uv">访客数</el-checkbox>
        <el-checkbox v-if="byChannel" label="uvRate">访客数占比</el-checkbox>
        <el-checkbox label="newUv">新访客数</el-checkbox>
        <el-checkbox label="newUvRate">新访客数占比</el-checkbox>
        <el-checkbox label="ipCount" style="margin-right: 0">IP数</el-checkbox>
        <el-checkbox
          v-if="byChannel"
          label="ipCountRate"
          style="margin-right: 0"
          >IP数占比</el-checkbox
        >
      </el-checkbox-group>
    </div>
    <div class="flow-item setSpace">
      <div class="flow-title">流量质量指标</div>
      <el-checkbox-group
        v-model="flowQuality"
        class="checkBoxStyle"
        @change="handelFlowQuality"
      >
        <el-checkbox label="bounceRate">跳出率</el-checkbox>
        <el-checkbox label="avgVisitTime">平均访问时长</el-checkbox>
        <el-checkbox v-if="byChannel" label="avgVisitTimeRate"
          >平均访问时长占比</el-checkbox
        >
        <el-checkbox label="avgPv">平均访问页数</el-checkbox>
        <el-checkbox v-if="byChannel" label="avgPvRate"
          >平均访问页数占比</el-checkbox
        >
      </el-checkbox-group>
    </div>
  </div>
</template>

<script>
import { percentage } from "@/utils/percent";
export default {
  props: {
    byChannel: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      flowTableList: [],
      channelList: ["pv", "visitCount", "newUvRate", "pvRate"],
      flowQuality: ["avgPv"],
      pv: false,
      visitCount: false,
      newUv: false,
      uv: false,
      ipCount: false,
      avgPv: false,
      avgVisitTime: false,
      bounceRate: false,
      pvRate: false,
      newUvRate: false,
      mergedArr: [],
    };
  },
  mounted() {
    this.initShowTable();
  },
  methods: {
    handelChannelList() {
      this.initShowTable();
    },
    handelFlowQuality() {
      this.initShowTable();
    },
    percentageFun(val) {
      return percentage(val);
    },
    initShowTable() {
      this.mergedArr = [];
      this.mergedArr = [...this.channelList, ...this.flowQuality];
      this.$emit("flowPoint", this.mergedArr);
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep {
  @import "~@/styles/components/el-checkbox.scss";
  @import "~@/styles/components/el-pagination.scss";
}
.chartsIcon {
  box-sizing: border-box;
  margin: 20px;
  padding-top: 1px;
  min-height: 461px;
  background: rgba(250, 250, 251);
  border-radius: 6px;
}
</style>
