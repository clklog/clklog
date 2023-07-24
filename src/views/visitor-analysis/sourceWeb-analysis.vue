<template>
  <div>
    <FilterBar></FilterBar>
    <div class="Overview">
      <div class="trafficHead" style="padding-left: 15px">来源网站分析</div>
      <div class="bid-list-page">
        <div class="bid-list-header">
          <div class="header-name w156">
            浏览量(PV)
            <img src="@/assets/images/question.png" alt="" />
          </div>
          <div class="header-name w156">
            访问次数<img src="@/assets/images/question.png" alt="" />
          </div>
          <div class="header-name w156">
            访客数<img src="@/assets/images/question.png" alt="" />
          </div>
          <div class="header-name w156">
            IP数<img src="@/assets/images/question.png" alt="" />
          </div>
          <div class="header-name w156">
            平均访问页面<img src="@/assets/images/question.png" alt="" />
          </div>
          <div class="header-name w156">
            平均访问时长<img src="@/assets/images/question.png" alt="" />
          </div>
          <div class="header-name w156">
            跳出率<img src="@/assets/images/question.png" alt="" />
          </div>
        </div>
        <div class="bid-list-record">
          <div class="bid-list-item w158">
            <p>18775</p>
          </div>
          <div class="bid-list-item w158">
            <p>14330</p>
          </div>
          <div class="bid-list-item w158">
            <p>877424</p>
          </div>
          <div class="bid-list-item w158">
            <p>800580</p>
          </div>
          <div class="bid-list-item w158">
            <p>1.50</p>
          </div>
          <div class="bid-list-item w158">
            <p>00:04:05</p>
          </div>
          <div class="bid-list-item w158">
            <p>82.56%</p>
          </div>
        </div>
      </div>
    </div>
    <sourceWebChart></sourceWebChart>
    <sourceWebTable></sourceWebTable>
  </div>
</template>

<script>
import { FilterBar } from "@/layout/components";
import sourceWebChart from "./chart-component/sourceWeb-analysis-chart";
import sourceWebTable from "./chart-component/sourceWeb-analysis-table";
// import { getSourceSiteDetailApi } from "@/api/sourcewebsite/visituri";
export default {
  components: {
    FilterBar,
    sourceWebChart,
    sourceWebTable
  },
};
</script>

<style lang="scss" scoped>
.Overview {
  margin: 20px;
  min-height: 118px;
  background-color: #fafafb;
  img {
    width: 11.44px;
    height: 11.44px;
    margin-left: 12px;
    cursor: pointer;
  }
  .trafficHead {
    font-size: 14px;
    font-weight: 500;
    line-height: 14px;
    color: #4d4d4d;
    padding: 15px 30px 15px;
    padding-left: 0;
  }
  .bid-list-page {
    width: clas(100% -20px);
    display: flex;
    flex-direction: column;
    .bid-list-header {
      display: flex;
      .header-name {
        width: 14%;
        justify-content: center;
        height: 30px;
        display: flex;
        align-items: center;
      }
      .w156 {
        font-size: 13px;
        font-weight: 400;
        line-height: 14px;
        color: #4d4d4d;
      }
    }
    .bid-list-record {
      display: flex;
      .bid-list-item {
        width: 14%;
        justify-content: center;
        height: 30px;
        display: flex;
        align-items: center;
      }
      .w157 {
        font-size: 12px;
        font-weight: 400;
        line-height: 14px;
        color: #4d4d4d;
      }
      .w158 {
        font-size: 16px;
        font-weight: bold;
        color: #3d64e6;
        line-height: 17px;
      }
    }
  }
}
</style>
