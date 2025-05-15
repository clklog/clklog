<template>
  <div>
    <el-dialog
      title="修改密码"
      :visible.sync="dialogVisible"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="true"
      width="800px"
      top="20vh"
      class="custom_warry"
    >
      <div
        class="public-hoverItem"
        style="padding: 25px; font-size: 20px; background-color: #fff"
      >
        <el-form
          class="dialog-form"
          :model="modifyPswForm"
          :rules="rules"
          ref="modifyPswForm"
          label-width="100px"
          size="mini"
          style="min-height: 330px"
        >
          <el-form-item label="原密码" prop="oldPassword">
            <el-input
              v-model="modifyPswForm.oldPassword"
              type="password"
              autocomplete="off"
              clearable
              placeholder="请输入原密码"
            ></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="modifyPswForm.newPassword"
              type="password"
              autocomplete="off"
              clearable
              placeholder="请输入新密码"
            ></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="modifyPswForm.confirmPassword"
              type="password"
              autocomplete="off"
              clearable
              placeholder="请再次输入新密码"
            ></el-input>
          </el-form-item>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
          style="margin-top: 20px; display: flex; justify-content: flex-end"
        >
          <el-button class="zc_btn" @click="modifyPsw">确 定</el-button>
          <el-button class="zc_btn_default" @click="dialogVisible = false"
            >取 消</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { modifyPassword } from "@/api/user";
export default {
  name: "ModifyPsw",
  data() {
    return {
      dialogVisible: false,
      modifyPswForm: {
        oldPassword: "",
        newPassword: "",
        confirmPassword: "",
      },
      rules: {
        oldPassword: [
          { required: true, message: "请输入原密码", trigger: "blur" },
        ],
        newPassword: [
          { required: true, message: "请输入新密码", trigger: "blur" },
        ],
        confirmPassword: [
          { required: true, message: "请再次输入新密码", trigger: "blur" },
        ],
      },
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.$bus.$on("$modifyPsw", (res) => {
        this.dialogVisible = true;
      });
    });
  },
  methods: {
    modifyPsw() {
      this.$refs.modifyPswForm.validate((valid) => {
        if (valid) {
          modifyPassword(this.modifyPswForm).then((res) => {
            if (res.code === 200) {
              this.$message({
                message: "修改密码成功",
                type: "success",
              });
              this.dialogVisible = false;
            } else {
              this.$message({
                message: res.msg,
                type: "error",
              });
            }
          });
        }
      });
    },
  },
  beforeDestroy() {
    this.$bus.$off("$modifyPsw");
  },
};
</script>

<style lang="scss" scoped>
::v-deep {
  @import "~@/styles/components/el-checkbox.scss";
  @import "~@/styles/components/el-pagination.scss";
  .el-form-item__label {
    text-align: left;
  }
  .el-dialog {
    border-radius: 6px;
  }
  .el-dialog__headerbtn {
    top: 15px;
  }
  .el-dialog__header {
    padding: 0 !important;
    border-radius: 6px;
    height: 44px;
    line-height: 44px;
    background: #fbfcfe;
  }
  .el-dialog__title {
    margin-left: 16px;
    font-size: 15px;
    color: #4d4d4d;
    font-weight: bold;
  }
  .el-dialog__body {
    padding: 20px;
    background-color: #eef4fd;
    border-radius: 6px;
  }

  .el-checkbox {
    margin-right: 20px;
  }
}
</style>
