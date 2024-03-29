package com.kob.backend.service.impl.ranklist;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.ranklist.GetRankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRankListServiceImpl implements GetRankListService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getList(Integer page) {
        IPage<User> userIPage = new Page<>(page, 3);//每页展示3个用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("rating"); //按天梯分降序排列
        List<User> users = userMapper.selectPage(userIPage, queryWrapper).getRecords(); //getRecord()用于获取分页查询结果的记录列表
        JSONObject resp = new JSONObject();

        for (User user : users) {
            user.setPassword(""); //清空密码，安全
        }

        resp.put("users", users);
        resp.put("users_count", userMapper.selectCount(null));

        return resp;
    }
}
