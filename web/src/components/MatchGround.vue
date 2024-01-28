
<template>
    <div class="matchground">
        <!--div.row>div.col-6+div.col-6 -->
        <div class="row">
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.user.photo">
                </div>
                <div class="user-username">
                    {{ $store.state.user.username }}
                </div>
            </div>

            <div class="col-4">
                <div class="user-select-bot">
                    <select v-model="select_bot" class="form-select" aria-label="Default select example">
                        <option value="-1" selected>亲自出马</option>
                        <option v-for="bot in bots" :key="bot.id" :value="bot.id">
                            {{ bot.title }}
                        </option>
                    </select>
                </div>
            </div>

            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.pk.opponent_photo">
                </div>
                <div class="user-username">
                    {{ $store.state.pk.opponent_username }}
                </div>
            </div>

            <div class="col-12" style="text-align: center; padding-top: 15vh">
                <button @click="click_match_btn" type="button" class="btn btn-warning btn-lg">{{match_btn_info}}</button>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue';
import { useStore } from 'vuex';
import $ from 'jquery';

export default{ 
    setup() {
        let match_btn_info = ref("开始匹配");
        const store = useStore();
        let bots = ref([]);
        let select_bot = ref("-1");//一开始是-1，即本人操作

        const click_match_btn = () => {
            if (match_btn_info.value === "开始匹配") {
                match_btn_info.value = "取消"; 
                store.state.pk.socket.send(JSON.stringify({
                    event: "start-matching", //event是键
                    bot_id: select_bot.value,
                }));
            } else {
                match_btn_info.value = "开始匹配";
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop-matching",
                }));
            }
        };

        const refresh_bots = () => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/getlist/",
                type: "get",
                headers:{//除了被后端放行的url外，其他的都要加headers
                    Authorization: "Bearer " + store.state.user.token, 
                },
                success(resp){
                    bots.value = resp;
                },
            })
        };

        refresh_bots(); //从云端动态获取bots

        return {
            match_btn_info,
            click_match_btn,
            bots,
            select_bot,
        }
    }

}
</script>

<style scoped>
div.matchground{
    width: 60vw; 
    height: 70vh;
    margin: 40px auto;
    background-color: rgb(50, 50, 50, 0.5);
}
div.user-photo {
    text-align: center;
    padding-top: 10vh;
}
div.user-photo > img {
    border-radius: 50%;
    width: 20vh;
}
div.user-username {
    text-align:  center;
    font-size: 24px;
    font-weight: 600;/*加粗 */
    color: white;
    padding-top: 2vh;
}
div.user-select-bot {
    padding-top: 20vh;
}
div.user-select-bot > select { /* 选择 div 元素中 class 为 "user-select-bot" 的子元素中的 select 元素。*/
    width: 60%; /*设置为父元素宽度的60% */
    margin: 0 auto; /* 这是设置上下外边距为 0，左右外边距自动居中。 */
}

</style>