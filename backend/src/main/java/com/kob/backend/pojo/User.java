package com.kob.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor //看target那里可以知道，自动生成getter,setter,等方法
public class User {
    private Integer id;
    private String username;
    private String password;
}
