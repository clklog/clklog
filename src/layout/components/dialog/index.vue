<template>
  <div>
    <el-dialog
      :visible.sync="centerDialogVisible"
      center
      :modal-append-to-body="false"
      width="1100px"
      @close="resetForm()"
    >
      <div class="el-dialog-div">
        <div class="left-item">
          <div class="visitorInfo">
            <img src="@/assets/images/avator.jpg" alt="" />
            <div style="padding-left: 49px">
              <div class="title">访客信息</div>
              <div style="display: flex">
                <div class="gap" style="padding-right: 30px">
                  <div
                    class="lable"
                    v-if="userBaseInfo && userBaseInfo.distinctId"
                  >
                    ID：
                    <span>{{ userBaseInfo.distinctId }}</span>
                  </div>
                  <div
                    class="lable"
                    v-if="userBaseInfo.country || userBaseInfo.city"
                  >
                    地域：
                    <span
                      >{{ userBaseInfo.country }} {{ userBaseInfo.city }}</span
                    >
                  </div>
                  <div class="lable" v-if="userBaseInfo.channel">
                    渠道：
                    <span>{{ userBaseInfo.channel }}</span>
                  </div>
                  <div class="lable" v-if="userBaseInfo.client_ip">
                    IP：
                    <span>{{ userBaseInfo.client_ip }}</span>
                  </div>
                </div>
                <div>
                  <div class="lable" v-if="userBaseInfo.visitorType">
                    类型：
                    <span>{{ userBaseInfo.visitorType }}</span>
                  </div>
                  <!-- <div class="lable">
                    性别：
                    <span>男</span>
                  </div>

                  <div class="lable">
                    年龄：
                    <span>21</span>
                  </div> -->
                </div>
              </div>
            </div>
          </div>
          <div class="total">
            <div class="total_lable">总计</div>
            <div class="total-head">
              <!-- 此用户在{{ userBaseInfo.visitCount }}次的访问中，平均访问了{{
                userBaseInfo.avgPv
              }}个界面，在货清清APP上总耗时为{{ userBaseInfo.visitTime }}分钟
              <br />用户每个界面的平均访问时长为{{
                userBaseInfo.avgVisitTime
              }}分钟<br />用户每次访平均点击的次数为{{ userBaseInfo.pv }}
            </div> -->
              此用户在{{ userBaseInfo.visitCount }}次的访问中，共访问了{{
                userBaseInfo.pv
              }}个页面，访问总耗时为{{ userBaseInfo.visitTime }}分钟。
              <br />每次访问的平均访问页数为{{
                userBaseInfo.avgPv
              }}
              页，平均访问时长为{{ userBaseInfo.avgVisitTime }}分钟
            </div>
            <div class="total-body">
              首次访问时间
              <span style="padding-right: 20px"></span
              >{{ userBaseInfo.firstTime }} 星期日 <br />
              最后访问时间
              <span style="padding-right: 20px"></span>
              {{ userBaseInfo.latestTime }}
              星期四
            </div>
          </div>

          <div class="device">
            <span class="device-label">设备</span>
            <div class="describe">
              来自智能手机设备的
              <div class="number">{{ userBaseInfo.visitCount }}</div>
              次访问：{{ userBaseInfo.manufacturer }}
            </div>
          </div>

          <div class="whereFrom">
            <span class="device-label"> 设备 </span>
            <div
              class="describe"
              style="display: flex; flex-direction: column; margin-top: 16px"
            >
              <div
                style="display: flex"
                v-for="(item, index) in userBaseInfo.visitorAreaList"
                :key="index"
              >
                <div class="number">{{ item.visitCount }}</div>
                次访问来自{{ item.country }}
                <div class="number">{{ item.city }}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="right-item">
          <div class="right-head">
            <div class="name">页面访问明细分析</div>
            <!-- <div class="btnEvent">
              <i
                class="el-icon-download"
                style="padding-right: 3px; font-size: 14px"
              ></i
              >下载
            </div> -->
          </div>
          <div
            class="right-body"
            v-for="(item, index) in visitorSessionList"
            :key="index"
          >
            <div class="right-body-head">
              <div class="body-left">访问#3</div>
              <div class="body-right">
                <span>访问时间：{{ item.firstTime }}</span>
                <span style="padding-top: 3px; padding-bottom: 13px"
                  >访问时长：{{ item.visitTime }}</span
                >
              </div>
            </div>

            <div class="lined"></div>
            <div class="webAddress">
              <div v-for="(value,index) in item.rows" :key="index">
                <div> {{ value.uri }}</div>
              </div>
              <!-- h5.huoqingqing.com/天天货清清
              <br />h5.huoqingqing.com/?wechatShare=/detail/?id=2c94825586e5d3e40186e83d3a1
              <br />35ce3$PARc$EQU$PAR -->
            </div>
            <div class="ipAdress">IP:<span>{{ item.clientIp }}</span></div>
          </div>
          <div class="block">
            <el-pagination
            v-if="visitorSessionList.length >0"
              next-text="下一页"
              :pager-count="3"
              :current-page="currentPage"
              :page-sizes="[1, 2, 3]"
              :page-size="pageSize"
              layout=" sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getVisitorDetailinfoApi,
  getVisitorSessionUriListApi,
  getVisitorSessionListApi,
} from "@/api/trackingapi/visitor";
export default {
  data() {
    return {
      total: 0,
      pageSize: 3,
      currentPage: 1,
      centerDialogVisible: false,
      userBaseInfo: {},
      visitorSessionList: {},
      distinctId: null,
    };
  },
  mounted() {
    // this.getVisitorSessionUriList();
  },
  methods: {
    handleSizeChange(val) {
      this.currentPage = 1;
      this.pageSize = val;
      this.getVisitorSessionList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getVisitorSessionList();
    },
    resetForm(val) {
      this.userBaseInfo = {};
    },
    callMethod(val) {
      this.distinctId = val;
      this.getVisitorDetailinfo();
      this.getVisitorSessionList();
      this.centerDialogVisible = true;
    },
    // user baseInfo
    getVisitorDetailinfo() {
      let params = { distinctId: this.distinctId };
      getVisitorDetailinfoApi(params).then((res) => {
        if (res.code == 200) {
          this.userBaseInfo = res.data;
        }
      });
    },
    // 右边 visit data
    getVisitorSessionList() {
      let params = {
        distinctId: this.distinctId,
        pageNum: this.currentPage,
        pageSize: this.pageSize,
      };
      getVisitorSessionListApi(params).then((res) => {
        if (res.code == 200) {
          this.total = res.data.total;
          let params = {
            pageNum: 1,
            pageSize: 10,
            distinctId: "",
            eventSessionId: "",
          };
          res.data.rows.map((item) => {
            item.rows = [];
            params.distinctId = item.distinctId;
            params.eventSessionId = item.eventSessionId;
            getVisitorSessionUriListApi(params).then((res) => {
              if (res.code == 200) {
                // console.log(res.data, "执行三次调用");
                item.rows =item.rows.concat(res.data.rows);
              }
            });
          });
          this.visitorSessionList = res.data.rows;
        }
      });
    },
    // 右边里面url
    getVisitorSessionUriList() {
      this.visitorSessionList.map((item) => {
        let params = {
          pageNum: 1,
          pageSize: 50,
          distinctId: this.distinctId,
          eventSessionId: "98816BD0-9E22-43DF-88CA-C29EFD910474",
        };
      });
      // let params = {
      //   pageNum: 1,
      //   pageSize: 50,
      //   distinctId: this.distinctId,
      //   eventSessionId: "98816BD0-9E22-43DF-88CA-C29EFD910474",
      // };
      getVisitorSessionUriListApi(params).then((res) => {
        if (res.code == 200) {
          // this.$refs.behaviorChart.getChannelList(res.data);
          this.visitorSessionList = res.data.rows;
        }
      });
    },
  },
};
</script>
<style></style>
<style lang="scss" scoped>
@import "~@/styles/components/el-pagination.scss";
::v-deep {
  .el-dialog__body {
    padding: 15px 15px;
  }
}
.el-dialog-div {
  display: flex;
  .left-item {
    min-width: 516px;
    min-height: 584px;
    max-height: 960px;
    padding: 15px 15px;
    padding-top: 0;
    .visitorInfo {
      width: 516px;
      height: 174px;
      background: #fafafb;
      border-radius: 6px;
      display: flex;
      //   align-items: center;
      padding: 24px 21px;
      box-sizing: border-box;
      img {
        width: 125px;
        height: 125px;
        background: rgba(0, 0, 0, 0);
        border-radius: 50%;
      }
      .title {
        margin-top: 6px;
        font-size: 13px;
        font-weight: 500;
        line-height: 13px;
        color: #4d4d4d;
        padding-bottom: 16px;
      }
      .lable {
        display: flex;
        font-size: 12px;
        font-weight: 500;
        line-height: 26px;
        color: #5a607f;
        white-space: nowrap;
        span {
          width: 140px;
          box-sizing: border-box;
          // width: 100%;
          overflow: hidden;
          text-overflow: ellipsis;
          font-size: 12px;
          line-height: 26px;
          color: #5a607f;
        }
      }
    }
    .total {
      margin-top: 15px;
      width: 516px;
      height: 172px;

      background: #fafafb;
      border-radius: 6px;
      padding: 12px;
      .total_lable {
        font-size: 13px;
        font-weight: 500;
        line-height: 13px;
        color: #4d4d4d;
        padding-bottom: 24px;
      }
      .total-head {
        font-size: 12px;
        line-height: 12px;
        line-height: 20px;
        color: #5a607f;
      }
      .total-body {
        padding-top: 18px;
        font-size: 13px;
        font-weight: 500;
        line-height: 13px;
        line-height: 20px;
        color: #3d64e6;
      }
    }
    .device {
      width: 516px;
      height: 76px;
      border-radius: 6px;
      padding: 12px;
      background: #fafafb;
      margin-top: 15px;
    }
    .device-label {
      font-size: 13px;
      font-weight: 500;
      line-height: 31px;
      color: #4d4d4d;
    }
    .describe {
      box-sizing: border-box;

      display: flex;
      font-size: 12px;
      line-height: 27px;
      color: #5a607f;
      .number {
        font-size: 22px;
        color: #3d64e6;
        font-weight: 500;
      }
    }
    .whereFrom {
      padding: 11px;
      box-sizing: border-box;
      margin-top: 16px;
      width: 516px;
      min-height: 116px;
      background: #fafafb;
      border-radius: 6px;
      display: flex;
      flex-direction: column;
    }
  }
  .right-item {
    margin-left: 15px;
    width: 525px;
    min-height: 570px;
    // max-height: 960px;
    // overflow-y: scroll;
    // overflow-x: hidden;
    background: #fafafb;
    border-radius: 6px;
    padding: 12px;
    .right-head {
      display: flex;
      justify-content: space-between;
      .name {
        font-size: 13px;
        font-weight: 500;
        line-height: 31px;
        color: #4d4d4d;
      }
      .btnEvent {
        cursor: pointer;
        width: 54px;
        height: 21px;
        border: 1px solid #3d64e6;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 400;
        line-height: 21px;
        text-align: center;
        color: #656d92;
      }
    }
    .right-body {
      width: 501px;
      min-height: 168px;
      background: #fafafb;
      border: 1px solid #f0f0f5;
      border-radius: 5px;
      padding: 15px;
      overflow: auto;
      overflow-x: hidden;
      margin-bottom: 15px;
      .right-body-head {
        display: flex;
        justify-content: space-between;
        .body-left {
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2;
          max-height: 30px;
          line-clamp: 2;
          font-size: 12px;
          font-weight: 400;
          line-height: 15px;
          color: #4d4d4d;
          width: 250px;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }

      .body-right {
        display: flex;
        flex-direction: column;
        text-align: right;
      }
      span {
        font-size: 12px;
        font-weight: 400;
        line-height: 13px;
        color: #4d4d4d;
      }
      .lined {
        width: 468px;
        height: 0px;
        border: 1px solid #f0f0f5;
        margin: 0 auto;
      }
      .webAddress {
        font-size: 12px;
        line-height: 20px;
        color: #5a607f;
        padding-top: 13px;
        padding-bottom: 26px;
      }
      .ipAdress {
        display: flex;
        align-items: center;
        font-size: 12px;
        font-weight: 500;
        line-height: 26px;
        color: #5a607f;
      }
    }
  }
}
</style>
