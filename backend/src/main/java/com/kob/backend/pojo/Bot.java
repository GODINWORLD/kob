package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bot {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;//数据库里是user_id，这里用驼峰命名
    private String title; //bot的标题，在数据库里并没有设置为非空，未来可能实现草稿功能等，作者没想好标题也能保存。

    private String description;
    private String content; //bot的代码
    private Integer rating; //积分

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")//定义成特定格式，东八区写上海即可
    private Date createtime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")//定义成特定格式
    private Date modifytime;
}
