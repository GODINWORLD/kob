<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'"/>
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
    <ResultBoard v-if="$store.state.pk.loser != 'none' "/>
</template>

<script>
import PlayGround from '../../components/PlayGround.vue';
import MatchGround from '@/components/MatchGround.vue';
import ResultBoard from '@/components/ResultBoard.vue';
import { onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';

export default{
    components:{
        PlayGround,
        MatchGround,
        ResultBoard,
    },
    setup() {
        const store = useStore();
        const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}/`;//注意url

        store.commit("updateLoser", "none");

        let socket = null;
        onMounted(() => { //当前组件被挂载完成后，可以认为是页面打开后

            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            });

            socket = new WebSocket(socketUrl);//vue3自带的WebSocket

            socket.onopen = () => {
                console.log("connected");
                store.commit("updateSocket", socket);
            }
            socket.onmessage = msg => {
                const data = JSON.parse(msg.data); //将一个 JSON 格式的字符串解析为 JavaScript 对象
                if (data.event === "start-matching"){  //匹配成功
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    });

                    // 如果后端发送消息的速度非常快，可能会导致 setTimeout 还未执行完毕就接收到了新的消息，
                    // 从而在处理新消息时访问到未赋值的 gameObject，导致空指针错误。
                    setTimeout(() => {//延迟一秒
                        store.commit("updateStatus", "playing");//status转换后，某些页面可以展示出来
                    }, 200);
                    store.commit("updateGame", data.game);

                } else if (data.event === "move") { //后端叫前端动， 才能动
                    console.log(data);
                    const game = store.state.pk.gameObject;//这个game是前端的地图对象
                    const [snake0, snake1] = game.snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction); 
                } else if (data.event === "result"){
                    console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;

                    if (data.loser ===  "all" || data.loser === "A"){
                        snake0.status = "die";
                    }
                    if (data.loser ===  "all" || data.loser === "B"){
                        snake1.status = "die";
                    }
                    store.commit("updateLoser", data.loser);
                }
            }
            socket.onclose = () => {
                console.log("disconnected");
            }

        })
        onUnmounted(() => {
            socket.close(); //每次离开这个页面要断开连接，不然不断切换页面时，会产生很多冗余连接
            store.commit("updateStatus", "matching");
        })

        return {

        }
    }
}

</script>

<style scoped>

</style>