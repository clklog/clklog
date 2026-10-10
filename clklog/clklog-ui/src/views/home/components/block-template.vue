<template>
  <div class="TrendChart">TrendChart</div>
</template>

<script>
export default {
  name: "TrendChart",
  props: {
    commonParams: {},
  },
  data() {
    return {
      chartData: null,
    };
  },
  computed: {
    params() {
      return Object.assign({}, this.commonParams);
    },
  },
  watch: {},
  methods: {},
};
</script>

<style lang="scss" scoped>
</style>
