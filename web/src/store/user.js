import $ from 'jquery'

//存到stroe的东西就是存到内存里，一刷新就清空了

export default{
    state: {
        id: "",
        username: "", //js里，字典的关键字可以括起来"username"，也可以不括
        photo: "",
        token: "",
        is_login: false,
        pulling_info: true, //表示当前是不是在获取信息中，if true，不要展示登陆界面
    },
    getters: {
    },
    mutations: {//同步操作放mutations
        updateUser(state, user){
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token){
            state.token = token;
        },
        logout(state){
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
            $.ajax({
                url: "http://127.0.0.1:3000/user/account/token/",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password,
                },
                success(resp){
                    if (resp.error_message === "success"){
                        localStorage.setItem("jwt_token", resp.token);//放到浏览器的本地内存空间

                        context.commit("updateToken", resp.token); //在ajax里调用mutations里的函数用commit+字符串
                        data.success(resp); //成功了要调用回调函数
                    }
                    else data.error(resp);
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
                            is_login: true,
                        });
                        data.success(resp);
                    }
                    else {
                        data.error(resp);
                    }
                },
                error(resp){
                    data.error(resp);
                }
            })
        },
        logout(context){
            localStorage.removeItem("jwt_token");

            context.commit("logout");
        },
    },
    modules: {
    }
}