import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '../views/pk/PkIndexView'
import RankListIndexView from '../views/ranklist/RankListIndexView'
import RecordIndexView from '../views/record/RecordIndexView'
import UserSnakeView from '../views/user/snake/UserSnakeView'
import NotFound from '../views/error/NotFound'
import UserAccountLoginView from '../views/user/account/UserAccountLoginView'
import UserAccountRegisterView from '../views/user/account/UserAccountRegisterView'


const routes = [{
        path: "/",
        name: "home",
        redirect: "/pk/" // 重定向，当输入根目录 localhost:8080时自动跳转至 pk 页面
    },
    {
        path: "/pk/", // 写的是相对域名的相对路径
        name: "pk_index", // 路径的名字
        component: PkIndexView // 组件
    }, {
        path: "/ranklist/",
        name: "ranklist_index",
        component: RankListIndexView
    },
    {
        path: "/record/",
        name: "record_index",
        component: RecordIndexView
    },
    {
        path: "/user/snake/",
        name: "user_snake_index",
        component: UserSnakeView
    },
    {
        path: "/user/account/login/",
        name: "user_account_login",
        component: UserAccountLoginView
    },
    {
        path: "/user/account/register/",
        name: "user_account_register",
        component: UserAccountRegisterView
    },
    {
        path: "/404/",
        name: "404",
        component: NotFound
    },
    {
        path: "/:catchAll(.*)", // 如果输入的是其他网址，则重定向404
        redirect: "/404/"
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router