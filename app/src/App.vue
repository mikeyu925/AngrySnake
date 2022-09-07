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
import $ from 'jquery'



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
    // {  apply_code 示例
    // "result": "success",
    // "appid": "3235",
    // "scope": "userinfo",
    // "redirect_uri": "https%3A%2F%2Fapp3235.acapp.acwing.com.cn%2Fapi%2Fuser%2Faccount%2Facwing%2Facapp%2Freceive_code%2F",
    // "state": "6107487011"
    // }
    // 获取token信息
    $.ajax({
      url: "https://app3235.acapp.acwing.com.cn/api/user/account/acwing/acapp/apply_code/",
      type: "GET",
      success : resp =>{
        if(resp.result === "success"){
          // 申请授权登录
          const jwt_token = resp.jwt_token;
          store.commit("updateToken", jwt_token);
          store.state.user.AcWingOS.api.oauth2.authorize(resp.appid, resp.redirect_uri, resp.scope, resp.state, resp=>{
            if (resp.result === "success") {
              const jwt_token = resp.jwt_token;
              store.commit("updateToken", jwt_token);
              store.dispatch("getinfo", {
                  success() {
                      store.commit("updatePullingInfo", false);
                  },
                  error() {
                      store.commit("updatePullingInfo", false);
                  }
              })
            } else {
              store.state.user.AcWingOS.api.window.close();
            }
          });
        }else{
          store.state.user.AcWingOS.api.window.close();
        }
      }
    });
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
