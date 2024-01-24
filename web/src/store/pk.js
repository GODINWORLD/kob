

export default{
    state: {
        socket: null, //链接
        status: "matching", //matching表示匹配界面, playing表示对战页面
        opponent_username: "",
        opponent_photo: "", //对手信息
        gamemap: null,
    },
    getters: {

    },
    mutations: {
        updateSocket(state, socket){
            state.socket = socket;
        },
        updateOpponent(state, opponent){
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status){
            state.status = status;
        },
        updateGamemap(state, gamemap){
            state.gamemap = gamemap;
        },
    },

    actions: {

    },
    modules: {
    }
}