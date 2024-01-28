package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer sx;//起点
    private Integer sy;
    private List<Integer> steps;//存每一步的方向

    private boolean check_tail_increasing(int step){ //检测当前回合，蛇的长度是否增加
        if(step <= 10) return true;//规则是前10个回合，每个回合长度加一，即蛇头动，蛇尾不动。
        if(step % 3 == 1) return true;//后面是每三个回合长度才加一
        return false;
    }

    public List<Cell> getCells(){//由于蛇并不大，可以枚举算出蛇的每部分身体
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for (int d : steps){
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!check_tail_increasing(++ step)){
                res.remove(0);//去掉蛇尾
            }
        }
        return res;
    }

    public String getStepsString(){
        StringBuilder res = new StringBuilder();
        for (int d : steps){
            res.append(d);
        }
        return res.toString();
    }
}
