<template>
    <div class="matchground-field">
        <div class="matchground">
            <div class="matchground-head">

                <div>
                    <div class="user-photo">
                        <img :src="$store.state.user.photo" alt="">
                    </div>
                    <div class="user-username">
                        {{$store.state.user.username}}
                    </div>
                </div>

                <div class="vs">
                    vs
                </div>

                <div>
                    <div class="user-photo">
                        <img :src="$store.state.pk.opponent_photo" alt="">
                    </div>
                    <div class="user-username">
                        {{$store.state.pk.opponent_username}}
                    </div>
                </div>

            </div>


            <div class="user-select-bot">
                <select v-model="select_bot" class="form-select" aria-label="Default select example">
                    <option value="-1">亲自出马</option>
                    <option v-for="snake in snakes" :key="snake.id" :value="snake.id">
                        {{snake.title}}
                    </option>
                </select>
            </div>


            <div class="start-match-button" style=" text-align: center; padding-top: 10vh">
                <button @click="click_match_btn" type="button">
                    {{match_btn_info}}
                </button>
            </div>

        </div>
    </div>
    
    
</template>

<script>
import { ref } from 'vue'
import {useStore} from 'vuex'
import $ from 'jquery'

export default {
    setup(){
        const store = useStore();
        let match_btn_info = ref("开始匹配");
        let snakes = ref([]);
        let select_bot = ref("-1");

        const click_match_btn = () =>{
            if(match_btn_info.value === "开始匹配"){
                match_btn_info.value = "取消匹配";
                // 向后端发送匹配请求 将JSON格式转换为字符串发送
                store.state.pk.socket.send(JSON.stringify({
                    event:"start-matching",
                    bot_id : select_bot.value,
                }));
            }else{
                match_btn_info.value = "开始匹配";
                // 向后端发送取消匹配请求
                store.state.pk.socket.send(JSON.stringify({
                    event:"stop-matching",
                }));
            }
        }

        const refresh_snakes = () =>{
        $.ajax({
        //   url: "http://127.0.0.1:6969/api/user/snake/getlist/",
          url: "https://app3235.acapp.acwing.com.cn/api/user/snake/getlist/",
          type: "get",
          headers: {
              Authorization: "Bearer " + store.state.user.token, // 验证的 token
          },
          success(resp) {
            snakes.value = resp;
          }
        })
      }
      
      refresh_snakes(); // 从云端动态获取snake

        return{
            match_btn_info,
            snakes,
            select_bot,
            click_match_btn,
        }
    }
}
</script>


<style scoped>
    div.matchground-field{
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 100%;
    }

    div.matchground-head{
        display: flex;
        /* 水平均匀分布 */
        justify-content: space-evenly; 

    }

    div.matchground{
        /* 匹配区域为屏幕宽度的 60% 高度的 70% */
        width: 60%; 
        height: 70%;
        background: rgba(50, 50, 50, 0.5);
        display: flex;
        flex-direction: column;
        justify-content: space-around;
    }

    div.vs{
        text-align: center; 
        color: rgb(223, 65, 12);
        font-size: 80px;
        font-weight: 600;
        font-style: italic;
        padding-top: 10vh;
    }

    div.user-photo{
        text-align: center;
        padding-top: 12vh;
    }

    div.user-photo > img {
        border-radius: 50%;
        width: 10vh;
    }

    div.user-username{
        text-align: center;
        font-size: 20px;
        font-weight: 600;
        color: aliceblue;
        padding-top: 2vh;
    }

    div.user-select-bot{
        /* padding-top: 1vh; */
        display: flex;
        justify-content: center;
        align-items: center;
        height: 20vh;
        text-align: center;
    }
    
    div.user-select-bot > select{
        width:25%;
        margin:0 auto;
        font-size: 20px;
        border-radius: 5px;
    }

    .start-match-button{
        text-align: center;
        
    }

    .start-match-button > button{
        font-size: 25px;
        border-radius: 5px;
        background-color: #FFC310;
        padding: 5px 10px;
        border: none;
        cursor: pointer;
    }

    .start-match-button > button:hover{
        transform: scale(1.1);
        transition: 100ms;
    }

</style>

