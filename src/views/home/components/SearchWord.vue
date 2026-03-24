<template>
  <div class="SearchWord block-main public-hoverItem">
    <div
      class="block-head"
      @click="$router.push('/access/search')"
    >
      <div class="block-title">Top10搜索词</div>
      <div class="block-head-icon">
        <img src="@/assets/images/icon.png" alt="" width="10px" />
      </div>
    </div>

    <div class="block-index-form">
      <el-table
        class="public-radius"
        :data="searchWordList"
        :cell-style="tableHeaderColor"
        style="width: 100%"
      >
        <el-table-column
          prop="word"
          label="搜索词"
          style="text-align: center;"
          align="left"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            {{ scope.row.word }}
          </template>
        </el-table-column>
        <el-table-column prop="pv" label="搜索次数" width="150"  align="center" />
        <el-table-column label="占比" width="150" align="center">
          <template slot-scope="scope">
            {{ percentageFun(scope.row.percent) }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { getSearchWordApi } from "@/api/trackingapi/searchword";
import { percentage } from "@/utils/percent";
export default {
  name: "SearchWord",
  props: {
    commonParams: {},
  },
  data() {
    return {
      searchWordList: null,
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
    headerStyle(row, column, rowIndex, columnIndex) {
      if (columnIndex === 0) {
        return "text-align:left";
      } else {
        return "text-align:center";
      }
    },
    // 关键词搜索
    getSearchWord() {
      getSearchWordApi(this.params).then((res) => {
        if (res.code == 200) {
          this.searchWordList = res.data;
        }
      });
    },
    percentageFun(val) {
      return percentage(val);
    },
  },
};
</script>

<style lang="scss" scoped></style>
