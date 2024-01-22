package com.kob.backend.service.user.account;

import java.util.Map;

public interface LoginService {
    //接口中的方法默认是是公共（public）和抽象（abstract）的。
    //这意味着在接口中声明的方法可以被其他类访问，
    // 并且实现该接口的类必须提供对这些方法的具体实现。由于默认是公共的，因此不需要显式地添加public修饰符来声明接口中的方法。
    Map<String, String> getToken(String username, String password);//方法声明
}
