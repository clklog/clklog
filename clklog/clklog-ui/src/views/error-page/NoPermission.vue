<template>
  <div class="nopermission-page">
    <div class="wscn-http404-pc nopermission-pc">
      <img class="no-able-img" src="@/assets/permission_imgs/nopermission2.png" alt="">
      <div class="bullshit base-info">
        <div class="bullshit__oops">Oops!</div>
      </div>
      <div class="tips-list" style="">
        <div>访问出错了！</div>
        <div>请检查您的路径输入是否无误或者是否有输入页面的访问权限!</div>
      </div>
    </div>
    <div class="nopermission-mobile">
      <div class="nopermission-mobile-content">
        <img class="no-able-img-mobile" src="@/assets/permission_imgs/nopermission2.png" alt="">
        <div class="base-info">
          <div class="nopermission-mobile-oops">Oops!</div>
          <div class="tips-list">
            <div>访问出错了！</div>
            <div>请检查您的路径输入是否无误或者是否有输入页面的访问权限!</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'NoPermission',
  computed: {
    message() {
      return '请确认当前账号是否分配项目权限'
    }
  }
}
</script>

<style lang="scss" scoped>
.nopermission-page {
  background: rgb(247, 250, 255);
  width: 100%;
  height: calc(100vh - 50px);
  box-sizing: border-box;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow-y: auto;

  .tips-list {
    font-size: 15px;
    color: #333;
    line-height: 30px;
    text-align: center;
  }

  .no-able-img {
    width: 350px;
    height: auto;
    object-fit: contain;
    flex-shrink: 0;
  }

  .wscn-http404-pc {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 80%;
    max-width: 800px;
    height: 530px;
    padding: 0 50px;

    .bullshit {
      width: 300px;

      &__oops {
        font-size: 32px;
        font-weight: bold;
        line-height: 40px;
        color: #1482f0;
        opacity: 0;
        margin-bottom: 20px;
        animation-name: slideUp;
        animation-duration: 0.5s;
        animation-fill-mode: forwards;
      }

      &__headline {
        font-size: 20px;
        line-height: 24px;
        color: #222;
        font-weight: bold;
        opacity: 0;
        margin-bottom: 10px;
        animation-name: slideUp;
        animation-duration: 0.5s;
        animation-delay: 0.1s;
        animation-fill-mode: forwards;
      }

      &__info {
        font-size: 13px;
        line-height: 21px;
        color: grey;
        opacity: 0;
        margin-bottom: 30px;
        animation-name: slideUp;
        animation-duration: 0.5s;
        animation-delay: 0.2s;
        animation-fill-mode: forwards;
      }

      &__return-home {
        display: block;
        float: left;
        width: 110px;
        height: 36px;
        background: #1482f0;
        border-radius: 100px;
        text-align: center;
        color: #ffffff;
        opacity: 0;
        font-size: 14px;
        line-height: 36px;
        cursor: pointer;
        animation-name: slideUp;
        animation-duration: 0.5s;
        animation-delay: 0.3s;
        animation-fill-mode: forwards;
      }

      @keyframes slideUp {
        0% {
          transform: translateY(60px);
          opacity: 0;
        }

        100% {
          transform: translateY(0);
          opacity: 1;
        }
      }
    }
  }

  .nopermission-mobile {
    display: none;
    width: 100%;
    max-width: 300px;
    height: 100%;
    box-sizing: border-box;

    .nopermission-mobile-content {
      width: 100%;
      padding: 0 20px;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      align-items: flex-start;
    }

    .nopermission-mobile-oops {
      font-size: 34px;
      font-weight: 700;
      line-height: 40px;
      color: #1482f0;
      margin: 0 0 12px 0;
    }

    .nopermission-mobile-headline {
      font-size: 18px;
      line-height: 24px;
      color: #222;
      font-weight: 700;
      margin: 0 0 16px 0;
    }

    .no-able-img-mobile {
      width: 100%;
      height: auto;
      object-fit: contain;
      flex-shrink: 0;
    }
  }
}

.base-info {
  width: 100%;
  text-align: center;
}

@media screen and (max-width: 992px) {
  .nopermission-page {

    height: calc(100vh - 50px);
    box-sizing: border-box;
    overflow-y: auto;
    padding: 72px 0 40px 0;

    .nopermission-pc {
      display: none;
    }

    .nopermission-mobile {
      display: flex;
      justify-content: center;
    }
  }
}
</style>
