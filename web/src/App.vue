<template>
  <navbar />
  <!-- router-view 可以自动根据网址来变化，在router文件夹下的index.js定义 -->
  <router-view/>
</template>


<script>
import Navbar from './components/NavBar.vue'
// 导入bootstrap的依赖
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
// js
export default{
  components:{
    Navbar
  }
}
</script>


<style>
/* 写css */
body {
  /* 页面背景 */
  background-image: url("@/assets/background.jpg");
  background-size: cover;
}
</style>
