package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  //看target那里可以知道，自动生成 getter,setter,tostring等方法
@NoArgsConstructor //无参构造函数
@AllArgsConstructor //全参构造函数
public class User {
    @TableId(type = IdType.AUTO) //id自增
    private Integer id; //用包装类
    private String username;
    private String password;
    private String photo;
    private Integer rating; //积分
}
