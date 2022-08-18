// 每个组件文件名必须有两个大写
// 导航栏 组件BavBar
<template>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container"> 
    <!-- <a class="navbar-brand" href="/">Angry Snake</a> -->
    <!-- 将<a> 标签替换为 <router-link> 可以防止页面刷新（单页面应用） -->
    <router-link class="navbar-brand" :to="{name:'home'}">Angry Snake</router-link>
    <div class="collapse navbar-collapse" id="navbarText">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <!-- <a class="nav-link" aria-current="page" href="/pk/">对战</a> -->
          <!-- active 用于设置聚焦 -->
          <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" :to="{name:'pk_index'}">对战 </router-link>
        </li>
        <li class="nav-item">
          <router-link :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'" :to="{name:'record_index'}">对局列表 </router-link>
        </li>
        <li class="nav-item">
          <router-link :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'" :to="{name:'ranklist_index'}">排行榜 </router-link>
        </li>
      </ul>

      <!-- <ul>
        <audio ref="audio" src="https://img.youpenglai.com/penglai/1.mp3" loop preload="auto"></audio>
      </ul> -->

      <ul class="navbar-nav" v-if="$store.state.user.is_login">
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            {{$store.state.user.username}}
          </a>
          <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
            <li>
              <router-link class="dropdown-item" :to="{name:'user_snake_index'}">我的 snake</router-link>
            </li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#" @click="logout">退出</a></li>
          </ul>
        </li>
      </ul>

      <ul class="navbar-nav" v-else-if="!$store.state.user.pulling_info">
        <li class="nav-item ">
          <router-link class="nav-link " :to="{name:'user_account_login'}" role="button" >
            登录
          </router-link>
        </li>
        <li class="nav-item ">
          <router-link class="nav-link " :to="{name:'user_account_register'}" role="button" >
            注册
          </router-link>
        </li>
      </ul>

    </div>
  </div>
</nav>
</template>


<script>
// useRoute 
import {useRoute} from 'vue-router'
// computed 实时计算
import {computed} from 'vue'
import {useStore} from 'vuex'
export default{
  setup(){
    const store = useStore();
    const route = useRoute();
    let route_name = computed(() => route.name)

    const logout = () =>{
      store.dispatch("logout");
    }

    return{
      route_name,
      logout
    } 
  }
}

</script>


<style scoped>
/* scoped 作用可以使得css加上一个随机字符串，不会影响其他组件 */


</style>
