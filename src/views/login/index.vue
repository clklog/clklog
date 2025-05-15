<template>
  <div class="container">
    <!-- pc端 -->
    <div v-if="device == 'desktop'" class="container-item">
      <div style="width: 60vw">
        <img
          src="@/assets/images/loginBg.png"
          style="width: 60vw; height: 100vh; object-fit: contain"
        />
      </div>
      <div
        style="
          width: 40vw;
          background-color: #f7faff;
          padding: 171px 0;
          box-sizing: border-box;
        "
      >
        <el-form
          ref="loginForm"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          autocomplete="on"
          label-position="left"
        >
          <div class="title-container">
            <h3 class="title">
              <img class="logo_head" src="@/assets/images/logo.png" />
            </h3>
          </div>
          <div
            style="
              font-size: 14px;
              font-weight: 400;
              line-height: 21px;
              color: #4d4d4d;
              margin-bottom: 8px;
            "
          >
            账号
          </div>
          <el-form-item prop="username" style="margin-bottom: 25px">
            <el-input
              ref="username"
              v-model="loginForm.username"
              placeholder="请输入账号"
              name="username"
              type="text"
              tabindex="1"
              autocomplete="on"
            />
          </el-form-item>
          <div
            style="
              font-size: 14px;
              font-weight: 400;
              line-height: 21px;
              color: #4d4d4d;
              margin-bottom: 8px;
            "
          >
            密码
          </div>
          <el-tooltip
            v-model="capsTooltip"
            content="Caps lock is On"
            placement="right"
            manual
          >
            <el-form-item prop="password" style="margin-bottom: 25px">
              <el-input
                :key="passwordType"
                ref="password"
                v-model="loginForm.password"
                :type="passwordType"
                placeholder="请输入密码"
                name="password"
                tabindex="2"
                autocomplete="on"
                @keyup.native="checkCapslock"
                @blur="capsTooltip = false"
                @keyup.enter.native="handleLogin"
              />

              <span
                style="cursor: pointer"
                :class="
                  passwordType === 'password'
                    ? 'el-icon-lock'
                    : 'el-icon-unlock'
                "
                @click="showPwd"
              >
                <svg-icon
                  :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'"
                />
              </span>
            </el-form-item>
          </el-tooltip>
          <div
            v-if="!is_clklog_demo_experience_account"
            style="display: flex; justify-content: space-between"
          >
            <el-button
              :loading="loading"
              type="primary"
              style="width: 100%; height: 46px; margin-bottom: 30px"
              @click.native.prevent="handleLogin"
              >登录</el-button
            >
          </div>
          <div v-else style="display: flex; justify-content: space-between">
            <el-button
              :loading="loading"
              type="primary"
              style="
                width: calc(50% - 10px);
                height: 46px;
                margin-bottom: 30px;
                font-size: 16px;
              "
              @click.native.prevent="handleLogin"
              >登录</el-button
            >
            <el-button
              style="
                width: calc(50% - 10px);
                height: 46px;
                margin-bottom: 30px;
                background-color: #fd9843;
                color: #fff;
                font-size: 16px;
              "
              @click="checkLoginEvent"
              >获取体验账号</el-button
            >
          </div>

          <!-- <div style="display: flex; justify-content: space-between">
            <el-button
              :loading="loading"
              type="primary"
              style="width: calc(50% - 10px); height: 46px; margin-bottom: 30px"
              @click.native.prevent="handleLogin"
              >登录</el-button
            >
            <el-button
              style="
                width: calc(50% - 10px);
                height: 46px;
                margin-bottom: 30px;
                background-color: #fd9843;
                color: #fff;
                font-size: 16px;
              "
              @click="checkLoginEvent"
              >获取体验账号</el-button
            >
          </div> -->
        </el-form>
      </div>
    </div>
    <!-- 移动端 -->
    <div class="container-item" v-else style="width: 100vw; height: 100vh">
      <div
        style="
          background-color: #f7faff;
          box-sizing: border-box;
          width: 100%;
          height: 100%;
        "
      >
        <el-form
          ref="loginForm"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          style="
            height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding: 0;
          "
          autocomplete="on"
          label-position="left"
        >
          <div class="title-container">
            <h3 class="title">
              <img class="logo_head" src="@/assets/images/logo.png" />
            </h3>
          </div>
          <div
            style="
              font-size: 14px;
              font-weight: 400;
              line-height: 21px;
              color: #4d4d4d;
              margin-bottom: 8px;
            "
          >
            账号
          </div>
          <el-form-item prop="username" style="margin-bottom: 25px">
            <el-input
              ref="username"
              v-model="loginForm.username"
              placeholder="请输入账号"
              name="username"
              type="text"
              tabindex="1"
              autocomplete="on"
            />
          </el-form-item>
          <div
            style="
              font-size: 14px;
              font-weight: 400;
              line-height: 21px;
              color: #4d4d4d;
              margin-bottom: 8px;
            "
          >
            密码
          </div>
          <el-tooltip
            v-model="capsTooltip"
            content="Caps lock is On"
            placement="right"
            manual
          >
            <el-form-item prop="password" style="margin-bottom: 25px">
              <el-input
                :key="passwordType"
                ref="password"
                v-model="loginForm.password"
                :type="passwordType"
                placeholder="请输入密码"
                name="password"
                tabindex="2"
                autocomplete="on"
                @keyup.native="checkCapslock"
                @blur="capsTooltip = false"
                @keyup.enter.native="handleLogin"
              />
              <span
                style="cursor: pointer"
                :class="
                  passwordType === 'password'
                    ? 'el-icon-lock'
                    : 'el-icon-unlock'
                "
                @click="showPwd"
              >
                <svg-icon
                  :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'"
                />
              </span>
            </el-form-item>
          </el-tooltip>

          <div
            v-if="!is_clklog_demo_experience_account"
            style="display: flex; justify-content: space-between"
          >
            <el-button
              :loading="loading"
              type="primary"
              style="width: 100%; height: 46px; margin-bottom: 30px"
              @click.native.prevent="handleLogin"
              >登录</el-button
            >
          </div>
          <div v-else style="display: flex; justify-content: space-between">
            <el-button
              :loading="loading"
              type="primary"
              style="
                width: calc(50% - 10px);
                height: 46px;
                margin-bottom: 30px;
                font-size: 16px;
              "
              @click.native.prevent="handleLogin"
              >登录</el-button
            >
            <el-button
              style="
                width: calc(50% - 10px);
                height: 46px;
                margin-bottom: 30px;
                background-color: #fd9843;
                color: #fff;
                font-size: 16px;
              "
              @click="checkLoginEvent"
              >获取体验账号</el-button
            >
          </div>
        </el-form>
      </div>
    </div>

    <el-dialog :visible.sync="dialogVisible" width="400px" center>
      <div
        style="
          display: flex;
          flex-direction: column;
          align-items: center;
          font-size: 15px;
          font-weight: 500;
        "
      >
        <div>微信扫描下方二维码</div>
        <div style="margin: 20px 0">关注公众号获取体验账号</div>
        <img
          src="@/assets/images/qrcode.png"
          style="width: 200px; height: 200px; object-fit: cover"
        />
        <el-button
          :loading="loading"
          type="primary"
          style="width: 100px; height: 35px; margin-top: 40px"
          @click.native.prevent="dialogVisible = false"
          >确定</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import SocialSign from "./components/SocialSignin";
import { profileApi, subscribeApi } from "@/api/trackingapi/subscribe.js";
import Cookies from "js-cookie";
import { mapState } from "vuex";
import ResizeMixin from "@/layout/mixin/ResizeHandler";
import { login } from "@/api/user";
import { getAppMyList } from "@/api/sysmanage/appmanage";
import { setLocalStorage } from "@/utils/localStorage";
export default {
  name: "Login",
  components: { SocialSign },
  mixins: [ResizeMixin],
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入账号"));
      } else {
        callback();
      }
    };
    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入密码"));
      } else {
        callback();
      }
    };
    return {
      loginForm: {
        username: "",
        password: "",
      },
      dialogVisible: false,
      loginRules: {
        username: [
          { required: true, trigger: "blur", validator: validateUsername },
        ],
        password: [
          { required: true, trigger: "blur", validator: validatePassword },
        ],
      },
      passwordType: "password",
      capsTooltip: false,
      loading: false,
      showDialog: false,
      redirect: undefined,
      otherQuery: {},
      validatePassword: this.$store.getters.password,
      clientId: "",
      subscribed: "",
      is_clklog_demo_experience_account: false,
    };
  },
  watch: {
    $route: {
      handler: function (route) {
        const query = route.query;
        if (query) {
          this.redirect = query.redirect;
          this.otherQuery = this.getOtherQuery(query);
        }
      },
      immediate: true,
    },
  },
  computed: {
    ...mapState({
      device: (state) => state.app.device,
    }),
  },
  created() {
    this.$bus.$on("$loginOpen", (res) => {
      console.log(res, "loginOpen");
      this.initClklog(res);
    });

    if (window.globalConfig.is_clklog_demo_experience_account) {
      this.is_clklog_demo_experience_account =
        window.globalConfig.is_clklog_demo_experience_account;
    }
    if (Cookies.get("userInfo")) {
      let userInfo = JSON.parse(Cookies.get("userInfo"));
      this.loginForm.username = userInfo.username;
    }
  },
  mounted() {
    if (this.loginForm.username === "") {
      this.$refs.username.focus();
    } else if (this.loginForm.password === "") {
      this.$refs.password.focus();
    }
  },
  methods: {
    openShow() {
      this.initClklog("sub");
    },
    checkLoginEvent() {
      console.log("执行了操作");

      this.dialogVisible = true;
    },
    checkCapslock(e) {
      const { key } = e;
      this.capsTooltip = key && key.length === 1 && key >= "A" && key <= "Z";
    },
    showPwd() {
      if (this.passwordType === "password") {
        this.passwordType = "";
      } else {
        this.passwordType = "password";
      }
      this.$nextTick(() => {
        this.$refs.password.focus();
      });
    },
    async handleLogin() {
      try {
        const valid = await this.$refs.loginForm.validate();
        if (!valid) {
          console.log("表单验证失败");
          return false;
        }

        this.loading = true;

        // 调用登录接口
        const response = await login({
          username: this.loginForm.username.trim(),
          password: this.loginForm.password,
        });

        if (response.code !== 200) {
          throw new Error(response.message || "登录失败");
        }

        // 保存用户信息到Cookie
        Cookies.set("userInfo", JSON.stringify(this.loginForm));

        // 执行登录操作
        await this.$store.dispatch("user/login", response);

        // 根据设备类型跳转不同页面
        if (this.device === "desktop") {
          await this.$router.push({
            path: this.redirect || "/",
            query: this.otherQuery,
          });
        } else {
          await this.$router.push("/record/summary");
        }

        // 获取应用列表
        const appListResponse = await getAppMyList();
        if (appListResponse.code === 200) {
          let projectArray = appListResponse.data || [];
          // 初始化clklog
          await this.initClklog();
          // 将项目列表存储到 Vuex store
          this.$store.dispatch("tracking/setProjectArray", projectArray);
          // 将项目列表存储到 localStorage
          setLocalStorage("projectList", projectArray);
        }
      } catch (error) {
        console.error("登录失败:", error);
      } finally {
        this.loading = false;
      }
    },
    initClklog(flag) {
      profileApi().then((res) => {
        if (res.code == 200) {
          this.clientId = res.data;
          // res.data.subscribed = false;
          if (!res.data.subscribed) {
            setTimeout(() => {
              this.$nextTick(() => {
                this.$bus.$emit("$letter", res.data);
              });
            }, 500);
          }
        }
      });
    },
    getOtherQuery(query) {
      return Object.keys(query).reduce((acc, cur) => {
        if (cur !== "redirect") {
          acc[cur] = query[cur];
        }
        return acc;
      }, {});
    },
  },
};
</script>

<style lang="scss">
$bg: #fff;
$light_gray: #7b7a7b;
$cursor: #7b7a7b;
@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .container-item .el-input input {
    color: #7b7a7b;
  }
}

.container-item {
  display: flex;

  .el-input {
    display: inline-block;
    height: 46px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 14px;
      color: $light_gray;
      height: 46px;
      caret-color: $cursor;
    }
  }

  .el-form-item {
    border: 1px solid #ddeafb;
    background: #fff !important;
    border-radius: 6px;
    color: #454545;
  }
}
</style>

<style lang="scss" scoped>
$bg: #fff;
$dark_gray: #889aa4;
$light_gray: #eee;

.el-dialog__body {
  padding: 0 30px 30px;
}

.el-dialog__headerbtn {
  top: 14px !important;
}

.container-item {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;

  // 登录注册
  .loginWarry {
    width: 85%;
    padding: 35px 0;
    margin: 0 auto;
    text-align: center;

    .colFont {
      font-size: 12px;
      font-weight: 500;
      line-height: 20px;
      color: #4d4d4d;
    }

    .registration {
      font-size: 12px;
      font-weight: 400;
      line-height: 22px;
      color: #2c7be5;
      cursor: pointer;
    }
  }

  .login-form {
    position: relative;
    width: 80%;
    padding: 103px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .tips {
    font-size: 14px;
    color: #7b7a7b;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 0px;
    color: $dark_gray;
    vertical-align: middle;
    // width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;

      .logo_head {
        height: 66px;
        width: 194px;
        object-fit: cover;
      }
    }
  }

  .el-icon-view {
    font-size: 16px;
    cursor: pointer;
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }

  .thirdparty-button {
    position: absolute;
    right: 0;
    bottom: 6px;
  }

  @media only screen and (max-width: 470px) {
    .thirdparty-button {
      display: none;
    }
  }
}
</style>
