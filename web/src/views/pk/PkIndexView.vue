<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'"/>
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
</template>

<script>
import PlayGround from '../../components/PlayGround.vue';
import MatchGround from '@/components/MatchGround.vue';
import { onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';

export default{
    components:{
        PlayGround,
        MatchGround,
    },
    setup() {
        const store = useStore();
        const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}/`;//注意url

        let socket = null;
        onMounted(() => { //当前组件被挂载完成后，可以认为是页面打开后

            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            });

            socket = new WebSocket(socketUrl);

            socket.onopen = () => {
                console.log("connected");
                store.commit("updateSocket", socket);
            }
            socket.onmessage = msg => {
                const data = JSON.parse(msg.data);
                if (data.event === "start-matching"){  //匹配成功
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    });
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                    }, 1000);
                    store.commit("updateGamemap", data.gamemap);
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