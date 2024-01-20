import $ from 'jquery'

export default{
    state: {
        id: "",
        username: "", //js里，字典的关键字可以括起来"username"，也可以不括
        photo: "",
        token: "",
        is_login: false,
    },
    getters: {
    },
    mutations: {
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
    },
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
            context.commit("logout");
        },
    },
    modules: {
    }
}