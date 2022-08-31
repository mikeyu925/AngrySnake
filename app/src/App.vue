<template>
  <!-- <div class="window"> -->
    <div class="game-body">
      <MenuView  v-if="$store.state.router.router_name === 'menu'"/>
      <PKIndexView v-else-if="$store.state.router.router_name === 'pk'"/>
      <RecordIndexView v-else-if="$store.state.router.router_name === 'record'"/>
      <RecordContentView v-else-if="$store.state.router.router_name === 'record_content'"/>
      <RankListIndexView v-else-if="$store.state.router.router_name === 'ranklist'"/>
      <UserSnakeView v-else-if="$store.state.router.router_name === 'user_bot'"/>
    </div>
  <!-- </div> -->
</template>


<script>
import {useStore} from 'vuex'
import MenuView from './views/MenuView.vue'
import PKIndexView from './views/pk/PkIndexView.vue'
import RecordIndexView from './views/record/RecordIndexView.vue'
import RecordContentView from './views/record/RecordContentView.vue'
import RankListIndexView from './views/ranklist/RankListIndexView.vue'
import UserSnakeView from './views/user/snake/UserSnakeView.vue'

// js
export default{
  components:{
    MenuView,
    PKIndexView,
    RecordIndexView,
    RecordContentView,
    RankListIndexView,
    UserSnakeView,
  },
  setup(){
    const store = useStore();
    // 获取token信息
    // const jwt_token = localStorage.getItem("jwt_token");
    const jwt_token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhYjBkZDYwM2NkMjM0NDM4YjVhOWZmNDFlMWY3MGQ5MiIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTY2MTgzODYyOSwiZXhwIjoxNjYzMDQ4MjI5fQ.JeEUoDfMsB5T8KbfDS1UfYVqzURZfXFtjo9J0Jk7Kos";
    if(jwt_token){
        store.commit("updateToken",jwt_token);
        // 从云端获取信息
        store.dispatch("getinfo",{
            success(){
                store.commit("updatePullingInfo",false);
            },
            error(){
                store.commit("updatePullingInfo",false);
            }
        })
    }else{
        store.commit("updatePullingInfo",false);
    }
  }
}
</script>


<style scoped>
  body{
    margin: 0;
  }
  /* 写css */
  div.game-body {
    /* 页面背景 */
    background-image: url("@/assets/images/background.jpg");
    background-size: cover;
    width: 100%;
    height: 100%;
  }
  div.window{
    width: 100vw;
    height: 100vh;
  }
</style>
