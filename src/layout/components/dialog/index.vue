<template>
  <div>
    <el-dialog
      :visible.sync="centerDialogVisible"
      center
      :modal-append-to-body="false"
      width="1100px"
    >
      <div class="el-dialog-div">
        <div class="left-item">
          <div class="visitorInfo">
            <img src="@/assets/images/avator.jpg" alt="" />
            <div style="padding-left: 49px">
              <div class="title">访客信息</div>
              <div style="display: flex">
                <div class="gap" style="padding-right: 53px">
                  <div class="lable">
                    ID：
                    <span>4e2077d9166e5596</span>
                  </div>
                  <div class="lable">
                    地域：
                    <span>中国 北京</span>
                  </div>
                  <div class="lable">
                    渠道：
                    <span>安卓</span>
                  </div>
                  <div class="lable">
                    IP：
                    <span>117.21.119.21</span>
                  </div>
                </div>
                <div>
                  <div class="lable">
                    性别：
                    <span>男</span>
                  </div>
                  <div class="lable">
                    类型：
                    <span>老访客</span>
                  </div>
                  <div class="lable">
                    年龄：
                    <span>21</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="total">
            <div class="total_lable">总计</div>
            <div class="total-head">
              此用户在x次的访问中，共访问了x个界面，在货清清APP上总耗时为x分钟
              <br />用户每个界面的平均访问时长为x分钟<br />用户每次访平均点击的次数为x
            </div>
            <div class="total-body">
              首次访问时间
              <span style="padding-right: 20px"></span> 2023年5月20日 09:21:46
              星期日 <br />
              最后访问时间
              <span style="padding-right: 20px"></span> 2023年5月25日 18:21:54
              星期四
            </div>
          </div>

          <div class="device">
            <span class="device-label">设备</span>
            <div class="describe">
              来自智能手机设备的
              <div class="number">3</div>
              次访问：Huawei DVC-AN20(4X)
            </div>
          </div>

          <div class="whereFrom">
            <span class="device-label"> 设备 </span>
            <div
              class="describe"
              style="display: flex; flex-direction: column; margin-top: 16px"
            >
              <div style="display: flex">
                <div class="number">3</div>
                次访问来自中国
                <div class="number">北京</div>
                ;
              </div>

              <div style="display: flex">
                <div class="number">2</div>
                次访问来自中国
                <div class="number">上海</div>
                ;
              </div>
            </div>
          </div>
        </div>
        <div class="right-item">
          <div class="right-head">
            <div class="name">页面访问明细分析</div>
            <div class="btnEvent">
              <i
                class="el-icon-download"
                style="padding-right: 3px; font-size: 14px"
              ></i
              >下载
            </div>
          </div>
          <div class="right-body">
            <div class="right-body-head">
              <span class="body-left">访问#3</span>
              <div class="body-right">
                <span>访问时间：2023年5月25日 18:21:54</span>
                <span style="padding-top: 3px; padding-bottom: 13px"
                  >访问时长：00:21:54</span
                >
              </div>
            </div>

            <div class="lined"></div>
            <div class="webAddress">
              h5.huoqingqing.com/天天货清清
              <br />h5.huoqingqing.com/?wechatShare=/detail/?id=2c94825586e5d3e40186e83d3a1
              <br />35ce3$PARc$EQU$PAR
            </div>
            <div class="ipAdress">IP:<span>无</span></div>
          </div>

          <div class="right-body">
            <div class="right-body-head">
              <span class="body-left">访问#3</span>
              <div class="body-right">
                <span>访问时间：2023年5月25日 18:21:54</span>
                <span style="padding-top: 3px; padding-bottom: 13px"
                  >访问时长：00:21:54</span
                >
              </div>
            </div>

            <div class="lined"></div>
            <div class="webAddress">
              h5.huoqingqing.com/天天货清清
              <br />h5.huoqingqing.com/?wechatShare=/detail/?id=2c94825586e5d3e40186e83d3a1
              <br />35ce3$PARc$EQU$PAR
            </div>
            <div class="ipAdress">IP:<span>无</span></div>
          </div>
          <div class="right-body">
            <div class="right-body-head">
              <span class="body-left">访问#3</span>
              <div class="body-right">
                <span>访问时间：2023年5月25日 18:21:54</span>
                <span style="padding-top: 3px; padding-bottom: 13px"
                  >访问时长：00:21:54</span
                >
              </div>
            </div>

            <div class="lined"></div>
            <div class="webAddress">
              h5.huoqingqing.com/天天货清清
              <br />h5.huoqingqing.com/?wechatShare=/detail/?id=2c94825586e5d3e40186e83d3a1
              <br />35ce3$PARc$EQU$PAR
            </div>
            <div class="ipAdress">IP:<span>无</span></div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      centerDialogVisible: false,
    };
  },
  methods: {
    callMethod() {
      this.centerDialogVisible = true;
    },
  },
};
</script>
<style>
</style>
<style lang="scss" scoped>
::v-deep {
    .el-dialog__body {
    padding: 15px 15px;
  }
}
.el-dialog-div {
  display: flex;
  .left-item {
    min-width: 516px;
    height: 584px;
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
      margin-top: 16px;
      width: 516px;
      height: 116px;
      background: #fafafb;
      border-radius: 6px;
      display: flex;
      flex-direction: column;
    }
  }
  .right-item {
    margin-left: 15px;
    width: 525px;
    height: 570px;
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
      height: 168px;
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
