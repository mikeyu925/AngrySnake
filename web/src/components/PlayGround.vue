// 游戏区域 PlayGround
<template>
    <div class="playground">
        <GameMap />
    </div>
    
</template>

<script>
import GameMap from './GameMap.vue'

export default {
    components:{
        GameMap,
    }
}
</script>


<style scoped>
    div.playground{
        /* 游戏区域为屏幕宽度的 60% 高度的 70% */
        width: 60vw; 
        height: 70vh;
        margin: 40px auto;
        /* background: lightskyblue; */
    }
</style>

