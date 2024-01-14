<template>
    <nav class="navbar navbar-expand-lg bg-body-tertiary" >
        <div class="container-fluid">
          <router-link class="navbar-brand" :to="{name: 'home'}">King Of Bots</router-link>
          <div class="collapse navbar-collapse" id="navbarText">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
              <li class="nav-item">
                <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'pk_index'}">对战</router-link>
              </li>
              <!-- router-link 组件来定义页面链接，to 属性指定了链接的目标路由， :是v-bind:的省略写法-->

              <li class="nav-item">
                <router-link :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'record_index'}">对局列表</router-link>
              </li>
              <li class="nav-item">
                <router-link :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'ranklist_index'}">排行榜</router-link>
              </li>
            </ul>
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                      lwx
                    </a>
                    <ul class="dropdown-menu">
                      <li>
                        <router-link class="dropdown-item" :to="{name: 'user_bot_index'}">我的Bot</router-link>   
                      </li>
                      <li><hr class="dropdown-divider"></li>
                      <li><a class="dropdown-item" href="#">退出</a></li>
                    </ul>
                  </li>
          
              </ul>
          </div>
        </div>
      </nav>
</template>

<script>
import { useRoute } from 'vue-router';
import { computed } from 'vue';

export default{
    setup(){
        const route = useRoute();//获取当前路由对象
        let route_name = computed(() => route.name); //动态计算
        return {
            route_name
        }
    }
}

</script>

<style scoped>

</style>