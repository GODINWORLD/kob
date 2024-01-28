package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//验证用户名密码，验证成功后返回jwt token（令牌）
@RestController //返回的数据会直接作为响应的主体内容（JSON 或 XML），不进行页面跳转或视图解析
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/user/account/token/") //从post请求里获取数据
    public Map<String, String> getToken(@RequestParam Map<String, String> map){
        String username = map.get("username");//这里的名字要和前端对应
        String password = map.get("password");
        return loginService.getToken(username, password);
    }
    //Map对象也可以很方便地转换为JSON格式，方便前端处理
}
