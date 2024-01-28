package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾，也是用jwt验证(token放在链接里）
public class WebSocketServer {

    /**
     *   存储所有连接,由于websocket实例在线程里运行，所以这个公共变量要是线程安全的
    *    虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态map保存起来。
    */
    public final static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();

    private User user;//保存每个连接对应的是谁
    private Session session = null;//每个连接用session来维护，需要通过它来给客户端发送数据

    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    public static BotMapper botMapper;
    public static RestTemplate restTemplate;
    public Game game = null;
    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper = recordMapper;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate = restTemplate;
    }
    @Autowired
    public void setBotMapper(BotMapper botMapper){
        WebSocketServer.botMapper = botMapper;
    }

    //连接建立时，自动触发这个函数
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;
        System.out.println("connected");
        Integer userId = JwtAuthentication.getUserId(token);//静态方法
        this.user = userMapper.selectById(userId);
        if (this.user != null) {
            users.put(userId, this);
        } else {
            this.session.close();
        }
    }

    //连接关闭，自动调用
    @OnClose
    public void onClose() {//如果突然断网，可能无法收到断开连接的消息
        // 关闭链接，普通的关闭页面可以触发这个函数
        System.out.println("disconnected");
        if (this.user != null) {
            users.remove(this.user.getId());
        }
    }

    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId){//被 StartGameServiceImpl调用
        User a = userMapper.selectById(aId), b = userMapper.selectById(bId);
        //botId有可能是-1，导致botA是null
        Bot botA = botMapper.selectById(aBotId), botB = botMapper.selectById(bBotId);

        Game game = new Game(13, 14,  20, a.getId(), botA, b.getId(), botB);
        game.createMap();
        if (users.get(a.getId()) != null)//如果一个人走了，可能会为null
            users.get(a.getId()).game = game;
        if (users.get(b.getId()) != null)
            users.get(b.getId()).game = game;

        game.start();// 启动一个线程

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());

        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame);
        if (users.get(a.getId()) != null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);
        if (users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
    }

    private void startMatching(Integer botId){
        System.out.println("start matching");

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        data.add("bot_id", botId.toString());
        restTemplate.postForObject(addPlayerUrl, data, String.class);//String.class 是期望的响应类型。
    }

    private void stopMatching(){
        System.out.println("stop matching");

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    private void move(int direction){ //接收前端的东西
        if (game.getPlayerA().getId().equals(user.getId())){
            if (game.getPlayerA().getBotId().equals(-1)) //亲自出马
                game.setNextStepA(direction);
            //否则屏蔽掉用户的输入
        } else if (game.getPlayerB().getId().equals(user.getId())){
            if (game.getPlayerB().getBotId().equals(-1))
                game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) { //当作一个路由，判断该调用哪个方法
        // 从Client接收消息
        System.out.println("receive message");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            startMatching(data.getInteger("bot_id"));
        } else if ("stop-matching".equals(event)){
            stopMatching();
        } else if ("move".equals(event)){
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    //后端向前端发送信息
    public void sendMessage(String message){
        synchronized (this.session){//异步，要加锁
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
