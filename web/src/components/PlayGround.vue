<!-- 指的是包裹地图的外部区域，对战页面-->
<template>
    <div class="playground">
        <GameMap />

    </div>
</template>

<script>
import GameMap from './GameMap.vue';

export default{
    components:{
        GameMap,
    }
}
</script>

<style scoped>
div.playground{
    width: 60vw; /*将 playground 的宽度设置为视口宽度（viewport width）的60%。
    vh 表示视口高度（viewport height），vw 表示视口宽度（viewport width），是一种相对于视口的单位，可以用于响应式设计。*/
    height: 70vh;
    margin: 40px auto;/*将 playground 的上下外边距设置为40px，左右外边距设置为自动，使其水平居中。*/
}

</style>