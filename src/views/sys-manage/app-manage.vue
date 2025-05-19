<template>
  <div class="public-block">
    <div class="Overview public-hoverItem" style="padding: 25px">
      <div class="public-firstHead">项目设置</div>
      <br />
      <el-button class="addProject" icon="el-icon-plus" @click="addApp"
        >新建项目</el-button
      >
      <br />
      <el-table
        border
        :data="appList"
        :header-cell-style="headerCellStyle"
        class="public-radius"
        :cell-style="{ textAlign: 'center' }"
        style="width: 100%; min-height: 500px"
      >
        <el-table-column label="序号" type="index" width="80" align="center">
          <template slot-scope="scope">
            <span v-text="getIndex(scope.$index)"> </span>
          </template>
        </el-table-column>
        <el-table-column label="项目信息">
          <template slot-scope="scope">
            <div class="app-table">
              <div style="text-align: left">
                <p>
                  <label style="font-weight: 400">项目名称</label>：&nbsp;{{
                    scope.row.projectDisplayName
                  }}
                </p>
                <p>
                  <label style="font-weight: 400">项目编码</label>：&nbsp;{{
                    scope.row.projectName
                  }}
                </p>
                <div style="display: flex; white-space: nowrap">
                  <label style="font-weight: 400">Token&nbsp;</label>：
                  <div style="min-width: 300px">
                    <span :id="'hide' + scope.row.projectName"
                      >****************************************************</span
                    >
                  </div>

                  <i
                    :id="'i' + scope.row.projectName"
                    slot="suffix"
                    class="icon-style el-icon-lock"
                    style="cursor: pointer"
                    @click="setPassword(scope.$index, scope.row)"
                  />
                </div>
              </div>

              <div style="text-align: left">
                <p>创建时间：&nbsp;{{ scope.row.createTime }}</p>
                <p>修改时间：&nbsp;{{ scope.row.updateTime }}</p>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" align="left" width="230">
          <template slot-scope="scope">
            <div style="display: flex; justify-content: center">
              <i
                class="el-icon-edit edit_btn"
                @click="handleUpdate(scope.row)"
              ></i>
              <i
                class="el-icon-delete delete_btn"
                @click="handleDelete(scope.row)"
              ></i>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :current-page.sync="queryParams.pageNum"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="queryParams.pageSize"
          layout="total,sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    <el-dialog
      :visible.sync="centerDialogVisible"
      :modal-append-to-body="false"
      :close-on-click-modal="true"
      :close-on-press-escape="false"
      width="80%"
      top="5vh"
      :title="title"
    >
      <el-form :model="form" ref="form" size="small" :rules="rules">
        <el-row>
          <el-col :span="11">
            <div class="gm-left-header">
              <el-form-item
                label="项目名称"
                prop="projectDisplayName"
                class="gp-item-01"
                size="medium"
              >
                <el-input
                  class="gm-input-01"
                  v-model="form.projectDisplayName"
                  placeholder="请输入项目名称"
                />
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="13">&nbsp;</el-col>
        </el-row>
        <el-row>
          <el-col :span="11">
            <div class="gm-left-header">
              <el-form-item
                label="项目编码"
                style="font-weight: 400"
                prop="projectName"
                class="gp-item-01"
              >
                <el-input
                  class="gm-input-01"
                  v-model="form.projectName"
                  placeholder="请输入项目编码"
                />
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="13"> </el-col>
        </el-row>
        <!-- 新增域名 -->
        <el-row>
          <el-col :span="11">
            <div class="gm-left-header">
              <p class="gm-h1">项目访问域名</p>
              <p class="gm-h2">请输入访问该项目的所有域名</p>

              <el-form-item prop="rootUrls" class="gp-item-01">
                <el-input
                  type="textarea"
                  rows="3"
                  class="gm-input-01"
                  v-model="form.rootUrls"
                  placeholder=""
                />
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="13">
            <div class="gm-right">
              <div class="gm-right-prompt">
                该配置用于受访页面结构化统计分析的数据展示配置，如果是WEB项目埋点必须配置该项，否则无法正常加载结构化受访页面访问统计相关数据。<br /><br />
                请输入访问该项目的所有域名(每行一个)。需包含http或https,例如:https:/kwww.a.com,htp:/192.168.1.10。
              </div>
            </div>
          </el-col>
        </el-row>
        <!-- end -->

        <el-row>
          <el-col :span="11">
            <div class="gm-left-header">
              <p class="gm-h1">排除的IP地址</p>
              <p class="gm-h2">以下IP地址将被排除在本项目统计数据外。</p>

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
              <p class="gm-h1">排除的用户代理列表</p>
              <p class="gm-h2">以下用户代理将被排除在本项目统计外。</p>

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
              <p class="gm-h1">排除的URL参数列表</p>
              <p class="gm-h2">
                以下URL参数将从本项目的受访页面分析统计中排除。
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
              <p class="gm-h1">站内搜索关键词参数</p>
              <p class="gm-h2">以下参数将作为本项目的站内搜索关键词参数。</p>

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
                该配置作为WEB项目站内搜索的搜索词提取依据，配置该项后，站内搜索才会有相关数据的统计。<br /><br />
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
            <el-button type="primary" @click="submitSaveForm">保存</el-button>
          </el-col>
        </el-row>
      </div>
    </el-dialog>

    <dialog-page ref="dialogPage"></dialog-page>
  </div>
</template>

<script>
import {
  addApp,
  getApp,
  editApp,
  deleteApp,
  getAppList,
  getAppMyList,
} from "@/api/sysmanage/appmanage";
import dialogPage from "./component/dialog";
import Cookies from "js-cookie";
import { setLocalStorage } from "@/utils/localStorage";
export default {
  name: "globalSetting",
  components: { dialogPage },
  data() {
    return {
      form: {
        id: "",
        projectDisplayName: "",
        projectName: "",
        excludedIp: "",
        excludedUa: "",
        excludedUrlParams: "",
        searchwordKey: "",
        searchwordCategoryKey: "",
        rootUrls: "",
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      rules: {
        projectDisplayName: [
          { required: true, message: "项目名称不能为空", trigger: "blur" },
        ],
        projectName: [
          { required: true, message: "项目编码不能为空", trigger: "blur" },
        ],
      },
      centerDialogVisible: false,
      operationalVisible: false,
      appList: [],
      total: 0,
      title: "",
    };
  },
  computed: {},
  watch: {},
  mounted() {
    this.getList();
  },
  methods: {
    // 更新项目列表
    updateProjectList() {
      getAppMyList().then((res) => {
        let projectArray;
        let adminNameList = ["admin", "clklog"];
        let userInfo = Cookies.get("userInfo")
          ? JSON.parse(Cookies.get("userInfo"))
          : "";
        if (userInfo.username) {
          if (adminNameList.includes(userInfo.username.trim())) {
            projectArray = res.data;
            this.$store.dispatch("tracking/setProjectArray", projectArray);
            setLocalStorage("projectList", projectArray);
          }
        }
      });
    },
    openDialogEvent() {
      this.$refs.dialogPage.dialogEvent();
    },
    headerCellStyle({ column, columnIndex }) {
      // 判断是否是第一个表头单元格
      if (columnIndex === 1) {
        return {
          "background-color": "#f7fafe",
          "padding-left": "20px",
          "text-align": "left",
        };
      } else {
        return {
          "background-color": "#f7fafe",
          "text-align": "center",
        };
      }
    },
    getIndex($index) {
      return (
        (this.queryParams.pageNum - 1) * this.queryParams.pageSize + $index + 1
      );
    },
    getList: function (flag) {
      getAppList(this.queryParams).then((response) => {
        if (response.data) {
          this.appList = response.data.rows;
          this.total = response.data.total;
          if (flag) {
          }
        }
      });
    },
    submitSaveForm: function () {
      if (window.globalConfig.is_clklog_demo_environment) {
        return this.openDialogEvent();
      }
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id) {
            editApp(this.form).then((response) => {
              this.$message({
                message: "保存成功",
                type: "success",
                duration: 2000,
              });
              // this.$bus.$emit("$updateProjectList", true);
              this.updateProjectList();
              this.getList(true);
              this.centerDialogVisible = false;
            });
          } else {
            addApp(this.form).then((response) => {
              this.$message({
                message: "保存成功",
                type: "success",
                duration: 2000,
              });
              // this.$bus.$emit("$updateProjectList", true);
              this.updateProjectList();

              this.getList(true);
              this.centerDialogVisible = false;
            });
          }
        }
      });
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val;
      this.getList();
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val;
      this.getList();
    },
    addApp() {
      this.reset();
      this.centerDialogVisible = true;
      this.title = "新增项目";
    },
    handleUpdate(row) {
      this.reset();
      const appId = row.id || this.ids;

      getApp({ id: appId }).then((response) => {
        this.form = response.data;
        this.centerDialogVisible = true;
        this.title = "修改项目";
      });
    },
    handleDelete(row) {
      if (window.globalConfig.is_clklog_demo_environment) {
        return this.openDialogEvent();
      }
      const appId = row.id || this.ids;
      this.$confirm("是否确认删除？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        cancelButtonClass: "custom-cancel-button",
        type: "warning",
      })
        .then(function () {
          return deleteApp({ id: appId });
        })
        .then(() => {
          this.getList(true);
          // this.$bus.$emit("$updateProjectList");
          this.updateProjectList();
          this.$message({ message: "删除成功", type: "success" });
        })
        .catch(() => {});
    },
    setPassword(index, row) {
      var projectName = row.projectName;
      var hideItem = document.getElementById("hide" + projectName);

      var iItem = document.getElementById("i" + projectName);
      if (hideItem.innerHTML.indexOf("*******") == -1) {
        hideItem.innerHTML =
          "****************************************************";
        // iItem.setAttribute("class", "icon-style el-icon-minus");
        iItem.setAttribute("class", "icon-style el-icon-lock");
      } else {
        hideItem.innerHTML = row.token;
        // iItem.setAttribute("class", "icon-style el-icon-view");
        iItem.setAttribute("class", "icon-style el-icon-unlock");
      }
    },
    reset() {
      this.form = {
        id: undefined,
        projectName: undefined,
        projectDisplayName: undefined,
        excludedIp: undefined,
        excludedUa: undefined,
        excludedUrlParams: undefined,
        searchwordCategoryKey: undefined,
        rootUrls: undefined,
        searchwordKey: undefined,
      };
    },
  },
  beforeDestroy() {
    // this.$bus.$off("$updateProjectList");
  },
};
</script>

<style lang="scss" scoped>
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
.addProject.el-button {
  color: #2c7be5;
  border: 1px solid #2c7be5;
  margin-bottom: 25px;
}

::v-deep {
  .el-form-item.is-error .el-input__inner,
  .el-form-item.is-error .el-input__inner:focus,
  .el-form-item.is-error .el-textarea__inner,
  .el-form-item.is-error .el-textarea__inner:focus {
    border-color: #fff;
    border-bottom: 1px solid #ff4949;
  }
  .el-form-item__label {
    font-weight: 400;
  }

  @import "~@/styles/components/el-pagination.scss";
  .el-dialog {
    border-radius: 6px;
  }

  .el-dialog__footer {
    text-align: left;
    margin-left: 30px;
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
  padding-left: 20px;
  font-weight: 400px !important;
}
.gm-right {
  width: 100%;
  margin-left: 20px;
}
.gm-h1 {
  font-size: 14px;
  color: #606266;
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
  margin-right: 20px;
  position: absolute;
  bottom: 10px;
  line-height: 28px;
}
.gm-input-01 {
  border: none !important;
  border-bottom: 1px solid #aaa;
}
.gp-item-01 {
  font-weight: 400px !important;
  width: 100%;
}
.gp-item-01 .el-form-item__content {
  width: 100%;
}
.gm-input-01 textarea {
  border: none !important;
}
.el-form-item__label {
  font-size: 20px;
}
.app-table {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 50px;
  width: 100%;
  border: none;
  color: #888;
}
.app-table tr td {
  border: none;
  padding: 0 20px;
}
.app-table .app-table-left {
  width: 45%;
}
.app-table-left span {
  display: inline-block;
}
.app-table-left label {
  color: #888;
  width: 70px !important;
  display: inline-block;
}
</style>
