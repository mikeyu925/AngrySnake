// 将用户信息存储在全局信息中
import $ from 'jquery'
export default {
    state: {
        AcWingOS: "AcWingOS",
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
        // 是否正在拉取信息 默认是 true
        pulling_info: true,
    },
    getters: {},
    // mutations 通常用来修改数据  采用commit调用
    mutations: {
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token) {
            state.token = token;
        },
        // 退出登录
        logout(state) {
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        },
        updatePullingInfo(state, pulling_info) {
            state.pulling_info = pulling_info;
        },
    },
    actions: {
        // 采用dispatch调用  从云端拉取信息，异步操作
        login(context, data) {
            $.ajax({
                // url: "http://127.0.0.1:6969/api/user/account/token/",
                url: "https://app3235.acapp.acwing.com.cn/api/user/account/token/",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password,
                },
                success(resp) {
                    if (resp.error_message === 'success') {
                        //将token存放至浏览器的localStorage
                        localStorage.setItem("jwt_token", resp.token);
                        context.commit("updateToken", resp.token);
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                }
            });
        },
        getinfo(context, data) {
            $.ajax({
                // url: "http://127.0.0.1:6969/api/user/account/info/",
                url: "https://app3235.acapp.acwing.com.cn/api/user/account/info/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + context.state.token,
                },
                success(resp) {
                    if (resp.error_message === 'success') {
                        // 如果成功 解析并更新用户信息
                        context.commit("updateUser", {
                            ...resp,
                            is_login: true,
                        });
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                }
            });
        },
        logout(context) {
            localStorage.removeItem("jwt_token")
            context.commit("logout");
        }
    },
    modules: {}
}