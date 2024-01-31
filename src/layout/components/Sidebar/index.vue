<template>
  <div :class="{ 'has-logo': showLogo }">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <!-- <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        background-color="#fff"
        text-color="#4D4D4D"
        active-text-color="#000000"
        :unique-opened="false"
        :collapse-transition="false"
        @open="handleOpen"
        mode="vertical"
      > -->
      <el-menu
        background-color="#fff"
        text-color="#4D4D4D"
        active-text-color="#000000"
        :unique-opened="false"
        :collapse-transition="false"
        :collapse="isCollapse"
        mode="vertical"
        :default-active="activeMenu"
        :default-openeds="defaultOpen"
        @open="handleOpen"
        @close="handleClose"
      >
        <sidebar-item
          v-for="route in permission_routes"
          :key="route.path"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import Logo from "./Logo";
import SidebarItem from "./SidebarItem";
import variables from "@/styles/variables.scss";

export default {
  components: { SidebarItem, Logo },
  computed: {
    ...mapGetters(["permission_routes", "sidebar"]),
    activeMenu() {
      const route = this.$route;
      const { meta, path } = route;
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu;
      }
      return path;
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo;
    },
    variables() {
      return variables;
    },
    isCollapse() {
      return !this.sidebar.opened;
    },
  },
  data() {
    return {
      defaultOpen: [],
    };
  },
  methods: {
    handleOpen(key, keyPath) {
      if (keyPath.length == 1) {
        this.defaultOpen = [key];
      }
    },
    handleClose(key, keyPath) {},
  },
};
</script>
<style>
#app .sidebar-container .el-submenu__title:hover {
  background: linear-gradient(to right, #d5e5fa, #fff) !important;
}
#app .sidebar-container .submenu-title-noDropdown:hover {
  background: linear-gradient(to right, #d5e5fa, #fff) !important;
}
</style>
<style lang="scss" scoped>
::v-deep {
  #app .sidebar-container .el-submenu .el-menu-item.is-active {
    background: linear-gradient(to right, #d5e5fa, #fff) !important;
  }
  .el-menu-item.is-active {
    background: linear-gradient(to right, #d5e5fa, #fff) !important;
  }
}
</style>
