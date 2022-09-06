<template>
    <div class="result-board">
        <div class="result-board-text" v-if="$store.state.pk.loser === 'all'">
            Draw! *_*
        </div>
        <div class="result-board-text" v-else-if="$store.state.pk.loser === 'A' && $store.state.pk.a_id == $store.state.user.id">
            Lose T_T
        </div>
        <div class="result-board-text" v-else-if="$store.state.pk.loser === 'B' && $store.state.pk.b_id === parseInt($store.state.user.id)">
            Lose T_T
        </div>
        <div class="result-board-text" v-else>
            Win! ^_^
        </div>

        <div class="result-board-btn">
            <button @click="restart" type="button" class="btn btn-info btn-lg">
                再来一局
            </button>
        </div>

    </div>
</template>

<script>
import {useStore} from 'vuex'

export default{
    setup(){
        const store = useStore();

        const restart = () =>{
            store.commit("updateStatus","matching");
            store.commit("updateLoser","none");
            store.commit("updateOpponent",{
                username:"不知名选手",
                photo:"https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
            })
        }

        return {
            restart,
        };
    }
}
</script>


<style scoped>
    div.result-board{
        height: 30vh;
        width: 30vw;
        background-color: rgba(50,50,50,0.5);
        position: absolute;
        top:0;
        bottom: 0;
        left:0;
        right: 0;
        margin: auto;
    }

    div.result-board-text{
        /* 居中 */
        text-align: center; 
        color: wheat;
        font-size: 60px;
        font-weight: 600;
        font-style: italic;
        padding-top: 4vh;

    }

    div.result-board-btn{
        padding-top: 4vh;
        text-align: center; 
    }
</style>
