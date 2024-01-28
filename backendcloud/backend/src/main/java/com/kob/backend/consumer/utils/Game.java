package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Record;
import org.springframework.security.core.parameters.P;
import org.springframework.ui.context.Theme;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;//意味着一旦被赋值后，其引用不可再更改， g是地图
    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    private final Player playerA, playerB;//a在左下角，b在右下角
    private Integer nextStepA = null;//玩家的下一步操作
    private Integer nextStepB = null; //null表示没有获取到操作， 0123是上右下左
    private ReentrantLock lock = new ReentrantLock();
    private String  status = "playing"; //playing->finished，整个游戏的局面
    private String loser = ""; //all:平局， A: A输

    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB){
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        playerA = new Player(idA, rows - 2, 1, new ArrayList<>());//左下角，数组坐标系
        playerB = new Player(idB, 1, cols - 2, new ArrayList<>());
    }

    public Player getPlayerA(){
        return playerA;
    }
    public Player getPlayerB(){
        return playerB;
    }
    //外面可能会调用这个方法来修改，里面也可能读取值，有冲突
    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally { //无论如何，最后都要解锁，否则会死锁
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    public int[][] getG() {
        return g;
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty){
        if(sx == tx && sy == ty) return true;
        g[sx][sy] = 1; //能来到这里说明g[sx][sy]一定是0

        for(int i = 0; i < 4; i ++){
            int x = sx + dx[i], y = sy + dy[i];
            if(x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0)
                if (check_connectivity(x, y, tx, ty)){
                    g[sx][sy] = 0;
                    return true;
                }
        }

        g[sx][sy] = 0; //回溯
        return false;
    }

    private boolean draw(){ //画地图
        for (int i = 0; i < this.rows; i ++){//初始化
            for (int j = 0; j < this.cols; j ++){
                g[i][j] = 0;
            }
        }

        //给四周加上障碍物
        for(int r = 0; r < this.rows; r ++){
            g[r][0] = g[r][this.cols - 1] = 1;
        }

        for(int c = 0; c < this.cols; c ++){
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        Random random = new Random();
        //里面创建随机障碍物，中心对称
        for(int i = 0; i < this.inner_walls_count / 2; i ++){
            for(int j = 0; j < 1000; j ++){ //循环一千次，大概率能找到合法的
                int r = random.nextInt(this.rows);//返回[0, rows)
                int c = random.nextInt(this.cols);

                if(g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) continue;
                if(r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;
                //在左下角和右上角也是不合法的障碍物

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap(){
        for (int i = 0; i < 1000; i ++){
            if (draw()){
                break;
            }
        }
    }

    private boolean nextStep(){ //等待两名玩家的下一步操作，注意不要和前面的lock造成死锁
        try {
            Thread.sleep(200);//前端是200ms走一格，如果多次按下，只会保留最后一次操作
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < 50; i ++) { //判断五次
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null){
                        playerA.getSteps().add(nextStepA);//如果只有一个人输入，那么不会把操作放进数据库
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB){
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);//取出蛇头
        if (g[cell.x][cell.y] == 1) return false;//撞墙

        for (int i = 0; i < n - 1; i ++){
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)
                return false;
        }
        for (int i = 0; i < n - 1; i ++){//同样是n-1就行
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                return false;
        }
        return true;
    }

    private void judge(){ //判断两名玩家下一步操作是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if (!validA || !validB){
            status = "finished";

            if (!validA && !validB){
                loser = "all";
            } else if (!validA){
                loser = "A";
            } else if(!validB){
                loser = "B";
            }
        }
    }

    private void sendAllMessage(String message){ //给前端发消息
        if (WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);//对应的websocketserver的方法
        if (WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private void sendMove(){ //向两个client传递移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    private String getMapString(){
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; i ++){
            for (int j = 0; j < cols; j ++){
                res.append(g[i][j]); //展开成一维
            }
        }

        return res.toString();
    }

    private void saveToDatabase(){
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    public void sendResult(){ // 向两个client公布结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);

        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run(){
        //大致估算，地图大小13*14< 200，蛇长度每三步增加1，最多600次增长到和地图一样大
        for (int i = 0; i < 1000; i ++){
            if (nextStep()) {
                judge();

                if (status.equals("playing")){
                    sendMove();
                } else {
                    sendResult();
                    break;//发完结果后，此线程结束
                }
            } else {
                status = "finished";

                //边界情况，nextStep()执行完后，才刚好读取到用户操作，这么极限，判你输也合理
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null){
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "A";
                    } else if (nextStepB == null){
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
