package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor //看target那里可以知道，自动生成getter,setter,等方法
public class User {
    @TableId(type = IdType.AUTO) //id自增
    private Integer id;
    private String username;
    private String password;
    private String photo;
}
