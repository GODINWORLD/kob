<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="register">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="mb-3">
                        <label for="confirmedPassword" class="form-label">确认密码</label>
                        <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword" placeholder="请再次输入密码">
                    </div>

                    <div class="error-message">{{error_message}}</div>
                    <button type="submit" class="btn btn-primary">提交</button> <!-- 这里是type="submit"-->
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue';
import { ref } from 'vue';
import router from '@/router/index';
import $ from 'jquery'

export default{
    components:{
        ContentField
    },
    setup(){
        let username = ref('');
        let password = ref('');
        let confirmedPassword = ref('');
        let error_message = ref('');
        
        //变量不会重新赋值用const
        //这个ajax不放到store里，是因为 有可能修改state的值时，才放到那里，而注册不会修改state的任何值
        //注册完后 要登陆才更新state的值
        const register = () => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/account/register/",
                type: "post", //一般只是获取数据库数据，用get，需要修改数据库，用post
                data:{
                    username: username.value,
                    password: password.value,
                    confirmedPassword: confirmedPassword.value,
                },
                //完整是"success" : function(resp){}
                //字典可以省略"", 如果关键字对应的值是一个函数，那么可以简写

                success(resp){
                    if (resp.error_message === "success"){
                        router.push({name: "user_account_login"});//让用户去登陆
                    }else{
                        error_message.value = resp.error_message;
                    }
                },
            
            });
        }

        return{
            username,
            password,
            confirmedPassword,
            error_message,
            register,
        }
    }
}
</script>

<style scoped>
button {
    width: 100%;
}

div.error-message{
    color: red;
}
</style>