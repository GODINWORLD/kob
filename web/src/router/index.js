import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '../views/pk/PkIndexView'
import RecordIndexView from '../views/record/RecordIndexView'
import RanklistIndexView from '../views/ranklist/RanklistIndexView'
import UserBotIndexView from '../views/user/bot/UserBotIndexView'
import NotFound from '../views/error/NotFound'

const routes = [
  {
    path:"/",
    name:"home",
    redirect:"/pk/" //重定向到pk
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PkIndexView, //表示该路由对应的组件
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView,
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
  },
  {
    path: "/404/",
    name: "404",
    component: NotFound,
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

export default router //将router实例导出，以便在其他组件中使用
