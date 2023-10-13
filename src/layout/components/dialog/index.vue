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
          <div class="visitorInfo commonBackgrond public-hoverItem">
            <div class="setImg">
              <img src="@/assets/images/avator.png" alt="" />
            </div>
            <div class="visitDetail">
              <div class="title common-title">访客信息</div>
              <div style="display: flex">
                <div class="gap" style="width: 380px">
                  <div style="display: flex">
                    <div
                      class="lable"
                      style="width: 50%; padding-right: 10px"
                      v-if="userBaseInfo && userBaseInfo.distinctId"
                    >
                      ID:
                      <el-popover
                        placement="top-start"
                        trigger="hover"
                        v-if="userBaseInfo.distinctId.length > 23"
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
                      v-if="userBaseInfo.visitorType"
                      style="width: 50%; text-align: right; padding-left: 10px"
                    >
                      类型：
                      <span>{{ userBaseInfo.visitorType }}</span>
                    </div>
                  </div>

                  <div style="display: flex; justify-content: space-between">
                    <div
                      class="lable"
                      style="width: 50%; text-align: left"
                      v-if="userBaseInfo.country || userBaseInfo.city"
                    >
                      地域：
                      <span
                        >{{ userBaseInfo.country }}
                        {{ userBaseInfo.city }}</span
                      >
                    </div>
                  </div>
                  <div class="lable" v-if="userBaseInfo.channel">
                    渠道：
                    <span>{{ userBaseInfo.channel }}</span>
                  </div>
                </div>
              </div>
              <div
                style="
                  font-size: 12px;
                  width: 380px;
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

          <div class="total commonBackgrond public-hoverItem">
            <div class="total_lable common-title">总计</div>
            <div class="total-head">
              <!-- find1 -->
              此用户在{{ userBaseInfo.visitCount }}次的访问中，共访问了{{
                userBaseInfo.pv
              }}个页面， 访问总耗时为{{
                formatTimeData(userBaseInfo.visitTime)
              }}。 <br />每次访问的平均访问页数为{{
                userBaseInfo.avgPv
              }}
              页，平均访问时长为{{
                formatTimeData(userBaseInfo.avgVisitTime)
              }}。
            </div>
            <div class="visit_Time">
              <div class="firstTime">
                <span>首次访问时间</span>
                <div class="detailTime">{{ userBaseInfo.firstTime }}</div>
              </div>
              <div class="lastTime">
                <span>最后访问时间</span>
                <div class="detailTime">{{ userBaseInfo.latestTime }}</div>
              </div>
            </div>
          </div>

          <div class="device commonBackgrond public-hoverItem">
            <span class="device-label common-title">设备</span>
            <div class="describe">
              来自智能手机设备的
              <div class="number">{{ userBaseInfo.visitCount }}</div>
              次访问：{{ userBaseInfo.manufacturer }}
            </div>
          </div>

          <div class="whereFrom commonBackgrond public-hoverItem">
            <span class="device-label common-title"> 位置 </span>
            <div
              class="describe"
              style="
                display: flex;
                flex-direction: column;
                line-height: 20px;
                margin-top: 5px;
              "
            >
              <div style="display: flex">
                <div
                  style="
                    display: flex;
                    margin-right: 10px;
                    box-sizing: border-box;
                  "
                  v-for="(item, index) in userBaseInfo.visitorAreaList"
                  :key="index"
                >
                  {{ item.country }}
                  <div class="number" style="white-space: nowrap">
                    {{ item.city }}
                  </div>
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
              <div class="right_body-con public-hoverItem">
                <div class="right_body_title">
                  <div class="body-left common-title">
                    访问#{{ getIndex(index) }}
                  </div>
                  <div class="body-right">
                    {{ item.firstTime }} &nbsp;&nbsp;
                    <!-- 访问时长:{{
                      item.visitTime
                    }} -->
                  </div>
                </div>
                <div class="line"></div>
                <div class="right_body_http">
                  <div class="right_ip">IP：</div>
                  <div v-for="ipItem in item.rows">
                    <div
                      style="
                        display: flex;
                        align-items: center;
                        height: 28px;
                        padding-left: 23px;
                      "
                    >
                      <span class="right_single_font">{{
                        ipItem.clientIp
                      }}</span>
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
                              min-height: 30px;
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
                              color: #4d4d4d;
                            "
                          >
                            <div>{{ val.logTime }}</div>
                            <div>{{ val.title }}</div>
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
  methods: {
    formatTimeData(val) {
      return formatTime(Math.floor(val));
    },
    getIndex(index) {
      // return (this.currentPage - 1) * this.pageSize + index + 1
      return this.total - ((this.currentPage - 1) * this.pageSize + index);
    },
    toVisitHttp(val, index) {
      this.visitListUrl = null; //重新赋值
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
        console.log(res,"res-------");
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
    getVisitorDetailinfo() {
      let params = { distinctId: this.distinctId };
      getVisitorDetailinfoApi(params).then((res) => {
        if (res.code == 200) {
          this.userBaseInfo = res.data;
          this.userBaseInfo.avgPv = Math.floor(this.userBaseInfo.avgPv);
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
  .el-dialog__header {
    background-color: #edf2f9;
  }
  .el-dialog__body {
    background-color: #edf2f9;
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
  justify-content: center;
  // justify-content: space-between;
  .left-item {
    min-width: 516px;
    min-height: 450px;
    max-height: 960px;
    // padding: 15px 15px;
    // padding-top: 0;
    box-sizing: border-box;
    .visitorInfo {
      width: 516px;
      overflow: hidden;
      min-height: 100px;
      border-radius: 6px;
      display: flex;
      padding: 15px;
      box-sizing: border-box;
      .setImg {
        width: 90px;
        height: 100%;
        display: flex;
        margin-top: 7px;
        box-sizing: border-box;
        img {
          width: 89px;
          height: 90px;
          object-fit: cover;
          background: rgba(0, 0, 0, 0);
          border-radius: 50%;
        }
      }
      .visitDetail {
        box-sizing: border-box;
        overflow: hidden;
        min-height: 100px;
        margin-left: 20px;
      }
      .lable {
        display: flex;
        font-size: 12px;
        line-height: 26px;
        font-weight: 500;
        color: #5a607f;
        white-space: nowrap;
        align-items: center;
        text-overflow: ellipsis;
        box-sizing: border-box;
        overflow: hidden;
        white-space: nowrap;
        span {
          box-sizing: border-box;
          overflow: hidden;
          text-overflow: ellipsis;
          font-size: 12px;
          line-height: 26px;
          color: #5a607f;
        }
      }
    }
    .total {
      margin-top: 20px;
      width: 516px;
      min-height: 140px;
      border-radius: 6px;
      padding: 15px;
      .visit_Time {
        display: flex;
        padding-top: 15px;
        font-size: 13px;
        font-weight: 500;
        line-height: 13px;
        line-height: 20px;
        color: #2c7be5;
        .firstTime {
          width: 100%;
          text-align: left;
        }
        .lastTime {
          width: 100%;
          text-align: left;
        }
        .detailTime {
          padding-top: 5px;
        }
      }
      .total-head {
        font-size: 12px;
        line-height: 12px;
        line-height: 20px;
        color: #5a607f;
      }
    }
    .device {
      width: 516px;
      height: 76px;
      border-radius: 6px;
      padding: 15px;
      margin-top: 20px;
    }
    .describe {
      box-sizing: border-box;
      display: flex;
      font-size: 12px;
      line-height: 27px;
      color: #5a607f;
      .number {
        font-size: 18px;
        color: #2c7be5;
        font-weight: 500;
      }
    }
    .whereFrom {
      padding: 15px;
      box-sizing: border-box;
      margin-top: 20px;
      width: 516px;
      min-height: 50px;
      border-radius: 6px;
      display: flex;
      flex-direction: column;
    }
  }
  .right-item {
    min-width: 501px;
    margin-left: 20px;
    min-height: 584px;
    background-color: #edf2f9;
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
        border: 1px solid #2c7be5;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 400;
        line-height: 21px;
        text-align: center;
        color: #656d92;
      }
    }
    .warry-body {
      min-height: 600px;
      max-height: 725px;
      overflow-y: auto;
      overflow-x: hidden;
    }
    .right-body {
      width: 501px;
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
          border-bottom: 1px solid #edf2f9;
          width: 100%;
        }
        .right_body_http {
          min-height: 50px;
          line-height: 13px;
          color: #2c7be5;
          font-size: 13px;
          text-align: right;
          position: relative;
          .right_ip {
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
    }
  }
}
</style>
