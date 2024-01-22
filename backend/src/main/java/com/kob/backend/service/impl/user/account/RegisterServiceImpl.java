package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;//密码加密

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String, String> map = new HashMap<>();
        if (username == null){
            map.put("error_message", "用户名不能为空");
            return map;
        }

        if (password == null || confirmedPassword == null){
            map.put("error_message", "密码不能为空");
            return map;
        }

        //用户名不能一样
        username = username.trim();//去掉空白字符,用户名不允许有前置空白字符，密码可以有空白字符
        if (username.length() == 0){
            map.put("error_message", "用户名不能为空");
            return map;
        }

        if (password.length() == 0 || confirmedPassword.length() == 0){
            map.put("error_message", "密码不能为空");
            return map;
        }

        if (username.length() > 100){
            map.put("error_message", "用户名长度不能大于100");
            return map;
        }

        if (password.length() > 100 || confirmedPassword.length() > 100){
            map.put("error_message", "密码长度不能大于100");
            return map;
        }

        if (!password.equals(confirmedPassword)){
            map.put("error_message", "两次输入的密码不一致");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);//查询用户名等于username的用户，用selectOne也行

        if (!users.isEmpty()){
            map.put("error_message", "用户名已存在");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(password);//加密后的密码
        String photo = "https://cdn.acwing.com/media/user/profile/photo/189081_sm_04d17020bb.jpg";
        User user = new User(null, username, encodedPassword, photo);//id是自增的

        userMapper.insert(user);//记得修改数据库
        map.put("error_message", "success");
        return map;
    }
}
