<template>
    <div class="info-board">

        <div class="snake-info">
            Snake Info
        </div>


        <div class="snake1" v-if="$store.state.pk.status ==='playing' && parseInt($store.state.user.id) === parseInt($store.state.pk.a_id)">
            <div class="snake-description">
                自己 : {{$store.state.user.username}}
            </div>
            <div class="snake-photo">
                <img src="@/assets/images/snake1.jpg" alt="">
            </div>
            <div class="snake-description">
                对手 : {{$store.state.pk.opponent_username}}
                
            </div>
            <div class="snake-photo">
                <img src="@/assets/images/snake2.jpg" alt="">
            </div>
        </div>

        <div class="snake2" v-if="$store.state.pk.status ==='playing' && parseInt($store.state.user.id) === parseInt($store.state.pk.b_id)">
            <div class="snake-description">
                自己 : {{$store.state.user.username}}
            </div>
            <div class="snake-photo">
                <img src="@/assets/images/snake2.jpg" alt="">
            </div>
            <div class="snake-description">
                对手 : {{$store.state.pk.opponent_username}}
            </div>
            <div class="snake-photo">
                <img src="@/assets/images/snake1.jpg" alt="">
            </div>
            

        </div>

    </div>
</template>

<script>
    export default{
        setup(){
    
        }
    }

</script>


<style scoped>
    div.info-board{
        height: 45vh;
        width: 14vw;
        background-color: wheat;
        position: absolute;
        top:14vh;
        left:12vw;
        border-radius: 5%;
    }

    div.snake-info{
        /* 居中 */
        text-align: center; 
        color: rgb(203, 74, 18);
        font-size: 40px;
        font-weight: 600;
        font-style: italic;
        padding-top: 2vh;

    }
    
    div.snake-description{
        color: rgb(10, 10, 10);
        font-size: 25px;
        font-weight: 560;
        padding-top: 6vh;
        padding-left: 1vw;
    }
    div.snake-photo {
        text-align: center;
    }
    div.snake-photo > img{
        border-radius: 50%;
    }

</style>
