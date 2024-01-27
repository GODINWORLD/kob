<!-- 指的是那个13*14的区域-->
<template>
    <div ref="parent" class="gamemap">
        <canvas ref="canvas" tabindex="0"> <!-- 聚焦于canvas，可以输入用户操作了-->

        </canvas>
    </div>
</template>

<script>
import {GameMap} from '@/assets/scripts/GameMap';
import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';
//在setup函数中使用ref函数创建了一个响应式对象，那么在组件的template部分
//就可以通过ref="parent"将该响应式对象与相应的 HTML 元素或组件关联起来。



export default{
    setup(){
        const store = useStore();
        let parent = ref(null);
        let canvas = ref(null);

        onMounted(() => {//如果不使用 onMounted()，那么就无法保证回调函数在组件挂载完成后才被执行。
            store.commit("updateGameObject",
                new GameMap(canvas.value.getContext('2d'), parent.value, store)
                //用来获取 <canvas> 元素的 2D 渲染上下文对象的方法。
            );
        });

        return{
            parent,
            canvas
        }

    }
}

</script>

<style scoped>
div.gamemap{
    width: 100%; /* 设置为父容器的100%,也就是playground*/
    height: 100%;
    display: flex; 
    justify-content: center;
    align-content: center; /* 竖直居中*/
}
</style>