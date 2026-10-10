<template>
  <div class="zc_overview">
    <div class="bid-list-headeded">
      <div
        class="header-name w156"
        :style="{ width: width + '%' }"
        v-for="(item, index) in labelList"
        :key="index"
      >
        {{ item.name }}
        <div class="parentPosition">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    labelList: {
      type: Array,
      default: [],
    },
    width: {
      type: [Number, String],
      default: 16,
    },
  },
};
</script>

<style lang="scss" scoped>
.zc_overview {
  .bid-list-headeded {
    padding-bottom: 10px;
    padding-top: 15px;
    display: flex;
    .header-name {
      justify-content: center;
      height: 30px;
      display: flex;
      align-items: center;
    }

    .w156 {
      font-size: 15px;
      font-weight: 400;
      line-height: 16px;
      color: #4d4d4d;
    }
    img {
      width: 11.44px;
      height: 11.44px;
      margin-left: 12px;
      cursor: pointer;
    }
  }
  .parentPosition {
    position: relative;
    .popImg {
      position: absolute;
      left: 0;
      transform: translate(0, -50%); //左右上下
    }
  }
}
</style>
