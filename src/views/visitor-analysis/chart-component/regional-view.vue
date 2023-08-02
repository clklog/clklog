<template>
  <div class="Overview">
    <div class="trafficHead" style="padding-left: 15px">地域分析</div>
    <div class="bid-list-page">
      <!-- <originView ref="originView" byFlowView></originView> -->
      <div class="bid-list-header">
        <div class="header-name w156">
          浏览量(PV)
          <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              选定时间段内用户访问应用的页面总次数
              <div style="margin-top: 5px">
                用户每打开一个页面被记录一次，同一页面打开多次浏览量值累计计算。
              </div>
            </div>
            <img
              slot="reference"
              class="icon-question"
              src="@/assets/images/question.png"
              alt=""
            />
          </el-popover>
        </div>
        <div class="header-name w156">
          访问次数
          <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              选定时间段内用户访问应用的总次数。
              <div style="margin-top: 5px">
                用户首次访问或距离上次访问超过30分钟再次访问会被记录为一个新的访问。
              </div>
            </div>
            <img
              slot="reference"
              class="icon-question"
              src="@/assets/images/question.png"
              alt=""
            />
          </el-popover>
        </div>
        <div class="header-name w156">
          访客数<el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              选定时间段内访问应用的独立访客数。
              <div style="margin-top: 5px">
                访客数以Cookie或设备ID为依据，选定时间段内同一访客多次访问应用只计算为一个访客。
              </div>
            </div>
            <img
              slot="reference"
              class="icon-question"
              src="@/assets/images/question.png"
              alt=""
            />
          </el-popover>
        </div>
        <div class="header-name w156">
          IP数<el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              选定时间段内访问应用的独立IP数。
            </div>
            <img
              slot="reference"
              class="icon-question"
              src="@/assets/images/question.png"
              alt=""
            />
          </el-popover>
        </div>
        <div class="header-name w156">
          平均访问页数<el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              平均访问页数 = 浏览量 / 访问次数
            </div>
            <img
              slot="reference"
              class="icon-question"
              src="@/assets/images/question.png"
              alt=""
            />
          </el-popover>
        </div>
        <div class="header-name w156">
          平均访问时长<el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              平均访问时长 = 访问总时长 / 访问次数
              <div style="margin-top: 5px">
                访客每次访问的总时长为打开第一个页面到退出或关闭最后一个页面的总时长。
              </div>
            </div>
            <img
              slot="reference"
              class="icon-question"
              src="@/assets/images/question.png"
              alt=""
            />
          </el-popover>
        </div>
        <div class="header-name w156">
          跳出率<el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              只浏览一个页面就离开或关闭应用的访问次数占总访问次数的百分比。
            </div>
            <img
              slot="reference"
              class="icon-question"
              src="@/assets/images/question.png"
              alt=""
            />
          </el-popover>
        </div>
      </div>
      <div class="bid-list-record">
        <div class="bid-list-item w158">
          <p>{{ (detailView && detailView.pv) || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ (detailView && detailView.visitCount) || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ (detailView && detailView.uv) || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ (detailView && detailView.ipCount) || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ (detailView && detailView.avgPv) || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ (detailView && detailView.avgVisitTime) || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ (detailView && detailView.bounceRate) || "--" }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      detailView: null,
    };
  },
  created() {},
  methods: {
    getDetailView(val) {
      this.detailView = val;
    },
  },
};
</script>

<style lang="scss" scoped>
.Overview {
  margin: 20px;
  min-height: 118px;
  background-color: #fff;
  img {
    width: 11.44px;
    height: 11.44px;
    margin-left: 12px;
    cursor: pointer;
  }
  .trafficHead {
    font-size: 16px;
    font-weight: 600;
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
