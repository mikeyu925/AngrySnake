import { createStore } from 'vuex'
import ModuleUser from './user'
import ModulePk from './pk'
import ModuleRecord from './record'

export default createStore({
    state: {},
    getters: {},
    mutations: {},
    actions: {},
    // 将user加入全局module
    modules: {
        user: ModuleUser,
        pk: ModulePk,
        record: ModuleRecord,
    }
})