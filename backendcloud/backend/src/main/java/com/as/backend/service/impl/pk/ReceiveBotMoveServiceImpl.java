package com.as.backend.service.impl.pk;

import com.as.backend.consumer.WebSocketServer;
import com.as.backend.consumer.utils.Game;
import com.as.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        System.out.println("receive bot move : " + userId + " " + direction);

        if (WebSocketServer.users.get(userId) != null){
            // 获取用户对应的WebSocketServer并获得其game成员变量
            Game game = WebSocketServer.users.get(userId).game;
            if (game != null){ // 判断玩家并设置方向
                if (game.getPlayerA().getId().equals(userId)){
                    game.setNextStepA(direction);
                }else if (game.getPlayerB().getId().equals(userId)){
                    game.setNextStepB(direction);
                }
            }
        }

        return "receive bot move success";
    }
}
