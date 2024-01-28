package com.kob.backend.service.impl.user.account;

import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.account.LoginService;
import com.kob.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Map<String, String> getToken(String username, String password) {

        // 生成一个包含账号密码的认证信息
        UsernamePasswordAuthenticationToken  authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);

        // AuthenticationManager校验这个认证信息，判断是否是当前用户，返回一个已认证的Authentication
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);//登陆失败，会自动处理

        //如果能执行到这里，一定是成功了
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        User user = loginUser.getUser();

        String jwt = JwtUtil.createJWT(user.getId().toString());
        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        map.put("token", jwt);//token名称可以随便定义，和前端对应即可
        //jwt是令牌

        return map;
    }
}
