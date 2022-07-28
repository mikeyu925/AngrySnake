import { createStore } from 'vuex'

import ModuleUser from './user'

export default createStore({
    state: {},
    getters: {},
    mutations: {},
    actions: {},
    // 将user加入全局module
    modules: {
        user: ModuleUser,
    }
})