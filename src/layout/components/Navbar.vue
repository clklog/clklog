<template>
  <div class="navbar">
    <!-- <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" /> -->

    <!-- <breadcrumb id="breadcrumb-container" class="breadcrumb-container" /> -->

    <!-- 埋点系统 -->
    <div class="burying_point">
      <div style="display: flex; align-items: center">
        <img src="@/assets/images/burying-logo.png" alt="" />
        <span>埋点系统</span>
        <el-select
          v-model="value"
          placeholder="请选择"
          class="custom_select"
          @change="handleChangeProject"
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </div>

      <div class="right-menu">
        <!-- <template v-if="device !== 'mobile'">
        <search id="header-search" class="right-menu-item" />

        <error-log class="errLog-container right-menu-item hover-effect" />

        <screenfull id="screenfull" class="right-menu-item hover-effect" />

        <el-tooltip content="Global Size" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip>
      </template> -->
        <div class="dataTime">2023年05月18日 星期四</div>
        <!-- <img
          :src="avatar + '?imageView2/1/w/80/h/80'"
          class="user-avatar"
          @click="clickImg"
        /> -->
        <img
          src="@/assets/images/avator.jpg"
          class="user-avatar"
          @click="clickImg"
        />
        <el-dropdown
          class="avatar-container right-menu-item hover-effect"
          trigger="click"
        >
          <div class="avatar-wrapper">
            <i class="el-icon-caret-bottom" />
          </div>
          <el-dropdown-menu slot="dropdown">
            <router-link to="/visitorAnalysis/trendAnalysis">
              <el-dropdown-item>访客分析</el-dropdown-item>
            </router-link>
            <router-link to="/behaviorAnalysis/user-behavior-analysis">
              <el-dropdown-item>行为分析</el-dropdown-item>
            </router-link>
            <!-- <a
              target="_blank"
              href="https://github.com/PanJiaChen/vue-element-admin/"
            >
              <el-dropdown-item>Github</el-dropdown-item>
            </a> -->
            <!-- <a
              target="_blank"
              href="https://panjiachen.github.io/vue-element-admin-site/#/"
            >
              <el-dropdown-item>Docs</el-dropdown-item>
            </a> -->
            <el-dropdown-item divided @click.native="logout">
              <span style="display: block">退出</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <dialogs ref="child" />
  </div>
</template>

<script>
import { mapGetters } from "vuex";
// import Breadcrumb from '@/components/Breadcrumb'
// import Hamburger from '@/components/Hamburger'
import ErrorLog from "@/components/ErrorLog";
import Screenfull from "@/components/Screenfull";
import SizeSelect from "@/components/SizeSelect";
import Search from "@/components/HeaderSearch";
import dialogs from "./dialog/index";

export default {
  components: {
    // Breadcrumb,
    // Hamburger,
    ErrorLog,
    Screenfull,
    SizeSelect,
    Search,
    dialogs,
  },
  data() {
    return {
      options: [
        {
          value: "货清清",
          label: "货清清",
        },
        {
          value: "在线拍",
          label: "在线拍",
        },
        {
          value: "上海机动车",
          label: "上海机动车",
        },
        {
          value: "国拍",
          label: "国拍",
        },
      ],
      value: "",
    };
  },
  created() {
    this.value = this.options[0].value;
    this.handleChangeProject(this.options[0].value);
  },
  computed: {
    // ...mapGetters(["sidebar", "avatar", "device"]),
    ...mapGetters(["sidebar", "avatar", "device", "project"]),
  },
  methods: {
    toggleSideBar() {
      // this.$store.dispatch("app/toggleSideBar");
    },
    async logout() {
      await this.$store.dispatch("user/logout");
      this.$router.push(`/login?redirect=${this.$route.fullPath}`);
    },
    clickImg() {
      // console.log(this.$refs.child.callMethod,234)
      this.$refs.child.callMethod();
    },
    handleChangeProject(val) {
      this.$store.dispatch("tracking/setProject", val);
    },
  },
};
</script>

<style lang="scss" scoped>
.navbar {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  height: 50px;
  overflow: hidden;
  // position: relative;
  width: 100%;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 1002;
  .burying_point {
    display: flex;
    justify-content: space-between;
    height: 50px;
    margin-left: 21px;
    .custom_select {
      max-width: 120px;
      ::v-deep {
        .el-select .el-input.is-focus .el-input__inner {
          background-color: #fff !important;
        }
        .el-select:hover .el-input__inner {
          border-color: #fff;
        }
        .el-select .el-input.is-focus .el-input__inner {
          border-color: #fff;
        }
        .el-select .el-input__inner:focus {
          border-color: #fff;
        }
        .el-input__inner {
          border: none;
        }
      }
    }
    img {
      width: 37px;
      height: 27px;
    }
    span {
      margin-left: 10px;
      margin-right: 53px;
      font-size: 13px;
      font-weight: bold;
      line-height: 17px;
      color: #4d4d4d;
    }
  }
  // .hamburger-container {
  //   line-height: 46px;
  //   height: 100%;
  //   float: left;
  //   cursor: pointer;
  //   transition: background 0.3s;
  //   -webkit-tap-highlight-color: transparent;

  //   &:hover {
  //     background: rgba(0, 0, 0, 0.025);
  //   }
  // }

  .breadcrumb-container {
    float: left;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    // float: right;
    display: flex;
    align-items: center;
    height: 100%;
    line-height: 50px;
    .dataTime {
      font-size: 11px;
      font-weight: 400;
      line-height: 14px;
      color: #656d92;
      margin-right: 29px;
    }
    .user-avatar {
      cursor: pointer;
      width: 40px;
      height: 40px;
      border-radius: 10px;
    }
    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
