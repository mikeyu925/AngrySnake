<template>
    <ContentField v-if="!$store.state.user.pulling_info">
        <div class="row justify-content-md-center">
            <div class="col-9">
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="exampleFormControlInput1" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="exampleFormControlInput1" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="error-message">{{error_message}}</div>
                    <button type="submit" class="btn btn-primary">登录</button>
                </form>
                <div style="text-align : center; margin-top:20px;cursor:pointer;" @click="acwing_login">
                    <img width="30" src="https://cdn.acwing.com/media/article/image/2022/09/06/1_32f001fd2d-acwing_logo.png" alt="">
                    <br>
                    AcWing一键登录
                </div>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'
import {useStore} from 'vuex'
import {ref} from 'vue'
import router from '../../../router/index'
import $ from 'jquery'

export default{
  components: { ContentField },
  setup(){
    const store = useStore();
    let username = ref('');
    let password = ref('');
    let error_message = ref('');

    // 获取token信息
    const jwt_token = localStorage.getItem("jwt_token");
    if(jwt_token){
        store.commit("updateToken",jwt_token);
        // 从云端获取信息
        store.dispatch("getinfo",{
            success(){
                // store.commit("updatePullingInfo",true);
                router.push({name:"home"});
                store.commit("updatePullingInfo",false);
            },
            error(){
                store.commit("updatePullingInfo",false);
            }
        })
    }else{
        store.commit("updatePullingInfo",false);
    }

    // 登录
    const login = () => {
        error_message.value = "";
        store.dispatch("login",{
            username : username.value,
            password : password.value,
            success(){
                store.dispatch("getinfo",{
                    success(){
                        // 登录成功 跳转至 home 页面
                        router.push({name : 'home'});
                        console.log(store.state.user);
                    }
                })

            },
            error(){
                error_message.value = "用户名或密码错误!";
            },
        })
    }

    // Acwing第三方登录
    const acwing_login = () =>{
        $.ajax({
            url : "https://app3235.acapp.acwing.com.cn/api/user/account/acwing/web/apply_code/",
            type: "GET",
            success: resp => {
                if (resp.result === "success") {
                    window.location.replace(resp.apply_code_url);
                }
            }
        });
    }

    return{
        username,
        password,
        error_message,
        login,
        acwing_login,
    }
    
  }
}
</script>

<style scoped>
    button{
        width: 100%;
    }
    div.error-message{
        color: red;
    }
</style>