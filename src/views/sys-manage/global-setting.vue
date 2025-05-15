<template>
  <div class="public-block">
    <div class="Overview public-hoverItem" style="padding: 25px">
      <div class="public-firstHead">全局设置</div>
      <el-form :model="form" ref="form" size="small" :inline="true">
        <el-row>
          <el-col :span="11">
            <div class="gm-left-header" style="margin-top: 30px">
              <p class="gm-h1">排除的全局的IP地址</p>
              <p class="gm-h2">以下IP地址将被排除在所有项目统计数据外。</p>

              <el-form-item prop="excludedIp" class="gp-item-01">
                <el-input
                  type="textarea"
                  rows="3"
                  class="gm-input-01"
                  v-model="form.excludedIp"
                  placeholder=""
                />
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="13">
            <div class="gm-right">
              <div class="gm-right-prompt">
                输入您要排除在系统统计之外的IP列表(每行一个)。您可以使用CIDR表示法,例如1.2.3.4/24,也可以使用通配符,例如1.2.3.*或1.2.*.*。
                <br /><br />
                <!-- 您的IP位址是 -->
              </div>
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="11">
            <div class="gm-left-header">
              <p class="gm-h1">排除的全局的用户代理列表</p>
              <p class="gm-h2">以下用户代理将被排除在所有项目统计外。</p>

              <el-form-item prop="excludedUa" class="gp-item-01">
                <el-input
                  type="textarea"
                  rows="3"
                  class="gm-input-01"
                  v-model="form.excludedUa"
                  placeholder=""
                />
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="13">
            <div class="gm-right">
              <div class="gm-right-prompt">
                输入不需要加入系统统计的用户代理列表
                (每行一个)，支持正则表达式。
                如果访客的用户代理字串包含指定的字符，访问将不被 统计。
                <br /><br />您可以用它排除统计机器人。例如/bot
                spiderlcrawlscanner/i。
              </div>
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="11">
            <div class="gm-left-header">
              <p class="gm-h1">排除的全局的URL参数列表</p>
              <p class="gm-h2">
                以下URL参数将从所有项目的受访页面分析统计中排除。
              </p>

              <el-form-item prop="excludedUrlParams" class="gp-item-01">
                <el-input
                  type="textarea"
                  rows="3"
                  class="gm-input-01"
                  v-model="form.excludedUrlParams"
                  placeholder=""
                />
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="13">
            <div class="gm-right">
              <div class="gm-right-prompt">
                输入要排除的URL查询参数列表
                (每行一个)，以便从受访页面分析报告中排除因无效参数产生的页面。支持普通参数、正则表达式。
              </div>
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="11">
            <div class="gm-left-header">
              <p class="gm-h1">全局的站内搜索关键词参数</p>
              <p class="gm-h2">以下参数将作为所有项目的站内搜索关键词参数。</p>

              <el-form-item prop="searchwordKey" class="gp-item-01">
                <el-input
                  type="textarea"
                  rows="3"
                  class="gm-input-01"
                  v-model="form.searchwordKey"
                  placeholder=""
                />
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="13">
            <div class="gm-right">
              <div class="gm-right-prompt">
                输入用逗号分开的包含站内搜索关键字的所有搜索参数名称的列表。
              </div>
            </div>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-row>
          <el-col :span="20">&nbsp;&nbsp;</el-col>
          <el-col :span="4">
            <el-button
              type="primary"
              style=" margin: 50px 0 100px 20px"
              @click="submitSaveForm"
              >保存</el-button>
            </el-col
          >
        </el-row>
      </div>
    </div>
    <dialog-page ref="dialogPage"></dialog-page>
  </div>
</template>

<script>
import dialogPage from "./component/dialog";
import { saveSetting, getSetting } from "@/api/sysmanage/globalsetting";
export default {
  name: "globalSetting",
  components: {dialogPage},
  data() {
    return {
      form: {
        excludedIp: "",
        excludedUa: "",
        excludedUrlParams: "",
        searchwordKey: "",
        searchwordCategoryKey: "",
      },
    };
  },
  computed: {},
  watch: {},
  mounted() {
    this.loadSetting();
  },
  methods: {
    openDialogEvent(){
      this.$refs.dialogPage.dialogEvent()
    },
    loadSetting: function () {
      getSetting().then((response) => {
        this.form = response.data;
      });
    },
    submitSaveForm: function () {
      if (window.globalConfig.is_clklog_demo_environment) {
        return this.openDialogEvent()
      }
      saveSetting(this.form).then((response) => {
        this.$message({
          message: "保存成功",
          type: "success",
          duration: 2000,
        });
      });
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep {
  .el-form-item--small.el-form-item {
    width: 100% !important;
  }
  .el-form--inline .el-form-item__content {
    width: 100%;
  }
  .gp-item-01 .el-form-item__content {
    width: 100%;
  }
  
  .el-textarea__inner {
    border: 1px solid #fff;
    border-bottom: 1px solid #dcdfe6;
  }
  .el-textarea__inner {
    resize: none;
  }
  .el-input__inner {
    border: 1px solid #fff;
    border-bottom: 1px solid #dcdfe6;
  }
}
.gm {
  width: 100%;
}
.gm li {
  float: left;
  width: 100%;
}
.gm-left-header {
  width: 100%;
  padding-left: 30px;
}
.gm-right {
  width: 100%;
}
.gm-h1 {
  font-size: 14px;
  color: #808080;
  font-weight: 400;
}
.gm-h2 {
  font-size: 13px;
  color: #888;
  padding-left: 5px;
}
.gm-right-prompt {
  font-size: 13px;
  background-color: #f7f9fe;
  padding: 10px;
  color: #808080;
  position: absolute;
  bottom: 10px;
  line-height: 28px;
  margin-left: 30px;
  margin-right: 20px;
}
.gm-input-01 {
  border: none !important;
  border-bottom: 1px solid #aaa;
}

.gm-input-01 textarea {
  border: none !important;
}
</style>
