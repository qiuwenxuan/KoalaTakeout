<!--整体后台管理样式框架：主体、侧边栏、数据表格-->
<template>
  <div class="manager-container">
    <!--  头部  -->
    <div class="manager-header">
      <div class="manager-header-left">
        <img src="../assets/imgs/logo1.png" class="enlarge-image"/>
        <div class="title">考拉外卖</div>
      </div>

      <div class="manager-header-center">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: $route.path }">{{ $route.meta.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="manager-header-right">
        <el-dropdown placement="bottom">
          <div class="avatar">
            <img :src="user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"/>
            <div>{{ user.name || '' }}</div>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="goToPerson">个人信息</el-dropdown-item>
            <el-dropdown-item @click.native="$router.push('/password')">修改密码</el-dropdown-item>
            <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <!--  主体  -->
    <div class="manager-main">
      <!--  侧边栏  -->
      <div class="manager-main-left">
        <el-menu :default-openeds="['info', 'user']" router style="border: none" :default-active="$route.path">
          <el-menu-item index="/home">
            <i class="el-icon-s-home"></i>
            <span slot="title">系统首页</span>
          </el-menu-item>
          <el-submenu index="info">
            <template slot="title">
              <i class="el-icon-menu"></i><span>信息管理</span>
            </template>
            <el-menu-item index="/category">商品分类</el-menu-item>
            <el-menu-item index="/goods">商品信息</el-menu-item>
            <el-menu-item index="/orders">订单管理信息</el-menu-item>
            <el-menu-item index="/collect">店铺收藏信息</el-menu-item>
            <el-menu-item index="/comment">店铺评论信息</el-menu-item>
            <el-menu-item index="/banner" v-if="user.role==='ADMIN'">广告信息</el-menu-item>
            <el-menu-item index="/notice" v-if="user.role==='ADMIN'">公告信息</el-menu-item>
          </el-submenu>

          <!--          v-if="user.role==='ADMIN' 表示当用户的角色为admin，该表项才会显示；当为商家登录时就看不到用户，商家，管理员等信息-->
          <el-submenu index="user" v-if="user.role==='ADMIN'">
            <template slot="title">
              <i class="el-icon-menu"></i><span>用户管理</span>
            </template>
            <el-menu-item index="/admin">管理员信息</el-menu-item>
            <el-menu-item index="/business">商家信息</el-menu-item>
            <el-menu-item index="/user">用户信息</el-menu-item>
          </el-submenu>
        </el-menu>
      </div>

      <!--  数据表格  -->
      <div class="manager-main-right">
        <!--        @update:user接收xxxPerson页面同步来的数据-->
        <router-view @update:user="updateUser"/>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "Manager",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    }
  },
  created() {
    if (!this.user.id) {
      this.$router.push('/login')
    }
  },
  methods: {
    updateUser() {
      this.user = JSON.parse(localStorage.getItem('xm-user') || '{}')   // 重新获取下用户的最新信息
    },
    goToPerson() {
      if (this.user.role === 'ADMIN') {
        this.$router.push('/adminPerson')
      } else if (this.user.role === 'BUSINESS') {
        this.$router.push('/businessPerson')
      }
    },
    logout() {
      localStorage.removeItem('xm-user')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
@import "@/assets/css/manager.css";

.title {
  margin-left: 20px; /* 向右移动 20px */
}
.enlarge-image {
  width: 45px;
  height: auto; /* 保留图片的原始纵横比 */
}
</style>