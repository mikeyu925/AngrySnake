<template>
    <ContentField>
        <PlayGround v-if="$store.state.pk.status === 'playing'" />
        <!-- <SnakeinfoBoard v-if="$store.state.pk.status === 'playing'" /> -->
        <MatchGround v-if="$store.state.pk.status === 'matching'" />
        <ResultBoard v-if="$store.state.pk.loser != 'none'"/>
        <div class="user-color" v-if="$store.state.pk.status === 'playing' && parseInt($store.state.user.id) === parseInt($store.state.pk.a_id)">左下角</div>
        <div class="user-color" v-if="$store.state.pk.status === 'playing' && parseInt($store.state.user.id) === parseInt($store.state.pk.b_id)">右上角</div>
    </ContentField>
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue'
import ResultBoard from '../../components/ResultBoard.vue'
// import SnakeinfoBoard from '../../components/SnakeinfoBoard.vue'
import ContentField from '../../components/ContentField.vue'
import {onMounted,onUnmounted} from 'vue'
import {useStore} from 'vuex'



export default{
    components:{
        PlayGround,
        MatchGround,
        ResultBoard,
        // SnakeinfoBoard,
        ContentField,
    },
    setup() {
        const store = useStore();
        const socketUrl = `wss://app3235.acapp.acwing.com.cn/websocket/${store.state.user.token}/`;
        store.commit("updateLoser","none");
        store.commit("updateIsRecord",false);


        let socket = null;
        // 组件被挂载时执行 onMounted (组件被挂载 == 页面被打开？)
        onMounted(() => {
            store.commit("updateOpponent",{
                username:"不知名选手",
                photo:"https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
            })

            socket = new WebSocket(socketUrl);
            // onopen 链接成功建立时执行
            socket.onopen = () =>{
                // console.log("connected !!!");
                store.commit("updateSocket",socket);
            }

            // onmessage 接收到信息时执行
            socket.onmessage = msg => {
                const data = JSON.parse(msg.data);
                if(data.event === "start-matching"){ // 匹配成功
                    store.commit("updateOpponent",{
                        username:data.opponent_username,
                        photo:data.opponent_photo,
                    });
                    setTimeout(()=>{
                        store.commit("updateStatus","playing");  // 修改状态
                    },200); //0.2秒后跳转至pk游戏界面
                    
                    store.commit("updateGame",data.game);
                }else if(data.event === "move"){
                    // console.log(data);
                    const game = store.state.pk.gameObject;
                    console.log(game);
                    const [snake0,snake1] = game.snakes; // 为什么是错误的(null) ?
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                }else if(data.event === "result"){
                    // console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0,snake1] = game.snakes;
                    if(data.loser === "all" || data.loser === "A"){
                        snake0.status = "die";
                    }
                    if(data.loser === "all" || data.loser === "B"){
                        snake1.status = "die";
                    }

                    store.commit("updateLoser",data.loser);
                }
                //  console.log(data);
            }
            // onclose 链接关闭时执行
            socket.onclose = () =>{
            }
        });
        // 组件被解除挂载时执行 onUnmounted (组件被解除挂载 == 页面被关闭？)
        onUnmounted(() => {
            // 断开连接  不然每次打开都会再建立一个新的连接 产生冗余
            socket.close();
            store.commit("updateStatus","matching");  // 修改状态
        });
        
    }
}

</script>

<style scoped>
div.user-color{
    text-align: center;
    color:white;
    font-size: 30px;
    font-weight: 600;
}
div.user-color{
    position: absolute;
    bottom: 5vh;
    width: 100%;
    text-align: center;
}
</style>
