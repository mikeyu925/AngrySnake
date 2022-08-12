package com.as.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.as.backend.consumer.utils.Game;
import com.as.backend.consumer.utils.JwtAuthentication;
import com.as.backend.mapper.RecordMapper;
import com.as.backend.mapper.UserMapper;
import com.as.backend.pojo.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {
    // users 将 userID 映射到 WebSocketServer 中。 ConcurrentHashMap 线程安全的哈希表  类变量
    public final static ConcurrentHashMap<Integer,WebSocketServer> users = new ConcurrentHashMap<>();
    // user 存储用户信息
    private User user;
    // 匹配池
    private final static CopyOnWriteArraySet<User>  matchpool = new CopyOnWriteArraySet<>();

    private Session session = null; // 每个链接实际使用Session维护

    private Game game = null;

    private static UserMapper userMapper;
    public  static RecordMapper recordMapper;
    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper = recordMapper;
    }
    /**
     * 建立连接，链接建立时触发
     * @param session
     * @param token
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session; // 保存当前session
        System.out.println("connected !!!");

        int userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        if (this.user != null){
            users.put(userId,this);
            System.out.println(user);
        }else{
            session.close();
        }

    }

    /**
     * 关闭链接，链接关闭时被调用
     */
    @OnClose
    public void onClose() {
        if (this.user != null){
            users.remove(this.user.getId());
            matchpool.remove(this.user);
        }
        System.out.println("disconnected !!!");
    }

    /**
     * 开始匹配
     */
    private void startMatching(){
        matchpool.add(this.user);

        while (matchpool.size() >= 2){
            Iterator<User> it = matchpool.iterator();
            // 默认 a 左下角 b 右上角
            User a = it.next(),b = it.next();
            matchpool.remove(a);
            matchpool.remove(b);

            // 创建地图 并添加至对应用户的链接中
            Game game = new Game(13,14,20,a.getId(),b.getId());
            game.createGameMap();
            users.get(a.getId()).game = game;
            users.get(b.getId()).game = game;
            // 开启一个新的线程执行游戏pk
            game.start();  // 执行体是 run()

            // 封装地图信息
            JSONObject respGame = new JSONObject();
            respGame.put("a_id",game.getPlayerA().getId());
            respGame.put("a_sx",game.getPlayerA().getSx());
            respGame.put("a_sy",game.getPlayerA().getSy());
            respGame.put("b_id",game.getPlayerB().getId());
            respGame.put("b_sx",game.getPlayerB().getSx());
            respGame.put("b_sy",game.getPlayerB().getSy());
            respGame.put("map",game.getGameMap());

            // 将配对消息发送给 浏览器A
            JSONObject respA = new JSONObject();
            respA.put("event","start-matching");
            respA.put("opponent_username",b.getUsername());
            respA.put("opponent_photo",b.getPhoto());
            respA.put("game",respGame);
            users.get(a.getId()).sendMessage(respA.toJSONString());

            // 将配对消息发送给 浏览器B
            JSONObject respB = new JSONObject();
            respB.put("event","start-matching");
            respB.put("opponent_username",a.getUsername());
            respB.put("opponent_photo",a.getPhoto());
            respB.put("game",respGame);
            users.get(b.getId()).sendMessage(respB.toJSONString());

            System.out.println(a.getUsername() + " matching with " + b.getUsername() + " successfully!");
        }

    }

    /**
     * 取消匹配
     */
    private void stopMatching(){
        matchpool.remove(this.user);
    }

    /**
     * 蛇移动操作 —— 设置game线程中的变量
     */
    private void move(int direction){
        if (game.getPlayerA().getId().equals(user.getId())){
            game.setNextStepA(direction);
        }else if (game.getPlayerB().getId().equals(user.getId())){
            game.setNextStepB(direction);
        }
    }

    /**
     * 从Client接收消息，当后端接收到前端信息时触发该函数
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)){
            startMatching();
        }else if ("stop-matching".equals(event)){
            stopMatching();
        }else if ("move".equals(event)){
            move(data.getInteger("direction"));
        }
    }

    /**
     * 从后端发送信息
     * @param message
     */
    public void sendMessage(String message) {
        synchronized (this.session){
            try {
                this.session.getBasicRemote().sendText(message);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}