import { createStore } from 'vuex' //将user放到全局
import ModuleUser from './user' //这里随便起名

export default createStore({
  state: {
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user : ModuleUser,
  }
})
