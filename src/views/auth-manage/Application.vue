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
          <el-form-item label="应用名称" prop="appName">
            <el-input
              v-model="queryParams.appName"
              placeholder="请输入应用名称"
              clearable
              size="mini"
              @keyup.enter.native="getApps"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              icon="el-icon-search"
              size="mini"
              @click="getApps"
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
          <div class="public-tableHead">应用管理</div>
          <div style="display: flex">
            <el-button
              class="custom_button zc_btn_default"
              @click="handleAdd"
              icon="el-icon-plus"
              >新增应用</el-button
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
          <el-table-column prop="appName" label="应用名称"> </el-table-column>
          <!-- <el-table-column prop="uri" label="uri"> </el-table-column> -->
          <el-table-column prop="createtime" label="创建日期">
            <template slot-scope="{ row }">
              {{ row.createtime | formatDate }}
            </template>
          </el-table-column>
          <el-table-column prop="modifytime" label="修改日期">
            <template slot-scope="{ row }">
              {{ row.modifytime | formatDate }}
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="{ row }">
              <div style="display: flex; justify-content: center">
                <i class="el-icon-edit edit_btn" @click="handleEdit(row)"></i>
                <i
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
      top="20vh"
      :close-on-click-modal="false"
    >
      <div
        class="public-hoverItem"
        style="
          min-height: 80px;
          background-color: #fbfcfe;
          padding: 16px;
          box-sizing: border-box;
          font-size: 20px;
        "
      >
        <el-form
          class="dialog-form"
          :model="form"
          :rules="rules"
          ref="form"
          label-width="80px"
        >
          <el-form-item label="应用名称" prop="appName">
            <el-input
              size="mini"
              v-model="form.appName"
              placeholder="请输入应用名称"
              clearable
            ></el-input>
          </el-form-item>
          <el-form-item label="uri" prop="uri">
            <el-input
              size="mini"
              v-model="form.uri"
              placeholder="请输入uri"
              clearable
            ></el-input>
          </el-form-item>
        </el-form>
        <div style="display: flex; justify-content: end">
          <span slot="footer" class="dialog-footer">
            <el-button size="mini" type="primary" @click="submit"
              >确 定</el-button
            >
            <el-button size="mini" @click="open = false">取 消</el-button>
          </span>
        </div>
      </div>
    </el-dialog>
  </div>
</template> 
<script>
import moment from "moment";
import {
  addApp,
  getAppListPage,
  editApp,
  deleteApp,
} from "@/api/authmanage/application";

export default {
  name: "ApplicationPage",
  data() {
    return {
      rows: [],
      total: 0,
      pageSize: 20,
      pageNum: 1,
      loading: false,
      queryParams: {
        appName: "",
        uri: "",
      },
      //弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 表单参数
      form: {
        appId: null,
        appName: null,
        uri: null,
      },
      // 表单校验
      rules: {
        appName: [{ required: true, message: "请输入应用名", trigger: "blur" }],
        uri: [{ required: true, message: "请输入地址", trigger: "blur" }],
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
    resetQuery() {
      this.queryParams = {
        appName: "",
        uri: "",
      };
      this.getApps();
    },
    getIndex($index) {
      return (this.pageNum - 1) * this.pageSize + $index + 1;
    },
    handleSizeChange(val) {
      this.pageIndex = val;
      this.getApps();
    },
    handleCurrentChange(val) {
      this.pageIndex = val;
      this.getApps();
    },
    getApps() {
      let para = {
        pageSize: this.pageSize,
        pageIndex: this.pageNum,
        appName: this.queryParams.appName,
        uri: this.queryParams.uri,
      };
      this.loading = true;
      getAppListPage(para).then((res) => {
        this.total = res.data.total;
        this.rows = res.data.rows;
        this.loading = false;
      });
    },
    handleDel: function (row) {
      this.$confirm("确认删除" + row.appName + "吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        cancelButtonClass: "custom-cancel-button",
        type: "warning",
      })
        .then(() => {
          this.loading = true;
          let para = { appId: row.appId };
          deleteApp(para).then((res) => {
            this.loading = false;
            let msg = res.data ? "删除成功" : "删除失败";
            let type = res.data ? "success" : "warning";
            this.$message({
              message: msg,
              type: type,
            });
            this.getApps();
          });
        })
        .catch(() => {});
    },
    handleEdit: function (row) {
      this.title = "编辑应用";
      this.form = Object.assign({}, row);
      this.open = true;
      this.$nextTick(() => {
        this.$refs["form"].clearValidate();
      });
    },
    handleAdd: function () {
      this.title = "添加应用";
      this.form = {
        appId: null,
        appName: null,
        uri: null,
      };
      this.open = true;
      this.$nextTick(() => {
        this.$refs["form"].clearValidate();
      });
    },
    submit: function () {
      if (this.form.appId) {
        this.editSubmit();
      } else {
        this.addSubmit();
      }
    },
    editSubmit: function () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.loading = true;
          let para = Object.assign({}, this.form);
          editApp(para).then((res) => {
            this.loading = false;
            let msg = res.data ? "编辑成功" : res.msg;
            let type = res.data ? "success" : "warning";
            this.$message({
              message: msg,
              type: type,
            });
            this.$refs["form"].resetFields();
            this.open = false;
            this.getApps();
          });
        }
      });
    },
    addSubmit: function () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.loading = true;
          let para = Object.assign({}, this.form);
          addApp(para).then((res) => {
            this.loading = false;
            let msg = res.data ? "添加成功" : res.msg;
            let type = res.data ? "success" : "warning";
            this.$message({
              message: msg,
              type: type,
            });
            this.$refs["form"].resetFields();
            this.open = false;
            this.getApps();
          });
        }
      });
    },
  },
  mounted() {
    this.getApps();
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