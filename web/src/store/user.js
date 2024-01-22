import $ from 'jquery'

//存到store的东西就是存到内存里，一刷新就清空了

export default{
    state: {
        id: "",
        username: "", //js里，字典的关键字可以括起来"username"，也可以不括
        photo: "",
        token: "",//令牌
        is_login: false, 
        pulling_info: true, //表示当前是不是在获取信息中，if true，不要展示登陆界面
        //一开始是true，然后根据路由，会调用UserAccountLoginView，在那里的setup函数里，由于jwt_token为空
        //所以会将pulling_info置为false
    },
    getters: {

    },
    mutations: {//同步、setter操作放mutations
        updateUser(state, user){//第一个参数默认是state，调用时不需要传入其他参数
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token){
            state.token = token;
        },
        logout(state){//由于是jwt验证，重要的是令牌，退出时重置令牌即可
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        },
        updatePullingInfo(state, pulling_info){
            state.pulling_info = pulling_info;
        }
    },

    //异步操作放actions里，比如这里要从云端拉取数据才能继续
    actions: {//一般来说，修改state的函数都可以写在actions里

        login(context, data){
            $.ajax({//发送http请求
                url: "http://127.0.0.1:3000/user/account/token/",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password,
                },
                success(resp){
                    if (resp.error_message === "success"){
                        localStorage.setItem("jwt_token", resp.token);//放到浏览器的本地内存空间

                        context.commit("updateToken", resp.token); //在ajax里调用mutations里的函数用commit+参数
                        data.success(resp); //成功了要调用
                    }
                    else data.error(resp);//这个参数传进去貌似没用
                },
                error(resp){
                    data.error(resp);
                }
            });
        },
        getinfo(context, data){
            $.ajax({
                url: "http://127.0.0.1:3000/user/account/info/",
                type: "get",
                headers:{
                    Authorization: "Bearer " + context.state.token,
                },
                success(resp){
                    if(resp.error_message === "success"){
                        context.commit("updateUser", {
                            ...resp, //...是把resp的信息解构出来，放到当前对象里
                            is_login: true,//注意后端返回时是没有is_login这个参数的
                        });
                        data.success(resp);
                    }
                    else {
                        data.error(resp);//调用data这个对象的error方法
                    }
                },
                error(resp){
                    data.error(resp);
                }
            })
        },
        logout(context){
            localStorage.removeItem("jwt_token");//直接去掉localStorage的token即可

            context.commit("logout");
        },
    },
    modules: {
    }
}