// 将链接信息存储在全局信息中
export default {
    state: {
        router_name: "menu", // menu, pk, record,record_content,ranklist,user_bot
    },
    getters: {},
    mutations: {
        updateRouterName(state, router_name) {
            state.router_name = router_name;
        },
    },
    actions: {},
    modules: {}
}