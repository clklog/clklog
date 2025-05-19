<template>
  <div>
    <el-dialog :show-close="false" :visible.sync="dialogVisible" width="1000px">
      <div>
        <div class="custom-header">
          <div>
            <div
              style="
                height: 44px;
                background: #fbfcfe;
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-radius: 6px 6px 0 0;
              "
            >
              <div
                style="
                  font-size: 16px;
                  font-weight: bold;
                  line-height: 44px;
                  margin-left: 16px;
                  color: #4d4d4d;
                "
              >
                崩溃详情信息
              </div>
              <div @click="dialogVisible = false" class="closeIcon">
                <i class="el-icon-close"></i>
              </div>
            </div>
          </div>
        </div>

        <div class="public-block" style="margin-right: 24px;">
          <div class="public-hoverItem">
            <div
              class="public-firstHead"
              style="background-color: #fff; border-radius: 6px;height: 100px;"
            >
              <trafficOverviewCommon
                :labelList="labelList"
                :width="19.6"
                :overviewList="overviewList"
              ></trafficOverviewCommon>
              <div
                class="bid-list-carded"
                v-for="(item, index) in overviewList"
                :key="index"
              >
                <div
                  class="bid-list-item w111"
                  :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
                >
                  <p>{{ item.logTime}}</p>
                </div>
                <div
                  class="bid-list-item w111"
                  :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
                >
                  <p>{{ item.appVersion }}</p>
                </div>
                <div
                  class="bid-list-item w111"
                  :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
                >
                  <p>{{ item.os }}</p>
                </div>
                <div
                  class="bid-list-item w111"
                  :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
                >
                  <p>{{ item.osVersion }}</p>
                </div>
                <div
                  class="bid-list-item w111"
                  :style="index == 0 ? 'color:#2c7be5' : 'color:#4d4d4d'"
                >
                  <p>{{ item.model }}</p>
                </div>
                
              </div>
            </div>
          </div>
        </div>

        <div
          style="
            min-height: 400px;
            max-height: 600px;
            overflow: auto;
            position: relative;
          "
        >
          <div class="userLoyalty public-hoverItem" style="margin-top: 0px;">
            <div
              style="line-height: 17px"
              v-html="filterReason(rowList.appCrashedReason)"
            ></div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import trafficOverviewCommon from "@/components/trafficOverview/index.vue";
import { sharedText } from "@/components/pointFont/index.js";
export default {
  components: {
    trafficOverviewCommon,
  },
  data() {
    return {
      dialogVisible: false,
      rowList: {},
      overviewList: [{}],
      labelList: [
        {
          name: "崩溃时间",
          tip: sharedText.visitCount,
        },
        {
          name: "应用版本",
          tip: sharedText.Crashtriggercount,
        },
        {
          name: "操作系统",
          tip: sharedText.Collapserate,
        },
        {
          name: "操作系统版本",
          tip: sharedText.visitUser,
        },
        {
          name: "设备型号",
          tip: sharedText.CrashtriggerUser,
        },
      ],
    };
  },
  methods: {
    filterReason(str) {
      return str ? str.replace(/\n/g, "<br/>") : "";
    },
    openErrorLog(row) {
      this.rowList = row;
      this.overviewList = [this.rowList];
      this.dialogVisible = true;
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep {
  .el-dialog {
    border-radius: 6px;
  }
  .el-dialog__headerbtn {
    top: 15px;
  }
  .el-dialog__header {
    padding: 0 !important;
    border-radius: 6px;
  }

  .el-dialog__body {
    padding: 0;
    background-color: #eef4fd;
    border-radius: 6px;
  }
}
.bid-list-carded {
    display: flex;
    .bid-list-item {
      width: 19.6%;
      justify-content: center;
      height: 30px;
      display: flex;
      align-items: center;
    }
    .w157 {
      color: #4d4d4d;
      font-size: 12px;
      line-height: 14px;
    }
    .w158 {
      color: #3a98e6;
      font-size: 17px;
      line-height: 17px;
    }
  }
.closeIcon {
  margin-right: 30px;
  cursor: pointer;
  font-size: 20px;
}
.userLoyalty {
  margin: 20px;
  padding: 20px;
  min-height: 400px;
  box-sizing: border-box;
  background: #fff;
  position: relative;
  border-radius: 6px !important;
}
</style>
