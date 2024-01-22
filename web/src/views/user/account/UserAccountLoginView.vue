<template>
    <!-- 直接写div.row>div.col-3，然后tab，会自动补全-->

    <ContentField v-if="! $store.state.user.pulling_info">
        <div class="row justify-content-md-center">
            <div class="col-3">

                <!-- 阻止默认的表单提交行为，从而使我们可以自定义表单的提交逻辑 -->
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>

                        <!-- 这里的type是password，输入密码后会变成圆点 -->
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="error-message">{{error_message}}</div>
                    <!-- 这里的type是submit -->
                    <button type="submit" class="btn btn-primary">提交</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue';
import { useStore } from 'vuex';
import { ref } from 'vue';
import router from '@/router/index';

export default{
    components:{
        ContentField
    },
    setup(){
        const store = useStore();
        let username = ref('');
        let password = ref('');
        let error_message = ref('');

        //let show_content = ref(false);//一开始默认是不展示登陆界面，避免一闪

        const jwt_token = localStorage.getItem("jwt_token");
        if (jwt_token){
            store.commit("updateToken", jwt_token);

            //将 success 方法写在组件内部，可以更好地控制回调函数的逻辑，例如页面跳转、状态更新等。
            // 同时，也可以将该回调函数作为参数传递给其他组件，实现更好的代码复用性。
            //如果直接将 success 方法写在 store 内部，会使得 store 的逻辑变得复杂，同时也限制了回调函数的灵活性和可复用性。
            store.dispatch("getinfo", { //如果可以从服务器获取信息，说明token是有效的
                success(){
                    router.push({name : "home"});
                    store.commit("updatePullingInfo", false);
                },
                error(){
                    store.commit("updatePullingInfo", false);
                }
            })
        }
        else {
            store.commit("updatePullingInfo", false);
        }
        
        //注意前面如果localStorage里有token的话，直接跳转到Home界面了，这个login函数根本不会触发
        //所以在NavBar.vue里，会先执行v-if="is_login"，显示第一个导航栏的样式
        //虽然v-else-if里的条件也会满足，但是执行不到
        const login = () => {//login和getinfo是绑定在一起的
            error_message.value = "";//每次要清空上一次的错误提示信息
            store.dispatch("login",{//这里将{}里所有东西封装起来传过去，理解为一个对象吧
                username: username.value,
                password: password.value,
                success(){
                    store.dispatch("getinfo",{
                        success(){
                            router.push({name: 'home'});//成功则自动跳到home页面
                        }
                    })
                },
                error(){
                    error_message.value = "用户名或密码错误";
                }
            })
        }

        return {
            username,
            password,
            error_message,
            login,
        }
    }
}
</script>

<style scoped>
button {
    width: 100%;
}
div.error-message {
    color: red;
}

</style>