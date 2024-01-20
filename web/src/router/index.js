import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '../views/pk/PkIndexView'
import RecordIndexView from '../views/record/RecordIndexView'
import RanklistIndexView from '../views/ranklist/RanklistIndexView'
import UserBotIndexView from '../views/user/bot/UserBotIndexView'
import NotFound from '../views/error/NotFound'
import UserAccountLoginView from '../views/user/account/UserAccountLoginView'
import UserAccountRegisterView from '../views/user/account/UserAccountRegisterView'
import store from '@/store/index'

const routes = [
  {
    path:"/",
    name:"home",
    redirect:"/pk/", //重定向到pk
    meta:{//名字随意
      requestAuth: true,//是否需要授权
    }
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PkIndexView, //表示该路由对应的组件
    meta:{
      requestAuth:true,
    }
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
    meta:{
      requestAuth:true,
    }
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView,
    meta:{
      requestAuth:true,
    }
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
    meta:{
      requestAuth:true,
    }
  },

  //登陆和注册
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta:{
      requestAuth:false,
    }
  },
  {
    path: "/user/account/register",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta:{
      requestAuth:false,
    }
  },

  {
    path: "/404/",
    name: "404",
    component: NotFound,
    meta:{
      requestAuth:false,
    }
  },
  {
    path:"/:catchAll(.*)",
    redirect:"/404/"
  }

]

const router = createRouter({
  history: createWebHistory(),
  routes
})

//通过router进入某个页面之前，会先调用这个函数
router.beforeEach((to, from, next) =>{//to是跳转到哪个页面，from是从哪个页面来的， next是否要下一步
  if (to.meta.requestAuth && !store.state.user.is_login){
    next({name: "user_account_login"});
  }
  else {
    next();
  }
})

export default router //将router实例导出，以便在其他组件中使用
