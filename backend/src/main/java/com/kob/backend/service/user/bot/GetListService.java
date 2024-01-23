package com.kob.backend.service.user.bot;

import com.kob.backend.pojo.Bot;

import java.util.List;

public interface GetListService {
    List<Bot> getList();//每个人的id放在token里，所以不需要参数，也能获取自己的bot列表
}
