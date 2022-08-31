package com.as.botrunningsystem.service.impl;

import com.as.botrunningsystem.service.BotRunningService;
import com.as.botrunningsystem.service.impl.utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {
    public final static BotPool botpool = new BotPool();

    @Override
    public String addBot(Integer userId, String botCode, String input) {
        botpool.addBot(userId,botCode,input);
        return "add bot success!";
    }
}
