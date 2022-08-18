package com.as.matchingsystem.service.impl;

import com.as.matchingsystem.service.MatchingService;
import com.as.matchingsystem.service.impl.utils.MatchingPool;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    // 唯一类变量
    public final static MatchingPool matchingPool = new MatchingPool();

    @Override
    public String addPlayer(Integer userId, Integer rating) {
        System.out.println("add player :" + userId +" " + rating);
        matchingPool.addPlayer(userId,rating);
        return "add player success!";
    }

    @Override
    public String removePlayer(Integer userId) {
        System.out.println("remove player :" + userId);
        matchingPool.removePlayer(userId);
        return "remove player success!";
    }
}
