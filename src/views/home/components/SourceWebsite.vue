<template>
  <div class="SourceWebsite block-main public-hoverItem">
    <div class="block-head" @click="$router.push('/visitorAnalysis/sourceWebAnalysis')">
      <div class="block-title">Top10来源网站</div>
      <div
        class="block-head-icon"
        
      >
        <img src="@/assets/images/icon.png" alt="" width="10px" />
      </div>
    </div>
    <div class="block-index-form">
      <el-table
        :data="SourceWebsitelist"
        :cell-style="tableHeaderColor"
        class="public-radius"
        style="width: 100%;"
      >
        <el-table-column
          align="left"
          prop="sourcesite"
          label="来源网站"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            {{ scope.row.sourcesite }}
          </template>
        </el-table-column>
        <el-table-column align="center" prop="pv" label="浏览量" width="150" />
        <el-table-column label="占比" align="center" width="150">
          <template slot-scope="scope">
            {{ percentageFun(scope.row.pvRate) }}
          </template>
        </el-table-column>
      </el-table>
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
    tableHeaderColor({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0) {
        return "text-align:left";
      } else {
        return "text-align:center";
      }
    },
    getSourceWebsite() {
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
<style lang="scss" scoped>
</style>
