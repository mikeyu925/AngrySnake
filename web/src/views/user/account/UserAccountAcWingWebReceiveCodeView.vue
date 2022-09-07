<template>
    <div>

    </div>
</template>

<script>
import router from '@/router/index'
import { useRoute } from 'vue-router';
import { useStore } from 'vuex';
import $ from 'jquery'

export default {
    setup() {
        const myRoute = useRoute();
        const store = useStore();

        $.ajax({
            url: "https://app3235.acapp.acwing.com.cn/api/user/account/acwing/web/receive_code/",
            type: "GET",
            data: {
                code: myRoute.query.code,
                state: myRoute.query.state,
            },
            success: resp => {
                if (resp.result === "success") {
                    localStorage.setItem("jwt_token", resp.jwt_token); // 存至本地
                    store.commit("updateToken", resp.jwt_token); // 存至内存中
                    router.push({ name: "home" }); // 登录成功跳转至home页面
                    store.commit("updatePullingInfo", false);
                } else {
                    router.push({ name: "user_account_login" }); // 否则跳转至登录页面
                }
            }
        })
    }
}
</script>


<style scoped>

</style>
