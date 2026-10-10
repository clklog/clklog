<template>
  <div>
    <el-dialog
      title="提示"
      :visible.sync="operationalVisible"
      width="450px"
      center
    >
      <span>演示环境不能执行相关操作</span>
      <span slot="footer" class="dialog-footer">
        <el-button class="zc_btn" @click="operationalVisible = false"
          >确 定</el-button
        >
        <el-button
          class="zc_btn_default"       
          @click="operationalVisible = false"
          >取 消</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      operationalVisible: false,
    };
  },
  methods: {
    dialogEvent() {
      this.operationalVisible = true;
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep {
  .el-dialog {
    border-radius: 6px;
  }
  .el-dialog__footer {
    margin-left: 0 !important;
    width: 100%;
    display: flex;
    justify-content: center;
  }
}
.dialog-footer {
  display: flex;
}
</style>
