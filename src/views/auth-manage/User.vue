<template>
  <div>
    <div class="documentation-container">
      <div class="search-area">
        <el-form
          :model="queryParams"
          ref="queryForm"
          size="small"
          :inline="true"
          label-width="68px"
        >
          <el-form-item label="账号" prop="userName">
            <el-input
              v-model="queryParams.userName"
              placeholder="请输入账号"
              clearable
              size="mini"
              @keyup.enter.native="getUsers"
            />
          </el-form-item>
          <el-form-item label="显示名" prop="显示名">
            <el-input
              v-model="queryParams.displayName"
              placeholder="请输入显示名"
              clearable
              size="mini"
              @keyup.enter.native="getUsers"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              icon="el-icon-search"
              size="mini"
              @click="getUsers"
              >搜索</el-button
            >
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
              >重置</el-button
            >
          </el-form-item>
        </el-form>
      </div>
    </div>
    <div class="public-block">
      <div class="block-main public-hoverItem logCon" style="min-height: 200px">
        <div style="display: flex; justify-content: space-between">
          <div class="public-tableHead">账号管理</div>
          <div style="display: flex">
            <el-button
              class="custom_button zc_btn_default"
              @click="handleAdd"
              icon="el-icon-plus"
              >新增账号</el-button
            >
          </div>
        </div>
        <el-table
          class="public-radius"
          :header-cell-style="{ textAlign: 'center', background: '#f7fafe ' }"
          :cell-style="{ textAlign: 'center' }"
          :data="rows"
          border
          style="width: 100%"
          v-loading="loading"
        >
          <el-table-column label="序号" type="index" width="80" align="center">
            <template slot-scope="scope">
              <span v-text="getIndex(scope.$index)"> </span>
            </template>
          </el-table-column>
          <el-table-column prop="userName" label="账号"> </el-table-column>
          <el-table-column prop="displayName" label="显示名"> </el-table-column>
          <el-table-column prop="createtime" label="创建日期">
            <template slot-scope="{ row }">
              {{ row.createtime | formatDate }}
            </template>
          </el-table-column>
          <el-table-column prop="latesttime" label="上次登录日期">
            <template slot-scope="{ row }">
              {{ row.latesttime | formatDate }}
            </template>
          </el-table-column>
          <el-table-column prop="createuser" label="创建人"> </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="{ row }">
              <div style="display: flex; justify-content: center">
                <i
                  class="el-icon-edit edit_btn"
                  @click="handleEdit(row)"
                  v-if="!['clklog', 'admin'].includes(row.userName)"
                ></i>
                <i
                  class="el-icon-key modifypwd_btn"
                  @click="handleEditModifyPwd(row)"
                ></i>
                <i
                  v-if="!['clklog', 'admin'].includes(row.userName)"
                  class="el-icon-delete delete_btn"
                  @click="handleDel(row)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div class="block">
          <el-pagination
            next-text="下一页"
            :current-page="pageNum"
            :page-sizes="[10, 20, 30, 40]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <el-dialog
      :title="title"
      :visible.sync="open"
      width="50%"
      top="8vh"
      :close-on-click-modal="false"
    >
      <div
        class="public-hoverItem"
        style="
          min-height: 80px;
          padding: 2px;
          font-size: 20px;
          background-color: #fff;
        "
      >
        <el-tabs
          style="background-color: #fff; padding: 20px"
          v-model="activeName"
          @tab-click="handleClick"
        >
          <el-tab-pane style="padding-top: 16px" label="账号信息" name="first">
            <el-form
              class="dialog-form"
              :model="form"
              :rules="rules"
              ref="form"
              label-width="80px"
            >
              <el-form-item label="账号" prop="userName">
                <el-input
                  size="mini"
                  v-model="form.userName"
                  placeholder="请输入账号"
                  clearable
                ></el-input>
              </el-form-item>
              <el-form-item label="显示名" prop="displayName">
                <el-input
                  size="mini"
                  v-model="form.displayName"
                  placeholder="请输入显示名"
                  clearable
                ></el-input>
              </el-form-item>
              <el-form-item label="密码" prop="password" v-if="!form.userId">
                <el-input
                  size="mini"
                  v-model="form.password"
                  placeholder="请输入密码"
                  clearable
                  type="password"
                ></el-input>
              </el-form-item>
              <el-form-item
                label="确认密码"
                prop="password2"
                v-if="!form.userId"
              >
                <el-input
                  size="mini"
                  v-model="form.password2"
                  placeholder="请再次输入新密码"
                  clearable
                  type="password"
                ></el-input>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
        <div style="margin: 20px; display: flex; justify-content: end">
          <span slot="footer" class="dialog-footer">
            <el-button size="mini" type="primary" @click="submit"
              >确 定</el-button
            >
            <el-button size="mini" @click="closeDialog()">取 消</el-button>
          </span>
        </div>
      </div>
    </el-dialog>

    <el-dialog
      title="修改密码"
      :visible.sync="openModifyPwd"
      width="30%"
      top="20vh"
      :close-on-click-modal="false"
    >
      <div
        class="public-hoverItem"
        style="
          min-height: 80px;
          padding: 20px;
          font-size: 20px;
          background-color: #fff;
        "
      >
        <el-form
          class="dialog-form"
          :model="formModifyPwd"
          :rules="rules"
          ref="form"
          label-width="80px"
        >
          <el-form-item label="新密码" prop="password">
            <el-input
              size="mini"
              v-model="formModifyPwd.password"
              placeholder="请输入新密码"
              clearable
              type="password"
            ></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="password2">
            <el-input
              size="mini"
              v-model="formModifyPwd.password2"
              placeholder="请再次输入新密码"
              clearable
              type="password"
            ></el-input>
          </el-form-item>
        </el-form>
        <div style="margin-top: 20px; display: flex; justify-content: end">
          <span slot="footer" class="dialog-footer">
            <el-button size="mini" type="primary" @click="submitModifyPwd"
              >确 定</el-button
            >
            <el-button size="mini" @click="closeDialogModifyPwd()"
              >取 消</el-button
            >
          </span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import moment from "moment";
import {
  getUserListPage,
  deleteUser,
  editUser,
  addUser,
  getUser,
  modifyPassword,
} from "@/api/authmanage/user";
export default {
  name: "UserPage",
  data() {
    return {
      rows: [],
      total: 0,
      pageSize: 20,
      pageNum: 1,
      loading: false,
      queryParams: {
        userName: "",
        displayName: "",
      },
      open: false,
      title: "",
      form: {
        userId: null,
        userName: "",
        displayName: "",
        password: "",
        password2: "",
        oldpassword: "",
      },
      activeName: "first",
      rules: {
        userName: [{ required: true, message: "请输入账号", trigger: "blur" }],
        displayName: [
          { required: true, message: "请输入显示名", trigger: "blur" },
        ],
      },
      openModifyPwd: false,
      formModifyPwd: {
        userId: null,
        password: "",
        password2: "",
      },
    };
  },
  filters: {
    formatDate(time) {
      if (!time) {
        return "";
      }
      return moment(time).format("YYYY-MM-DD HH:mm:ss");
    },
  },
  methods: {
    closeDialog() {
      this.open = false;
      this.loading = false;
    },
    handleClick(tab, event) {
      //console.log(tab, event);
    },
    resetQuery() {
      this.queryParams.userName = "";
      this.queryParams.displayName = "";
      this.getUsers();
    },
    getIndex($index) {
      return (this.pageNum - 1) * this.pageSize + $index + 1;
    },
    UserSelectChange(item) {
      this.editForm.alias = item;
      this.addForm.alias = item;
    },
    handleSizeChange(val) {
      this.pageIndex = val;
      this.getUsers();
    },

    handleCurrentChange(val) {
      this.pageIndex = val;
      this.getUsers();
    },
    getUsers() {
      let para = {
        pageSize: this.pageSize,
        pageIndex: this.pageNum,
        userName: this.queryParams.userName,
        displayName: this.queryParams.displayName,
      };
      this.loading = true;
      getUserListPage(para).then((res) => {
        this.total = res.data.total;
        this.rows = res.data.rows;
        this.loading = false;
      });
    },
    handleDel: function (row) {
      this.$confirm("确认删除" + row.userName + "吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        cancelButtonClass: "custom-cancel-button",
        type: "warning",
      })
        .then(() => {
          if (window.globalConfig.is_clklog_demo_environment) {
            console.log("执行了演示环境");
            
            return this.$bus.$emit("$demo_event");
          }
          this.loading = true;
          let para = { userId: row.userId };
          deleteUser(para).then((res) => {
            this.loading = false;
            let msg = res.data ? "删除成功" : "删除失败";
            let type = res.data ? "success" : "warning";
            this.$message({
              message: msg,
              type: type,
            });
            this.getUsers();
          });
        })
        .catch(() => {});
    },
    handleEdit: function (row) {
      this.title = "编辑账号";
      this.open = true;
      this.activeName = "first";
      let para = { userId: row.userId };
      getUser(para).then((res) => {
        this.loading = false;
        if (res.code == 200) {
          this.form = res.data;
        } else {
          let msg = res.msg;
          let type = "warning";
          this.$message({
            message: msg,
            type: type,
          });
          this.$refs["form"].resetFields();
        }
      });
    },
    handleAdd: function () {
      this.title = "新增账号";
      this.form = {
        userId: null,
        userName: "",
        displayName: "",
        password: "",
        password2: "",
      };
      this.activeName = "first";
      this.open = true;
      this.$nextTick(() => {
        this.$refs["form"].clearValidate();
      });
    },
    submit: function () {
      this.loading = true;
      if (!this.form.userName) {
        this.$message({
          message: "请输入账号",
          type: "warning",
        });
        this.loading = false;
        return;
      }
      if (!this.form.displayName) {
        this.$message({
          message: "请输入显示名",
          type: "warning",
        });
        this.loading = false;
        return;
      }
      if (!this.form.userId) {
        if (!this.form.password) {
          this.$message({
            message: "请输入密码",
            type: "warning",
          });
          this.loading = false;
          return;
        }
        if (!this.form.password2) {
          this.$message({
            message: "请再次输入密码",
            type: "warning",
          });
          this.loading = false;
          return;
        }
      }
      if (
        this.form.password &&
        this.form.password2 &&
        this.form.password != this.form.password2
      ) {
        this.$message({
          message: "两次密码不一致",
          type: "warning",
        });
        this.loading = false;
        return;
      }
      if (this.form.userId) {
        this.editSubmit();
      } else {
        this.addSubmit();
      }
    },
    editSubmit: function () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (window.globalConfig.is_clklog_demo_environment) {
            this.loading = false;
            return this.$bus.$emit("$demo_event");
          }
          let para = Object.assign({}, this.form);
          editUser(para).then((res) => {
            this.loading = false;
            let msg = res.data ? "编辑成功" : res.msg;
            let type = res.data ? "success" : "warning";
            this.$message({
              message: msg,
              type: type,
            });
            this.$refs["form"].resetFields();
            this.open = false;
            this.getUsers();
          });
        } else {
          this.loading = false;
        }
      });
    },
    addSubmit: function () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (window.globalConfig.is_clklog_demo_environment) {
            this.loading = false;
            return this.$bus.$emit("$demo_event");
          }
          let para = Object.assign({}, this.form);
          addUser(para).then((res) => {
            this.loading = false;
            let msg = res.data ? "添加成功" : res.msg;
            let type = res.data ? "success" : "warning";
            this.$message({
              message: msg,
              type: type,
            });
            this.$refs["form"].resetFields();
            this.open = false;
            this.getUsers();
          }).catch((err) => {
            this.loading = false;
          });
        } else {
          this.loading = false;
        }
      });
    },
    handleEditModifyPwd: function (row) {
      this.title = "修改密码";
      this.openModifyPwd = true;
      this.formModifyPwd = {
        userId: row.userId,
        password: "",
        password2: "",
      };
      this.$nextTick(() => {
        this.$refs["form"].clearValidate();
      });
    },
    closeDialogModifyPwd() {
      this.openModifyPwd = false;
      this.loading = false;
    },
    submitModifyPwd: function () {
      if (window.globalConfig.is_clklog_demo_environment) {
        return this.$bus.$emit("$demo_event");
      }
      this.loading = true;
      if (!this.formModifyPwd.password) {
        this.$message({
          message: "请输入新密码",
          type: "warning",
        });
        this.loading = false;
        return;
      }
      if (!this.formModifyPwd.password2) {
        this.$message({
          message: "请再次输入新密码",
          type: "warning",
        });
        this.loading = false;
        return;
      }
      if (this.formModifyPwd.password != this.formModifyPwd.password2) {
        this.$message({
          message: "两次密码不一致",
          type: "warning",
        });
        this.loading = false;
        return;
      }
      let para = {
        userId: this.formModifyPwd.userId,
        newPassword: this.formModifyPwd.password,
      };
      modifyPassword(para).then((res) => {
        this.loading = false;
        let msg = res.data ? "修改成功" : res.msg;
        let type = res.data ? "success" : "warning";
        this.$message({
          message: msg,
          type: type,
        });
        this.$refs["form"].resetFields();
        this.openModifyPwd = false;
      });
    },
  },
  
  mounted() {
    this.getUsers();
  },
};
</script>

<style lang="scss" scoped>
::v-deep {
  @import "~@/styles/components/el-checkbox.scss";
  @import "~@/styles/components/el-pagination.scss";

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
    padding: 16px;
    background-color: #eef4fd;
    border-radius: 6px;
  }

  .el-checkbox {
    margin-right: 20px;
  }
}

.documentation-container {
  box-sizing: border-box;
  min-height: 66px;
  padding-bottom: 20px;
  width: 100%;
  .search-area {
    position: fixed;
    padding: 15px 15px;
    display: flex;
    border-bottom: 1px solid #eee;
    width: 100%;
    z-index: 200;
    background: #fff;
    overflow: auto;
    white-space: nowrap;
    min-height: 66px;

    ::v-deep {
      .el-form-item {
        margin-bottom: 0px;
      }
      .el-form-item__label {
        font-size: 13px;
      }
    }
  }
}

.dialog-form {
  ::v-deep {
    .el-form-item__label {
      font-size: 13px;
    }
  }
}

.edit_btn {
  width: 20px;
  height: 20px;
  line-height: 20px;
  border-radius: 2px;
  background: #dde4ed;
  color: #2c7be5;
  cursor: pointer;
}

.modifypwd_btn {
  margin-left: 20px;
  width: 20px;
  height: 20px;
  line-height: 20px;
  border-radius: 2px;
  background: #dde4ed;
  color: #b95593;
  cursor: pointer;
}

.delete_btn {
  width: 20px;
  height: 20px;
  line-height: 20px;
  border-radius: 2px;
  background: #f3e2d0;
  color: #de6210;
  margin-left: 20px;
  cursor: pointer;
}
</style>
