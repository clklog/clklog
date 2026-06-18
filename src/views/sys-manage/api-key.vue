<template>
  <div class="public-block">
    <div class="Overview public-hoverItem" style="padding: 25px">
      <div class="public-firstHead">API密钥管理</div>
      <br>
      <el-button class="addApiKey" icon="el-icon-plus" @click="addApiKey">新建API密钥</el-button>
      <br>
      <el-table border :data="apiKeyList" :header-cell-style="headerCellStyle" class="public-radius"
        :cell-style="{ textAlign: 'center' }" style="width: 100%; min-height: 500px">
        <el-table-column label="序号" type="index" :width="$fixedWidth.FwIndex" align="center">
          <template slot-scope="scope">
            <span v-text="getIndex(scope.$index)" />
          </template>
        </el-table-column>
        <el-table-column label="密钥信息" min-width="300px">
          <template slot-scope="scope">
            <div class="app-table-info">
              <div class="info-left">
                <div class="info-item">
                  <label class="info-label">显示名称：</label>
                  <span class="info-value">{{ scope.row.displayName }}</span>
                </div>
                <div class="info-item">
                  <label class="info-label">API密钥：</label>
                  <div class="key-wrapper">
                    <span :id="'hideKey' + scope.row.id" class="key-text">{{ scope.row.showKey ? scope.row.apiKey :
                      maskKey(scope.row.apiKey) }}</span>
                    <!-- <i :id="'iKey' + scope.row.id" :class="['icon-style', scope.row.showKey ? 'el-icon-unlock' : 'el-icon-lock']"
                      @click="toggleKeyVisibility(scope.$index, scope.row)" /> -->
                  </div>
                </div>
              </div>
              <!-- <div class="info-right">
                <div class="info-item">
                  <label class="info-label">创建时间：</label>
                  <span class="info-value">{{ scope.row.createdAt }}</span>
                </div>
                <div class="info-item">
                  <label class="info-label">过期时间：</label>
                  <span class="info-value">{{ formatExpireTime(scope.row.expiresAt) }}</span>
                </div>
              </div> -->
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="150">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '启用' ? 'success' : 'warning'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="200px">
          <template slot-scope="scope">
            {{ scope.row.createdAt }}
          </template>
        </el-table-column>
        <el-table-column label="过期时间" min-width="200px">
          <template slot-scope="scope">
            {{ formatExpireTime(scope.row.expiresAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="left" min-width="200px">
          <template slot-scope="scope">
            <div style="display: flex; justify-content: center">
              <i class="el-icon-edit edit_btn" @click="handleUpdate(scope.row)" />
              <i class="el-icon-delete delete_btn" @click="handleDelete(scope.row)" />
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog :visible.sync="centerDialogVisible" :modal-append-to-body="false" :close-on-click-modal="true"
      :close-on-press-escape="false" width="600px" top="10vh" :title="title">
      <el-form ref="form" :model="form" size="small" :rules="rules" label-width="100px">
        <el-form-item label="显示名称" prop="displayName" size="medium">
          <el-input v-model="form.displayName" placeholder="请输入密钥显示名称" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="过期时间" prop="expiresAt" size="medium">
          <div style="display: flex; align-items: center;">
            <el-date-picker v-model="form.expiresAt" type="datetime" placeholder="选择过期时间"
              :picker-options="pickerOptions" style="flex: 1;"  value-format="yyyy-MM-dd HH:mm:ss" />
            <span style="margin-left: 15px; color: #909399; font-size: 13px; white-space: nowrap;">留空则永久有效</span>
          </div>
        </el-form-item>
        <el-form-item v-if="form.id" label="状态" prop="status" size="medium">
          <!-- <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
            <el-option label="启用" value="启用" />
            <el-option label="禁用" value="禁用" />
          </el-select> -->
          <el-radio-group v-model="form.status" style="width: 100%;">
            <el-radio label="启用">启用</el-radio>
            <el-radio label="禁用">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer" style="text-align: right; display: flex; justify-content: center;">
        <el-button class="zc_btn" @click="submitSaveForm">保存</el-button>
        <el-button class="zc_btn_default" @click="centerDialogVisible = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="showKeyDialogVisible" :modal-append-to-body="false" :close-on-click-modal="false"
      :close-on-press-escape="false" width="600px" top="15vh" custom-class="success-key-dialog">
      <div slot="title" class="success-dialog-title">
        <i class="el-icon-success"
          style="color: #67C23A; font-size: 24px; margin-right: 10px; vertical-align: middle;"></i>
        <span style="font-weight: bold; font-size: 18px; vertical-align: middle;">API密钥创建成功</span>
      </div>
      <div class="show-key-content">
        <div class="warning-banner">
          <i class="el-icon-warning"></i>
          <span>请立即复制并妥善保存以下密钥信息，关闭后将无法再次查看完整的密钥！</span>
        </div>
        <div class="key-display-card">
          <div class="key-label">API Key</div>
          <div class="key-value-box">
            <code class="key-code">{{ newApiKey }}</code>
            <el-tooltip content="复制密钥" placement="top">
              <el-button type="text" icon="el-icon-document-copy" class="copy-btn"
                @click="copyToClipboard(newApiKey)">复制</el-button>
            </el-tooltip>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer" style="display: flex; justify-content: center;align-items: center;">
        <el-button class="zc_btn" @click="closeShowKeyDialog">确定</el-button>
        <el-button class="zc_btn_default" @click="closeShowKeyDialog">取消</el-button>
      </div>
    </el-dialog>

    <dialog-page ref="dialogPage" />
  </div>
</template>

<script>
import {
  addApiKey,
  getApiKey,
  editApiKey,
  deleteApiKey,
  listApiKey
} from '@/api/sysmanage/apikey'
import dialogPage from './component/dialog'
export default {
  name: 'ApiKey',
  components: { dialogPage },
  data() {
    return {
      form: {
        id: '',
        displayName: '',
        expiresAt: '',
        status: '启用'
      },
      rules: {
        displayName: [
          { required: true, message: '显示名称不能为空', trigger: 'blur' }
        ]
      },
      centerDialogVisible: false,
      apiKeyList: [],
      title: '',
      pickerOptions: {
        shortcuts: [
          {
            text: '一周后',
            onClick(picker) {
              const date = new Date()
              date.setDate(date.getDate() + 7)
              picker.$emit('pick', date)
            }
          },
          {
            text: '一个月后',
            onClick(picker) {
              const date = new Date()
              date.setMonth(date.getMonth() + 1)
              picker.$emit('pick', date)
            }
          },
          {
            text: '三个月后',
            onClick(picker) {
              const date = new Date()
              date.setMonth(date.getMonth() + 3)
              picker.$emit('pick', date)
            }
          }
        ]
      },
      showKeyDialogVisible: false,//生成密钥弹窗
      newApiKey: '',
      newApiSecret: ''
    }
  },
  computed: {},
  watch: {},
  mounted() {
    this.getList()
  },
  methods: {
    openDialogEvent() {
      this.$refs.dialogPage.dialogEvent()
    },
    headerCellStyle({ column, columnIndex }) {
      if (columnIndex === 1) {
        return {
          'background-color': '#f7fafe',
          'padding-left': '20px',
          'text-align': 'left'
        }
      } else {
        return {
          'background-color': '#f7fafe',
          'text-align': 'center'
        }
      }
    },
    getIndex($index) {
      return $index + 1
    },
    getList() {
      listApiKey({}).then((response) => {
        if (response.data) {
          this.apiKeyList = response.data
        }
      })
    },
    formatExpireTime(expiresAt) {
      if (!expiresAt) {
        return '永不过期'
      }
      return expiresAt
    },
    maskKey(key) {
      if (!key) return ''
      if (key.length <= 10) return '**********'
      return key.substring(0, 6) + '************************' + key.substring(key.length - 4)
    },
    toggleKeyVisibility(index, row) {
      this.$set(row, 'showKey', !row.showKey)
    },
    copyToClipboard(text) {
      const textarea = document.createElement('textarea')
      textarea.value = text
      textarea.style.position = 'fixed'
      textarea.style.left = '-9999px'
      document.body.appendChild(textarea)
      textarea.select()
      try {
        document.execCommand('copy')
        this.$message({
          message: '复制成功',
          type: 'success',
          duration: 1500
        })
      } catch (err) {
        this.$message({
          message: '复制失败，请手动复制',
          type: 'error',
          duration: 2000
        })
      }
      document.body.removeChild(textarea)
    },
    closeShowKeyDialog() {
      this.showKeyDialogVisible = false
      this.newApiKey = ''
      this.newApiSecret = ''
    },
    submitSaveForm() {
      if (window.globalConfig.is_clklog_demo_environment) {
        return this.openDialogEvent()
      }
      this.$refs['form'].validate((valid) => {
        if (valid) {
          const params = {
            ...this.form
          }
          if (this.form.id) {
            editApiKey(params).then((response) => {
              this.$message({
                message: '保存成功',
                type: 'success',
                duration: 2000
              })
              this.getList()
              this.centerDialogVisible = false
            })
          } else {
            addApiKey(params).then((response) => {
              if (response.data) {
                this.newApiKey = response.data.apiKey || ''
                this.newApiSecret = response.data.apiSecret || ''
                this.showKeyDialogVisible = true
              }
              this.getList()
              this.centerDialogVisible = false
            })
          }
        }
      })
    },
    addApiKey() {
      this.reset()
      this.centerDialogVisible = true
      this.title = '新建API密钥'
    },
    handleUpdate(row) {
      this.reset()
      const apiKeyId = row.id
      getApiKey({ id: apiKeyId }).then((response) => {
        this.form = response.data
        this.centerDialogVisible = true
        this.title = '修改API密钥'
      })
    },
    handleDelete(row) {
      if (window.globalConfig.is_clklog_demo_environment) {
        return this.openDialogEvent()
      }
      const apiKeyId = row.id
      this.$confirm('是否确认删除该API密钥？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        cancelButtonClass: 'custom-cancel-button',
        type: 'warning'
      })
        .then(function () {
          return deleteApiKey({ id: apiKeyId })
        })
        .then(() => {
          this.getList()
          this.$message({ message: '删除成功', type: 'success' })
        })
        .catch(() => { })
    },
    reset() {
      this.form = {
        id: undefined,
        displayName: undefined,
        expiresAt: '',
        status: '启用'
      }
    }
  }
}
</script>

<style lang="scss" scoped>
::v-deep {
  .el-dialog__body {
    padding: 24px 50px 0 24px;
  }
  .el-form-item {
    margin-bottom: 15px;
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

.addApiKey.el-button {
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
    border: 1px solid #ff4949;
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
    border: 1px solid #dcdfe6;
  }

  .el-input__inner {
    border: 1px solid #dcdfe6;
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
  width: 100%;
}

.gp-item-01 {
  font-weight: 400px !important;
  width: 100%;
}

.gp-item-01 .el-form-item__content {
  width: 100%;
}

.gm-input-01 textarea {
  border: 1px solid #dcdfe6;
}

.el-form-item__label {
  font-size: 14px;
}

.app-table-info {
  display: flex;
  justify-content: space-between;
  padding: 0px 10px;
  color: #606266;
  width: 100%;

  .info-left,
  .info-right {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 5px;
  }

  .info-left {
    padding-right: 0px;
  }

  .info-item {
    display: flex;
    align-items: center;
    line-height: 1.4;
  }

  .info-label {
    width: 85px;
    color: #909399;
    font-weight: 400;
    flex-shrink: 0;
    text-align: left;
  }

  .info-value {
    color: #303133;
    word-break: break-all;
  }

  .key-wrapper {
    display: flex;
    align-items: center;
    gap: 10px;
    flex: 1;

    .key-text {
      font-family: monospace;
      background: #f5f7fa;
      padding: 2px 6px;
      border-radius: 4px;
      text-align: left;
      color: #303133;
    }

    .icon-style {
      cursor: pointer;
      color: #409EFF;
      font-size: 16px;

      &:hover {
        color: #66b1ff;
      }
    }
  }
}

.show-key-content {
  padding: 10px 0;
}

.warning-banner {
  background-color: #FFF7E8;
  border: 1px solid #FFD591;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 12px;
  display: flex;
  align-items: flex-start;
  color: #FA8C16;
  font-size: 13px;
  line-height: 1.5;

  i {
    margin-right: 8px;
    margin-top: 2px;
    font-size: 16px;
  }
}

.key-display-card {
  background: #F9FBFF;
  border-radius: 8px;
  padding: 15px;
  border: 1px dashed #ADC6FF;
}

.key-label {
  font-size: 12px;
  color: #8C8C8C;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.key-value-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #FFFFFF;
  border: 1px solid #D9D9D9;
  border-radius: 4px;
  padding: 8px 12px;
}

.key-code {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 15px;
  color: #262626;
  word-break: break-all;
  user-select: all;
}

.copy-btn {
  padding: 0;
  margin-left: 15px;
  font-size: 14px;
  font-weight: 500;

  &:hover {
    color: #40a9ff;
  }
}

.success-key-dialog {
  .el-dialog__header {
    border-bottom: 1px solid #F0F0F0;
    padding: 20px 24px;
  }

  .el-dialog__footer {
    padding: 24px;
    border-top: none;
  }
}
</style>
