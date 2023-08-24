<template>
  <div>
    <el-dialog
      :visible.sync="centerDialogVisible"
      :modal-append-to-body="false"
      width="1100px"
      top="8vh"
      @close="resetForm()"
    >
      <div class="el-dialog-div">
        <div class="left-item">
          <div class="visitorInfo commonBackgrond">
            <img src="@/assets/images/avator.jpg" alt="" />
            <div style="padding-left: 49px">
              <div class="title common-title">访客信息</div>
              <div style="display: flex">
                <div class="gap" style="padding-right: 30px">
                  <div
                    class="lable"
                    v-if="userBaseInfo && userBaseInfo.distinctId"
                  >
                    ID：
                    <!-- <span>{{ userBaseInfo.distinctId }}</span> -->
                    <el-popover
                      placement="top-start"
                      trigger="hover"
                      v-if="userBaseInfo.distinctId.length > 19"
                    >
                      <div style="font-size: 12px">
                        {{ userBaseInfo.distinctId }}
                      </div>
                      <div
                        slot="reference"
                        class="overItem"
                        style="cursor: pointer"
                      >
                        {{ userBaseInfo.distinctId || "--" }}
                      </div>
                    </el-popover>
                    <div v-else class="overItem">
                      {{ userBaseInfo.distinctId || "--" }}
                    </div>
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
                  <!-- <div class="lable" v-if="userBaseInfo.client_ip">
                    IP：
                    <span>{{ userBaseInfo.client_ip }}</span>
                  </div> -->
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
              <div
                style="
                  font-size: 12px;
                  width: 300px;
                  overflow: hidden;
                  white-space: nowrap;
                  text-overflow: ellipsis;
                  padding-top: 7px;
                "
                v-if="userBaseInfo.client_ip"
              >
                IP：
                <span>{{ userBaseInfo.client_ip }}</span>
              </div>
            </div>
          </div>
          <div class="total commonBackgrond">
            <div class="total_lable common-title">总计</div>
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
              >{{ userBaseInfo.firstTime }} <br />
              最后访问时间
              <span style="padding-right: 20px"></span>
              {{ userBaseInfo.latestTime }}
            </div>
          </div>

          <div class="device commonBackgrond">
            <span class="device-label common-title">设备</span>
            <div class="describe">
              来自智能手机设备的
              <div class="number">{{ userBaseInfo.visitCount }}</div>
              次访问：{{ userBaseInfo.manufacturer }}
            </div>
          </div>

          <div class="whereFrom commonBackgrond">
            <span class="device-label common-title"> 位置 </span>
            <div
              class="describe"
              style="display: flex; flex-direction: column; margin-top: 16px"
            >
              <div style="display: flex">
                <div
                  style="display: flex;margin-right: 10px;box-sizing: border-box;"
                  v-for="(item, index) in userBaseInfo.visitorAreaList"
                  :key="index"
                >
                  {{ item.country }}
                  <div class="number" style="white-space: nowrap;">{{ item.city }}</div>
                </div>
               
              </div>
            </div>
          </div>
        </div>
        <div class="right-item commonBackgrond">
          <div class="right-head">
            <!-- <div class="name">页面访问明细分析</div> -->
            <!-- <div class="btnEvent">
              <i
                class="el-icon-download"
                style="padding-right: 3px; font-size: 14px"
              ></i
              >下载
            </div> -->
          </div>
          <div class="warry-body">
            <div
              class="right-body"
              v-for="(item, index) in visitorSessionList"
              :key="index"
            >
              <div class="right_body-con">
                <div class="right_body_title">
                  <div class="body-left common-title">
                    访问#{{ getIndex(index) }}
                  </div>
                  <div class="body-right">
                    {{ item.firstTime }} &nbsp;&nbsp; 访问时长:{{
                      item.visitTime
                    }}
                  </div>
                </div>
                <div class="line"></div>
                <div class="right_body_http">
                  <div class="right_ip">IP：</div>
                  <div v-for="ipItem in item.rows">
                    <div
                      style="display: flex; align-items: center; height: 28px;padding-left: 23px;"
                    >
                      <span class="right_single_font"
                        >{{ ipItem.clientIp }}</span
                      >
                      <span class="right_single_font"
                        >{{ ipItem.province }}
                      </span>
                      <!-- <span class="right_single_font">{{ ipItem.pv }}次</span> -->
                    </div>
                  </div>
                  <div
                    v-loading="clickIndex == index && loading"
                    style="
                      line-height: 20px;
                      box-sizing: border-box;
                      padding-bottom: 20px;
                      cursor: pointer;
                    "
                    @click="toVisitHttp(item, index)"
                  >
                    {{ item.visitTime }}中的{{ item.pv }}个动作
                  </div>

                  <div
                    style="
                      text-align: left;
                      max-height: 500px;
                      overflow-y: auto;
                      overflow-x: hidden;
                    "
                    v-if="item.httpList.length > 0"
                  >
                    <div v-for="val in item.httpList[0]">
                      <div style="display: flex">
                        <div
                          style="
                            width: 50px;
                            box-sizing: border-box;
                            height: 100%;
                            text-align: start;
                          "
                        >
                          <i class="el-icon-link" style="font-size: 16px"></i>
                          <div
                            style="
                              width: 1px;
                              min-height: 40px;
                              border-left: 1px solid #b8b8b8;
                              margin: 5px 0 5px 6px;
                            "
                          ></div>
                        </div>
                        <div>
                          <div
                            style="
                              width: 435px;
                              min-height: 15px;
                              line-height: 18px;
                              margin-bottom: 5px;
                              color: #4d4d4d;
                            "
                          >
                            {{ val.logTime }}
                          </div>
                          <div
                            style="
                              width: 435px;
                              min-height: 15px;
                              line-height: 18px;
                              margin-bottom: 10px;
                            "
                          >
                            <!-- {{ val.uri }} -->
                            <a :href="val.uri" target="_blank">{{ val.uri }}</a>

                          </div>
                        </div>
                      </div>
                    </div>
                    <div
                      @click="tomoreEvent(item, index)"
                      v-if="
                        item.httpList.length > 0 &&
                        item.pv > item.httpList[0].length
                      "
                      class="loadMore"
                    >
                      加载更多
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="block">
            <!-- :page-sizes="[10, 15, 20]" -->
            <!-- layout=" sizes, prev, pager, next, jumper" -->
            <el-pagination
              v-if="visitorSessionList && visitorSessionList.length > 0"
              next-text="下一页"
              :pager-count="5"
              :current-page="currentPage"
              :page-size="pageSize"
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
import { formatTime } from "@/utils/format";
export default {
  data() {
    return {
      total: 0,
      // pageSize: 3,
      pageSize: 10,
      currentPage: 1,
      centerDialogVisible: false,
      userBaseInfo: {},
      visitorSessionList: null,
      distinctId: null,
      loading: false,
      clickIndex: -1,
      visitListUrl: null,
      allVisitListUrl: [],
    };
  },
  mounted() {
    // this.getVisitorSessionUriList();
  },
  methods: {
    getIndex(index) {
      // return (this.currentPage - 1) * this.pageSize + index + 1
      return this.total - ((this.currentPage - 1) * this.pageSize + index);
    },
    toVisitHttp(val, index) {
      this.visitListUrl = null; //重新赋值
      // console.log(val, index, "val----------");

      this.getVisitorSessionUriList(val);
      this.clickIndex = index;
      this.loading = true;
    },
    // 一级接口
    getVisitorSessionList() {
      let params = {
        distinctId: this.distinctId,
        pageNum: this.currentPage,
        pageSize: this.pageSize,
      };
      getVisitorSessionListApi(params).then((res) => {
        if (res.code == 200) {
          this.total = res.data.total;
          this.visitorSessionList = res.data.rows;

          this.visitorSessionList.map((item) => {
            item.httpList = [];
            item.allHttpList = [];
            item.visitTime = formatTime(Math.floor(item.visitTime));
          });
        }
      });
    },
    // 二级接口 url链接的接口
    getVisitorSessionUriList(val) {
      let params = {
        pageNum: 1,
        pageSize: val.pv,
        distinctId: val.distinctId,
        eventSessionId: val.eventSessionId,
      };
      getVisitorSessionUriListApi(params).then((res) => {
        if (res.code == 200) {
          this.loading = false;
          this.visitListUrl = res.data.rows;
          this.visitorSessionList.map((i) => {
            if (i.eventSessionId == this.visitListUrl[0].eventSessionId) {
              if (i.httpList.length > 0) {
                i.httpList = [];
                i.allHttpList = [];
                i.httpList.push(
                  JSON.parse(JSON.stringify(this.visitListUrl.slice(0, 10)))
                );
                i.allHttpList.push(
                  JSON.parse(JSON.stringify(this.visitListUrl))
                );
              } else {
                i.httpList.push(
                  JSON.parse(JSON.stringify(this.visitListUrl.slice(0, 10)))
                );
                i.allHttpList.push(
                  JSON.parse(JSON.stringify(this.visitListUrl))
                );
              }
            }
          });
          // console.log(this.visitorSessionList, "处理后一级会话");
        } else {
          this.loading = false;
        }
      });
    },
    tomoreEvent(item, index) {
      if (item.clickMore) {
        if (item.clickMore + 10 > item.pv) {
          item.clickMore = item.pv;
        } else {
          item.clickMore = item.clickMore + 10;
        }
      } else {
        item.clickMore = 20;
      }
      const copyArray = JSON.parse(JSON.stringify(this.visitorSessionList));
      const abc = JSON.parse(
        JSON.stringify(item.allHttpList[0].slice(0, item.clickMore))
      );
      copyArray[index].httpList = [abc];
      this.visitorSessionList = copyArray;
    },
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
      this.currentPage = 1;
      this.visitListUrl = null;
    },
    // user baseInfo
    getVisitorDetailinfo() {
      let params = { distinctId: this.distinctId };
      getVisitorDetailinfoApi(params).then((res) => {
        if (res.code == 200) {
          this.userBaseInfo = res.data;
          // this.userBaseInfo.avgVisitTime = formatTime(Math.floor(this.userBaseInfo.avgVisitTime))
          this.userBaseInfo.avgVisitTime = Math.floor(
            Math.floor(this.userBaseInfo.avgVisitTime) / 60
          );
          this.userBaseInfo.visitTime = Math.floor(
            Math.floor(this.userBaseInfo.visitTime) / 60
          );
          this.userBaseInfo.avgPv = Math.floor(this.userBaseInfo.avgPv);
        }
      });
    },

    // 右边里面url
    // getVisitorSessionUriList() {
    //   this.visitorSessionList.map((item) => {
    //     let params = {
    //       pageNum: 1,
    //       pageSize: 50,
    //       distinctId: this.distinctId,
    //       eventSessionId: "98816BD0-9E22-43DF-88CA-C29EFD910474",
    //     };
    //   });
    //   // let params = {
    //   //   pageNum: 1,
    //   //   pageSize: 50,
    //   //   distinctId: this.distinctId,
    //   //   eventSessionId: "98816BD0-9E22-43DF-88CA-C29EFD910474",
    //   // };
    //   getVisitorSessionUriListApi(params).then((res) => {
    //     if (res.code == 200) {
    //       // this.$refs.behaviorChart.getChannelList(res.data);
    //       this.visitorSessionList = res.data.rows;
    //     }
    //   });
    // },
  },
};
</script>
<style></style>
<style lang="scss" scoped>
@import "~@/styles/components/el-pagination.scss";
::v-deep {
  .el-dialog__header {
    background-color: #efefef;
  }
  .el-dialog__body {
    background-color: #efefef;
    padding: 15px 15px;
  }
}
.commonBackgrond {
  background-color: #fff;
}
.common-title {
  font-size: 15px;
  font-weight: 600;
  line-height: 31px;
  color: #4d4d4d !important;
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
      // background: #fafafb;
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
        // font-size: 13px;
        // font-weight: 500;
        // line-height: 13px;
        // color: #4d4d4d;
        // padding-bottom: 16px;
      }
      .lable {
        display: flex;
        font-size: 12px;
        font-weight: 500;
        line-height: 26px;
        color: #5a607f;
        white-space: nowrap;
        align-items: center;
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

      // background: #fafafb;
      border-radius: 6px;
      padding: 12px;
      .total_lable {
        // font-size: 13px;
        // font-weight: 500;
        // line-height: 13px;
        // color: #4d4d4d;
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
      margin-top: 15px;
    }
    .describe {
      box-sizing: border-box;
      display: flex;
      font-size: 12px;
      line-height: 27px;
      color: #5a607f;
      .number {
        font-size: 18px;
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
      // background: #fafafb;
      border-radius: 6px;
      display: flex;
      flex-direction: column;
    }
  }
  .right-item {
    margin-left: 15px;
    // width: 525px;
    min-height: 584px;
    // max-height: 750px;
    // height: 750px;
    // overflow-y: auto;
    // overflow-x: hidden;
    background-color: #efefef;
    // background: #fafafb;
    // border-radius: 6px;
    // padding: 12px;
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
    .warry-body {
      // height: calc(100%-52px);
      min-height: 600px;
      max-height: 725px;
      overflow-y: auto;
      overflow-x: hidden;
    }
    .right-body {
      width: 501px;
      // min-height: 40px;
      // min-height: 40px;
      // max-height: 650px;
      height: 40x;

      .right_body-con {
        padding: 5px 15px;
        box-sizing: border-box;
        width: 100%;
        background-color: #fff;
        min-height: 101px;
        margin-bottom: 20px;
        border-radius: 5px;
        .right_body_title {
          min-height: 50px;
          line-height: 50px;
          display: flex;
          justify-content: space-between;
          .body-left {
            font-size: 15px;
            font-weight: 500px;
            color: #4d4d4d;
            line-height: 50px;
          }
          .body-right {
            font-size: 13px;
            color: #4d4d4d;
          }
        }
        .line {
          border-bottom: 1px solid #efefef;
          width: 100%;
        }
        .right_body_http {
          min-height: 50px;
          line-height: 13px;
          color: #3d64e6;
          font-size: 13px;
          text-align: right;
          position: relative;
          .right_ip{
            position: absolute;
            top: 10px;
            color: #4d4d4d;
            font-size: 13px;
          }
          .right_single_font {
            box-sizing: border-box;
            font-size: 13px;
            line-height: 28px;
            color: #4d4d4d;
            margin-top: 5px;
            margin-right: 5px;
          }
          .loadMore {
            box-sizing: border-box;
            padding-bottom: 20px;
            cursor: pointer;
            font-size: 12px;
            color: #4d4d4d;
            text-align: center;
          }
        }
      }
      // background: #fafafb;
      // border: 1px solid #f0f0f5;
      // border-radius: 5px;
      // padding: 15px;
      // overflow: auto;
      // overflow-x: hidden;
      // margin-bottom: 15px;
      // .right-body-head {
      //   display: flex;
      //   justify-content: space-between;
      //   .body-left {
      //     display: -webkit-box;
      //     -webkit-box-orient: vertical;
      //     -webkit-line-clamp: 2;
      //     max-height: 30px;
      //     line-clamp: 2;
      //     font-size: 12px;
      //     font-weight: 400;
      //     line-height: 15px;
      //     color: #4d4d4d;
      //     width: 250px;
      //     overflow: hidden;
      //     text-overflow: ellipsis;
      //   }
      // }

      // .body-right {
      //   display: flex;
      //   flex-direction: column;
      //   text-align: right;
      // }
      // span {
      //   font-size: 12px;
      //   font-weight: 400;
      //   line-height: 13px;
      //   color: #4d4d4d;
      // }
      // .lined {
      //   width: 468px;
      //   height: 0px;
      //   border: 1px solid #f0f0f5;
      //   margin: 0 auto;
      // }
      // .webAddress {
      //   font-size: 12px;
      //   line-height: 20px;
      //   color: #5a607f;
      //   padding-top: 13px;
      //   padding-bottom: 26px;
      // }
      // .ipAdress {
      //   display: flex;
      //   align-items: center;
      //   font-size: 12px;
      //   font-weight: 500;
      //   line-height: 26px;
      //   color: #5a607f;
      // }
    }
  }
}
</style>
