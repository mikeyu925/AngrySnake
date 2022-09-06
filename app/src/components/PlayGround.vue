<template>
    <div class="content-field">
        <div class="playground">
            <GameMap />
        </div>
        <div class="go-back" @click="click_goback_handler" v-if="$store.state.record.is_record">
            返回对局列表
        </div>
    </div>
</template>

<script>
import GameMap from './GameMap.vue'
import {useStore} from 'vuex';
export default {
    components:{
        GameMap,
    },
    setup(){
        const store = useStore();
        
        const click_goback_handler = () =>{
            store.commit("updateRouterName","record");
        }
        return {
            click_goback_handler,
        }
    }

}
</script>

<style scoped>
    div.content-field{
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 100%;
        
    }
    div.playground{
        width: 40vw; 
        height: 50vh;
    }

    div.go-back{
        position: absolute;
        left: 5vh;
        bottom: 5vh;
        color: white;
        font-size: 30px;
        font-style: italic;
        font-weight: 600;
        cursor: pointer;
        user-select: none;
    }
    div.go-back:hover{
        transform: scale(1.2);
        transition: 200ms;
    }
</style>

