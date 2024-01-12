<template>
  <NavBar />
  <router-view/>
</template>

<script>
import NavBar from './components/NavBar.vue';
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap";

export default{//全部在用户浏览器里执行的
  components:{
    NavBar
  }
}
</script>

<style>
body{
  background-image: url("@/assets/images/backgroud.png");
  background-size: cover;
}
</style>
