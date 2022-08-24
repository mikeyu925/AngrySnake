import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '../views/pk/PkIndexView'
import RankListIndexView from '../views/ranklist/RankListIndexView'
import RecordIndexView from '../views/record/RecordIndexView'
import RecordContentView from '../views/record/RecordContentView'
import UserSnakeView from '../views/user/snake/UserSnakeView'
import NotFound from '../views/error/NotFound'
import UserAccountLoginView from '../views/user/account/UserAccountLoginView'
import UserAccountRegisterView from '../views/user/account/UserAccountRegisterView'
import store from '@/store/index'

const routes = [{
        path: "/",
        name: "home",
        redirect: "/pk/", // 重定向，当输入根目录 localhost:8080时自动跳转至 pk 页面
        meta: {
            requestAuth: true,
        }
    },
    {
        path: "/pk/", // 写的是相对域名的相对路径
        name: "pk_index", // 路径的名字
        component: PkIndexView, // 组件
        meta: {
            requestAuth: true,
        }
    }, {
        path: "/ranklist/",
        name: "ranklist_index",
        component: RankListIndexView,
        meta: {
            requestAuth: true,
        }
    },
    {
        path: "/record/",
        name: "record_index",
        component: RecordIndexView,
        meta: {
            requestAuth: true,
        }
    },
    {
        path: "/record/:recordId/",
        name: "record_content",
        component: RecordContentView,
        meta: {
            requestAuth: true,
        }
    },
    {
        path: "/user/snake/",
        name: "user_snake_index",
        component: UserSnakeView,
        meta: {
            requestAuth: true,
        }
    },
    {
        path: "/user/account/login/",
        name: "user_account_login",
        component: UserAccountLoginView,
        meta: {
            requestAuth: false,
        }
    },
    {
        path: "/user/account/register/",
        name: "user_account_register",
        component: UserAccountRegisterView,
        meta: {
            requestAuth: false,
        }
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

router.beforeEach((to, from, next) => { // 在通过router进入某个页面之前执行
    // to:跳转至哪个页面
    // from:从哪个页面跳转的
    // next:要不要执行下一步操作
    if (to.meta.requestAuth && !store.state.user.is_login) {
        next({ name: "user_account_login" }); //跳转至登录页面
    } else {
        next();
    }
})

export default router