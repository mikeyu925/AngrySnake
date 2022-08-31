// 游戏地图
<template>
    <div ref="parent" class="gamemap">
        <!--canvas画布？ tabindex用于获取用户输入 -->
        <canvas ref="canvas" tabindex="0"></canvas>
    </div>
</template>



<script>
import { GameMap } from "@/assets/scripts/GameMap";
import { ref, onMounted } from 'vue'
import { useStore } from "vuex";

export default {
    setup() {
        const store = useStore();
        let parent = ref(null);
        let canvas = ref(null);

        onMounted(() => {
            store.commit(
                "updateGameObject",
                new GameMap(canvas.value.getContext('2d'), parent.value, store)
            );
        });

        return {
            parent,
            canvas
        }
    }
}
</script>


<style scoped>
    div.gamemap{
        /* 设置和父元素(即游戏区域)等长 */
        width: 100%;
        height: 100%;
        display: flex;
        /* 水平居中 */
        justify-content: center;
        /* 竖直方向居中 */
        align-items: center; 
    }
</style>
