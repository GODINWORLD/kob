package com.kob.botrunningsystem.service;

public interface BotRunningService {
    String addBot(Integer userId, String botCode, String input);//bot属于谁，代码是什么，其他输入信息
}
