import { createStore } from 'vuex' //将user放到全局
import ModuleUser from './user' //这里随便起名

export default createStore({
  state: {
       //这里放全局参数
  },
  getters: {
    //这里是get方法
  },
  mutations: {
    //这里是set方法
  },
  actions: {

  },
  modules: {
    user : ModuleUser,
    //这里是我自己理解的是为了给全局变量 #分组#，所以需要写提前声明其他store文件，然后引入这里
  }
})
