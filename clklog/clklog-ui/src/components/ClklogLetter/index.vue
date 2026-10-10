<template>
  <div>
    <el-dialog
      :visible.sync="DialogVisible"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      center
      width="700px"
      class="custom_warry letter-dialog"
    >
      <div class="letter-wrap">
        <div class="cancleBtn" @click="closeDialog()">×</div>

        <div class="letter-title">感谢您选择 ClkLog</div>

        <div class="star-box">
          <div class="star-title">⭐ 支持 ClkLog 开源社区</div>
          <p>ClkLog 是一个持续维护的开源用户行为分析平台。</p>
          <p>如果 ClkLog 对您的项目有所帮助，</p>
          <p>欢迎前往 Gitee 点亮 ⭐ Star 支持我们。</p>
          <a
            class="star-btn"
            href="https://gitee.com/clklog/clklog"
            target="_blank"
            rel="noopener noreferrer"
          >
            ⭐ Star 支持 ClkLog
          </a>
        </div>

        <div class="survey-section">
          <div class="survey-title">参与 ClkLog 社区建设</div>
          <div class="survey-desc">
            为了帮助我们持续优化产品与社区服务，希望了解一下您的使用情况。
          </div>

          <el-form
            :model="ruleForm"
            :rules="rules"
            ref="ruleForm"
            class="demo-ruleForm"
            label-position="top"
          >
            <div class="field-block">
              <div class="field-label">您使用 ClkLog 的场景？</div>
              <el-radio-group v-model="usageScenario" class="option-grid">
                <el-radio v-for="item in usageList" :key="item" :label="item">{{
                  item
                }}</el-radio>
              </el-radio-group>
            </div>

            <div class="field-block">
              <div class="field-label">您的项目所属领域？</div>
              <el-radio-group v-model="industry" class="option-grid">
                <el-radio
                  v-for="item in industryList"
                  :key="item"
                  :label="item"
                  >{{ item }}</el-radio
                >
              </el-radio-group>
            </div>

            <div class="field-block email-block">
              <div class="field-label">
                如果您希望获取版本更新、社区活动等信息，可以留下邮箱。
              </div>
              <div class="email-desc">邮箱：</div>
              <el-form-item prop="email">
                <el-input v-model="ruleForm.email" placeholder=""></el-input>
              </el-form-item>
            </div>
          </el-form>
        </div>
        <div class="footer-tip">
          感谢每一个 ⭐ Star，支持 ClkLog 开源社区持续发展
        </div>
        <div class="btn-row">
          <el-button class="zc_btn_default letter-btn" @click="skipBtn"
            >跳过</el-button
          >
          <el-button class="zc_btn letter-btn" @click="confirm">确认</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  skipsubscriptionApi,
  subscribeApi,
  subscribeActiveApi,
} from "@/api/trackingapi/subscribe.js";

const usageList = [
  "企业内部系统用户行为分析",
  "Web / APP / 小程序数据分析",
  "SaaS 产品用户分析",
  "替代商业分析平台",
  "学习研究",
  "其他",
];

const industryList = [
  "政企数字化",
  "金融证券",
  "电商",
  "教育",
  "社交",
  "游戏",
  "其他",
];

export default {
  data() {
    return {
      DialogVisible: false,
      usageList,
      industryList,
      usageScenario: "",
      industry: "",
      ruleForm: {
        receiveNotification: true,
        clientId: "",
        orgnizationName: "",
        contact: "",
        email: "",
        phone: "",
        projectType: "",
        projectName: this.$store.getters.projectName,
        remark: "",
      },
      openFLag: {
        subscribed: false,
        clientId: "",
      },
      rules: {
        email: [
          {
            pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
            message: "邮箱格式不正确",
            trigger: "blur",
          },
        ],
      },
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.$bus.$on("$letter", (res) => {
        this.clearData();
        this.ruleForm.clientId = res.clientId;
        this.openFLag = res;
        this.openLetter(res);
      });
    });
  },
  methods: {
    openShow() {},
    closeDialog() {
      this.DialogVisible = false;
      this.skipBtn();
    },
    openLetter(res) {
      if (this.$refs.ruleForm) {
        this.$refs.ruleForm.resetFields();
      }
      this.ruleForm = Object.assign(this.getEmptyForm(), res || {});
      if (res.receiveNotification === false) {
        this.ruleForm.receiveNotification = false;
      } else {
        this.ruleForm.receiveNotification = true;
      }
      this.ruleForm.projectName = this.$store.getters.projectName;
      // 回显：提交时 usageScenario → remark，industry → projectType
      this.usageScenario = this.usageList.includes(res.remark)
        ? res.remark
        : "";
      this.industry = this.industryList.includes(res.projectType)
        ? res.projectType
        : "";
      this.DialogVisible = true;
    },
    getEmptyForm() {
      return {
        receiveNotification: true,
        clientId: "",
        orgnizationName: "",
        contact: "",
        email: "",
        phone: "",
        projectType: "",
        projectName: this.$store.getters.projectName,
        remark: "",
      };
    },
    clearData() {
      this.usageScenario = "";
      this.industry = "";
      this.ruleForm = this.getEmptyForm();
    },
    confirm() {
      this.formCollection();
    },
    skipBtn() {
      let params = {
        clientId: this.openFLag.clientId,
      };
      if (!this.openFLag.subscribed) {
        skipsubscriptionApi(params)
          .then(() => {
            if (!this.openFLag.subscribed) {
              this.acitiveEvent();
            }
            this.DialogVisible = false;
          })
          .catch(() => {
            this.DialogVisible = false;
          });
      }
      this.DialogVisible = false;
    },
    formCollection() {
      this.$refs["ruleForm"].validate((valid) => {
        if (valid) {
          this.ruleForm.remark = this.usageScenario;
          this.ruleForm.projectType = this.industry;
          this.ruleForm.clientId = this.openFLag.clientId;
          subscribeApi(this.ruleForm).then((res) => {
            if (res.code == 200) {
              this.$message.success("提交成功！");
              this.acitiveEvent(this.ruleForm);
              this.DialogVisible = false;
            }
          });
        }
      });
    },
    acitiveEvent(val) {
      let params = {};
      if (val) {
        params = this.ruleForm;
      }
      subscribeActiveApi(params)
        .then(() => {
          this.DialogVisible = false;
        })
        .catch(() => {
          this.DialogVisible = false;
        });
    },
  },

  beforeDestroy() {
    this.$bus.$off("$letter");
  },
};
</script>

<style lang="scss" scoped>
.letter-dialog {
  ::v-deep .el-dialog {
    border-radius: 8px;
    overflow: hidden;
  }

  ::v-deep .el-dialog__header {
    display: none;
    padding: 0;
  }

  ::v-deep .el-dialog__body {
    padding: 28px 36px 32px;
    box-sizing: border-box;
  }
}

.letter-wrap {
  position: relative;
  color: #333;
}

.cancleBtn {
  position: absolute;
  right: -8px;
  top: -12px;
  color: #99aab3;
  font-size: 22px;
  font-weight: 400;
  width: 28px;
  height: 28px;
  line-height: 28px;
  text-align: center;
  cursor: pointer;

  &:hover {
    color: #296bef;
  }
}

.letter-title {
  text-align: center;
  font-size: 20px;
  font-weight: 600;
  color: #333;
  line-height: 28px;
  margin-bottom: 20px;
}

.star-box {
  background: #eef5ff;
  border-radius: 6px;
  padding: 16px 18px;
  margin-bottom: 24px;

  .star-title {
    font-size: 15px;
    font-weight: 600;
    color: #2c7be5;
    margin-bottom: 10px;
  }

  p {
    margin: 0;
    font-size: 13px;
    line-height: 22px;
    color: #4d4d4d;
  }

  .star-btn {
    display: inline-block;
    margin-top: 14px;
    padding: 8px 16px;
    font-size: 13px;
    font-weight: 600;
    color: #fff;
    background: #2c7be5;
    border-radius: 4px;
    text-decoration: none;
    line-height: 1.4;

    &:hover {
      opacity: 0.9;
    }
  }
}

.survey-section {
  .survey-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    line-height: 24px;
    margin-bottom: 6px;
  }

  .survey-desc {
    font-size: 13px;
    color: #666;
    line-height: 20px;
    margin-bottom: 18px;
  }
}

.field-block {
  margin-bottom: 18px;

  .field-label {
    font-size: 14px;
    font-weight: 600;
    color: #333;
    line-height: 22px;
    margin-bottom: 12px;
  }
}

.option-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  row-gap: 12px;
  column-gap: 25px;

  ::v-deep .el-radio {
    margin-right: 0;
    display: flex;
    align-items: center;
  }

  ::v-deep .el-radio__label {
    font-size: 13px;
    color: #4d4d4d;
    padding-left: 8px;
    white-space: normal;
    line-height: 18px;
  }
}

.email-block {
  .email-desc {
    font-size: 13px;
    color: #666;
    line-height: 20px;
    margin-bottom: 12px;
  }

  ::v-deep .el-form-item {
    margin-bottom: 0;
  }

  ::v-deep .el-input__inner {
    height: 36px;
    line-height: 36px;
    border-radius: 4px;
    border-color: #dcdfe6;
  }
}

.footer-tip {
  text-align: center;
  font-size: 12px;
  font-weight: 500;
  color: #aeb1b4;
  line-height: 22px;
  margin: 4px 0 20px;
  letter-spacing: 0.5px;
}

.btn-row {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;

  .letter-btn {
    min-width: 88px;
    height: 34px;
    border-radius: 4px;
    font-size: 14px;
  }
}
</style>
