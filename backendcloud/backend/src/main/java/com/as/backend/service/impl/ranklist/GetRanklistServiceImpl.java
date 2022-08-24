package com.as.backend.service.impl.ranklist;

import com.alibaba.fastjson.JSONObject;
import com.as.backend.mapper.UserMapper;
import com.as.backend.pojo.User;
import com.as.backend.service.ranklist.GetRanklistService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRanklistServiceImpl implements GetRanklistService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getList(Integer page) {
        IPage<User> userIPage = new Page<>(page,10);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("rating");
        List<User> users = userMapper.selectPage(userIPage,queryWrapper).getRecords();
        JSONObject resp = new JSONObject();
        for (User user : users)
            user.setPassword("");

        resp.put("users",users);
        resp.put("users_count",userMapper.selectCount(null));
        return resp;
    }
}
