<template>
  <div>
    <div v-if="byFlowView || byChannel">
        <div class="bid-list-header">
        <div class="header-name w156" v-if="!byChannel"></div>
        <div class="header-name w156">
          浏览量
          <div class="parentPosition">
            <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              选定时间段内用户访问应用的页面总次数。
              <div class="setFontTop">
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
        
        </div>
        <div class="header-name w156">
          访问次数
          <div class="parentPosition">
            <el-popover
              placement="bottom-start"
              width="200"
              trigger="hover"
              class="popImg"
            >
              <div style="padding: 5px; font-size: 12px">
                选定时间段内用户访问应用的总次数。
                <div class="setFontTop">
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
        </div>
        <div class="header-name w156">
          访客数
          <div class="parentPosition">
            <el-popover
              placement="bottom-start"
              width="200"
              trigger="hover"
              class="popImg"
            >
              <div style="padding: 5px; font-size: 12px">
                选定时间段内访问应用的独立访客数。
                <div class="setFontTop">访客数以Cookie或设备ID为依据，选定时间段内同一访客多次访问应用只计算为一个访客。
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
        </div>
        <div class="header-name w156">
          IP数
          <div class="parentPosition">
            <el-popover
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
        </div>
        <div class="header-name w156">
          平均访问页数
          <div class="parentPosition">
            <el-popover
              placement="bottom-start"
              width="200"
              trigger="hover"
              class="popImg"
            >
              <div style="padding: 5px; font-size: 12px">
                平均访问页数 = 浏览量 / 访问次数 。
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
        <div class="header-name w156">
          平均访问时长
          <div class="parentPosition">
          <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              平均访问时长 = 访问总时长 / 访问次数 。
              <div class="setFontTop"> 访客每次访问的总时长为打开第一个页面到退出或关闭最后一个页面的总时长。
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
        </div>
        <div class="header-name w156">
          跳出率
          <div class="parentPosition">
          <el-popover
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
      </div>
    </div>

    <!-- 地域分析 -->
    <div class="bid-list-page" v-if="byAreaAnaly">
        <div class="bid-list-header">
        <div class="header-name w156">
          浏览量(PV)
          <div class="parentPosition">
          <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              选定时间段内用户访问应用的页面总次数。
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
        </div>
        <div class="header-name w156">
          访问次数
          <div class="parentPosition">
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
        </div>
        <div class="header-name w156">
          访客数
          <div class="parentPosition">
          <el-popover
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
        </div>
        <div class="header-name w156">
          IP数
          <div class="parentPosition">
          <el-popover
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
        </div>
        <div class="header-name w156">
          平均访问页数
          <div class="parentPosition">
          <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              平均访问页数 = 浏览量 / 访问次数。
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
        <div class="header-name w156">
          平均访问时长
          <div class="parentPosition">
          <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              平均访问时长 = 访问总时长 / 访问次数。
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
        </div>
        <div class="header-name w156">
          跳出率
          <div class="parentPosition">
          <el-popover
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
      </div>
      <div class="bid-list-record" v-if="originData">
        <div class="bid-list-item w158">
          <p>{{ (originData && originData.pv) || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ (originData && originData.visitCount) || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ (originData && originData.uv) || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ (originData && originData.ipCount) || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ averageRulesEvent(originData.avgPv) || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ formatTimeFun(originData && originData.avgVisitTime) || "--" }}</p>
        </div>
        <div class="bid-list-item w158">
          <p>{{ percentageFun(originData && originData.bounceRate)|| "--" }}</p>
        </div>
      </div>
    </div>

    <div class="bid-list-page" v-if="byVisit">
        <div class="bid-list-header">
        <div class="header-name w156">
            访客数
            <div class="parentPosition">
          <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
                选定时间段内访问应用的独立访客数。
              <div class="setFontTop">
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
        </div>
        <div class="header-name w156">
            新访客数
            <div class="parentPosition">
          <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
                选定时间段内的独立访客中，历史以来第一次访问应用的访客数。
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
        <div class="header-name w156">
            新访客数占比
            <div class="parentPosition">
            <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
                新访客占比 = 新访客数 / 访客数（UV）。
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
        <div class="header-name w156">
            回流用户
            <div class="parentPosition">
            <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
                老访客中，当天访问但前一天未访问的独立访客数。
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
      
        <div class="header-name w156">
            沉默用户
            <div class="parentPosition">
            <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
                老访客中，当天未访问但前一天有访问的独立访客数。
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
        <div class="header-name w156">
            流失用户
            <div class="parentPosition">
            <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
                选定时间段内每天流失的独立访客数的总和。
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
      </div>
      <div class="bid-list-record">
            <div class="bid-list-item w158">
              <p>{{ (originData && originData.uv) || "--" }}</p>
            </div>
            <div class="bid-list-item w158">
              <p>
                {{ (originData && originData.newUv) || "--" }}
              </p>
            </div>
            <div class="bid-list-item w158">
              <p>{{ percentageFun(originData && originData.visitRate)|| "--" }}</p>
            </div>
            <div class="bid-list-item w158">
              <p>
                {{
                  (originData && originData.revisit) || "--"
                }}
              </p>
            </div>
            <div class="bid-list-item w158">
              <p>{{ (originData && originData.silent) || "--" }}</p>
            </div>
           
            <div class="bid-list-item w158">
              <p>
                <p v-if="originData && originData.churn">{{ originData.churn }}</p>
                <p v-else>--</p>
              </p>
            </div>
      </div>
    </div>

    <div class="bid-list-page" v-if="byVisitedPage">
        <div class="bid-list-header">
        <div class="header-name w156">
          浏览量(PV)
          <div class="parentPosition">
          <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
        
            <div style="padding: 5px; font-size: 12px">
              选定时间段内用户访问应用的页面总次数。
              <div class="setFontTop">
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
        </div>
        <div class="header-name w156">
          访客数
          <div class="parentPosition">
          <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              选定时间段内访问应用的独立访客数。
              <div class="setFontTop">访客数以Cookie或设备ID为依据，选定时间段内同一访客多次访问应用只计算为一个访客。
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
        </div>
        <div class="header-name w156">
            贡献下游浏览量
            <div class="parentPosition">
            <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
                选定时间段内的受访页面给站内其他页面直接带去的浏览量。
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
        <div class="header-name w156">
            退出页次数
            <div class="parentPosition">
            <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
                选定时间段内的每次用户退出前访问的最后一个页面的访问次数总和。
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
      
        <div class="header-name w156">
            平均访问时长
            <div class="parentPosition">
            <el-popover
            placement="bottom-start"
            width="200"
            trigger="hover"
            class="popImg"
          >
            <div style="padding: 5px; font-size: 12px">
              平均访问时长 = 访问总时长 / 访问次数。
              <div class="setFontTop">
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
        </div>
        <div class="header-name w156">
          跳出率
          <div class="parentPosition">
          <el-popover
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
      </div>
      <div class="bid-list-record" >
            <div class="bid-list-item w158">
              <p>{{ (originData && originData.pv) || "--" }}</p>
            </div>
            <div class="bid-list-item w158">
              <p>
                {{ (originData && originData.uv) || "--" }}
              </p>
            </div>
            <div class="bid-list-item w158">
              <p>{{ (originData && originData.downPvCount) || "--" }}</p>
            </div>
            <div class="bid-list-item w158">
              <p>
                {{
                  (originData && originData.exitCount) || "--"
                }}
              </p>
            </div>
            <div class="bid-list-item w158">
              <p>{{ formatTimeFun(originData && originData.avgVisitTime) || "--" }}</p>
            </div>
           
            <div class="bid-list-item w158">
              <p>
                <p v-if="originData && originData.bounceRate">{{ percentageFun(originData.bounceRate) }}</p>
                <p v-else>--</p>
              </p>
            </div>
      </div>
    </div>


  </div>
</template>

<script>
import { percentage,averageRules } from "@/utils/percent";
import { formatTime } from "@/utils/format";
export default {
  name: "originName",
  props: {
    byChannel:{
      type:Boolean,
       default: false,
    },
    byFlowView:{ 
      type:Boolean,
       default: false,
    },
    byAreaAnaly:{ 
      type:Boolean,
       default: false,
    },
    byVisit: {
      type: Boolean,
      default: false,
    },
    byVisitedPage:{ 
      type:Boolean,
       default: false,
    }
    
  },
  data(){
    return{
        originData:null,
    }
  },
  methods:{
    averageRulesEvent(num) {
      return averageRules(num);
    },
    formatTimeFun(val){
      val =  Math.floor(val);
      return formatTime(val);
    },
    originEvent(val){
        this.originData = val;
    },
    percentageFun(val) {
      return percentage(val);
    },
  }
};
</script>

<style lang="scss" scoped>
::v-deep.setFontTop{
    margin-top:5px;
  }
  .parentPosition{
    position: relative;
    .popImg{
      position: absolute;
      left:0;
      transform: translate(0,-50%);//左右上下
    }
  }
  
.bid-list-header {
  padding-bottom: 10px;
  padding-top: 15px;
  display: flex;
  .header-name {
    width: 14%;
    justify-content: center;
    height: 30px;
    display: flex;
    align-items: center;
  }
 
  .w156 {
    font-size: 15px;
    font-weight: 400;
    line-height: 16px;
    color: #4d4d4d;
  }
  img {
    width: 11.44px;
    height: 11.44px;
    margin-left: 12px;
    cursor: pointer;
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
        color: #2c7be5;
        line-height: 17px;
      }
    }
</style>
