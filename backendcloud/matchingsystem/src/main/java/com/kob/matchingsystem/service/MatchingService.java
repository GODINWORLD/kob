package com.kob.matchingsystem.service;

public interface MatchingService {
    String addPlayer(Integer userId, Integer rating);//添加一个玩家
    String removePlayer(Integer userId);
}
