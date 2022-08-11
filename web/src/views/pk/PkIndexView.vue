<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'" />

</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue'
import {onMounted,onUnmounted} from 'vue'
import {useStore} from 'vuex'

export default{
    components:{
        PlayGround,
        MatchGround,
    },
    setup() {
        const store = useStore();
        const socketUrl = `ws://127.0.0.1:6969/websocket/${store.state.user.token}/`;
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
                console.log("connected !!!");
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
                    },2000); //2秒后跳转至pk游戏界面
                    
                    store.commit("updateGameMap",data.gamemap);
                }
                 console.log(data);
            }
            // onclose 链接关闭时执行
            socket.onclose = () =>{
                console.log("disconnected !!!");
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

</style>
