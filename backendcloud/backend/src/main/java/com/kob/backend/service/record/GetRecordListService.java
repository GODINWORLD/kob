package com.kob.backend.service.record;

import com.alibaba.fastjson2.JSONObject;

public interface GetRecordListService {
    JSONObject getList(Integer page);//展示第几页的信息
}
