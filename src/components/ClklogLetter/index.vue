<template>
  <div>
    <el-dialog
      :visible.sync="DialogVisible"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      center
      width="800px"
      style="min-height: 90vh; border-radius: 6px"
      class="custom_warry"
    >
      <div class="custom-header">
        <div
          class="letter_h1"
          style="
            text-align: center;
            position: relative;
            line-height: 40px;
            margin-bottom: 20px;
            font-size: 18px;
          "
        >
          <div class="cancleBtn" @click="closeDialog()">X</div>
          感谢您对ClkLog的关注与支持！
        </div>
        <div style="text-align: center">
          为了给您提供更好的服务与支持，请填写以下表单。
        </div>
      </div>

      <div style="min-height: 700px;margin-top: 25px;">
        <div>
          <div class="letter_h1">是否愿意接收ClkLog后续更新提醒:</div>
          <div style="margin-top: 20px; text-align: left">
            <el-radio
              v-model="ruleForm.receiveNotification"
              style="margin-right: 50px"
              :label="true"
              >接收</el-radio
            >
            <el-radio v-model="ruleForm.receiveNotification" :label="false"
              >不接收</el-radio
            >
          </div>
        </div>

        <!-- info -->
        <!-- <div class="letter_h1 setSpace">联系人信息:</div> -->
        <el-form
          :model="ruleForm"
          :rules="rules"
          ref="ruleForm"
          label-width="120px"
          class="demo-ruleForm"
        >
          <!-- <div class="letter_h1">是否愿意接收ClkLog后续更新提醒:</div>
          <el-form-item>
            <div>
              <el-radio
                v-model="ruleForm.receiveNotification"
                style="margin-right: 50px"
                :label="true"
                >接收</el-radio
              >
              <el-radio v-model="ruleForm.receiveNotification" :label="false"
                >不接收</el-radio
              >
            </div>
          </el-form-item> -->

          <div class="letter_h1 setSpace">联系人信息:</div>
          <el-form-item label="公司名称:" prop="orgnizationName">
            <el-input
              v-model="ruleForm.orgnizationName"
              placeholder=""
            ></el-input>
          </el-form-item>
          <el-form-item label="联系人:" prop="contact">
            <el-input v-model="ruleForm.contact" placeholder=""></el-input>
          </el-form-item>
          <el-form-item label="联系邮箱:" prop="email">
            <el-input v-model="ruleForm.email" placeholder=""></el-input>
          </el-form-item>
          <el-form-item label="联系电话:" prop="phone">
            <el-input v-model="ruleForm.phone" placeholder=""></el-input>
          </el-form-item>
          <!-- 说明 -->
          <div class="setSpace">
            <span class="letter_h1"> ClkLog在您的项目中的使用场景: </span>
            <div class="letter_h2 indent">
              以下信息仅用于我们了解ClkLog在您的项目中的使用场景，
              在后续产品迭代升级时考虑相关行业的特殊性，以提供更好的服务。
            </div>
          </div>

          <el-form-item
            label="埋点项目类型:"
            prop="projectType"
            style="margin-top: 20px"
          >
            <el-select
              class="appli_select"
              v-model="ruleForm.projectType"
              placeholder="请选择项目类型"
              style="width: 100%; height: 36px"
            >
              <el-option
                v-for="(item, index) in typeList"
                :key="index"
                :label="item"
                :value="item"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="埋点项目说明:" style="margin-left: 8px;">
            <el-input
              type="textarea"
              maxlength="1000"
              show-word-limit
              v-model="ruleForm.remark"
              placeholder=""
              rows="4"
            ></el-input>
          </el-form-item>

          <!-- <el-form-item>
            <div
              style="display: flex; justify-content: center; height: 35px;"
            >
              <el-button class="zc_btn" @click="confirm">确认</el-button>
              <el-button
                class="zc_btn_default"
                @click="skipBtn"
                style="margin-left: 50px"
                >跳过</el-button
              >
            </div>
          </el-form-item> -->
        </el-form>
        <div style="display: flex; justify-content: center; height: 35px">
          <el-button class="zc_btn" @click="confirm">确认</el-button>
          <el-button
            class="zc_btn_default"
            @click="skipBtn"
            style="margin-left: 50px"
            >跳过</el-button
          >
        </div>

        <!-- <div
          style="
            display: flex;
            justify-content:center;
            height: 50px;
            margin: 0 140px;
          "
        >
          <el-button class="zc_btn" @click="confirm">确认</el-button>
          <el-button
            class="zc_btn_default"
            @click="skipBtn"
            style="margin-left: 50px"
            >跳过</el-button
          >
        </div> -->
      </div>
      <!-- 确认事件 -->
    </el-dialog>
  </div>
</template>

<script>
import {
  skipsubscriptionApi,
  subscribeApi,
  subscribeActiveApi,
} from "@/api/trackingapi/subscribe.js";
const typedata = [
  "电商",
  "社交电商",
  "游戏",
  "泛互联网",
  "品牌零售",
  "互联网金融",
  "证券",
  "银行",
  "企业服务",
  "房产行业",
  "汽车",
  "保险",
  "融合媒体",
  "医疗服务",
  "教育行业",
  "文娱",
  "其他",
];
export default {
  data() {
    return {
      DialogVisible: false,
      typeList: typedata,
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
        orgnizationName: [
          { required: true, message: "公司名称不能为空", trigger: "blur" },
        ],
        contact: [
          { required: true, message: "联系人不能为空", trigger: "blur" },
        ],
        email: [
          {
            required: true,
            message: "请输入邮箱地址",
            trigger: ["blur", "change"],
          },
          {
            pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
            message: "邮箱格式不正确",
            trigger: "blur",
          },
        ],
        phone: [
          {
            required: true,
            message: "请输电话",
            trigger: ["blur", "change"],
          },
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: "电话格式不正确",
            trigger: "blur",
          },
        ],
        projectType: [
          {
            required: true,
            message: "项目类型不能为空",
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
      this.ruleForm = res;
      if (res.receiveNotification === false) {
        this.ruleForm.receiveNotification = false;
      } else {
        this.ruleForm.receiveNotification = true;
      }
      this.ruleForm.projectName = this.$store.getters.projectName;
      this.DialogVisible = true;
    },
    clearData() {
      this.ruleForm = {
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

    confirm() {
      this.formCollection();
    },
    skipBtn() {
      let params = {
        clientId: this.openFLag.clientId,
      };
      if (!this.openFLag.subscribed) {
        skipsubscriptionApi(params)
          .then((res) => {
            if (!this.openFLag.subscribed) {
              this.acitiveEvent();
            }
            this.DialogVisible = false;
          })
          .catch((err) => {
            this.DialogVisible = false;
          });
      }
      this.DialogVisible = false;
    },
    formCollection() {
      this.$refs["ruleForm"].validate((valid) => {
        if (valid) {
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
    // 1537411672@qq.com
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
::v-deep {
  .el-dialog__body {
    padding: 30px 120px;
    box-sizing: border-box;
  }
  .el-dialog__title {
    font-size: 20px;
  }
  .el-radio__label {
    font-size: 15px;
  }
  .el-form-item__label {
    font-size: 15px;
    font-weight: 400;
    line-height: 36px;
    text-align: left;
    color: #4d4d4d;
  }
  .el-form-item {
    margin: 20px 0;
  }
}
.setSpace {
  margin-top: 40px;
}
.custom_warry {
  .letter_h1 {
    font-size: 16px;
    font-weight: 600;
    line-height: 25px;
    color: #4d4d4d;
  }
  .letter_h2 {
    font-size: 14px;
    font-weight: 400;
    line-height: 22px;
    color: #4d4d4d;
  }
  .skip {
    cursor: pointer;
  }
  .skip:hover {
    color: #a9c4f9;
  }
  .indent {
    text-indent: 2em;
    margin-top: 10px;
  }

  .custom-header {
    font-size: 15px;
    .cancleBtn {
      position: absolute;
      right: -90px;
      top: -42px;
      color: #99aab3;
      font-size: 18px;
      font-weight: 400;
      width: 30px;
      cursor: pointer;
    }
    .cancleBtn:hover {
      color: #296bef !important;
    }
  }
}
</style>
