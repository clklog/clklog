<template>
  <div class="PageView block-main public-hoverItem">
    <div
      class="block-head"
      @click="$router.push('/visitorAnalysis/visitedPage')"
    >
      <div class="block-title">Top10受访页面</div>
      <div class="block-head-icon">
        <img src="@/assets/images/icon.png" alt="" width="10px" />
      </div>
    </div>
    <div class="block-index-form">
      <el-table
        class="public-radius"
        :data="getVisitList"
        :cell-style="tableHeaderColor"
        style="width: 100%"
      >
        <el-table-column
          align="left"
          prop="uri"
          label="受访页面"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <!-- {{ scope.row.uri }} -->
              {{ scope.row.title }}<br />
              {{ scope.row.uri }}
          </template>
        </el-table-column>
        <el-table-column align="center" prop="pv" label="浏览量" width="150" />
        <el-table-column align="center" label="占比" width="150">
          <template slot-scope="scope">
            {{ percentageFun(scope.row.percent) }}
          </template>
        </el-table-column>
      </el-table>
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
    tableHeaderColor({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0) {
        return "text-align:left";
      } else {
        return "text-align:center";
      }
    },
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
