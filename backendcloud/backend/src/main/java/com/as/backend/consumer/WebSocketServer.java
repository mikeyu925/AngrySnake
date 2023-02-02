package com.as.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.as.backend.consumer.utils.Game;
import com.as.backend.consumer.utils.JwtAuthentication;
import com.as.backend.mapper.RecordMapper;
import com.as.backend.mapper.SnakeMapper;
import com.as.backend.mapper.UserMapper;
import com.as.backend.pojo.Snake;
import com.as.backend.pojo.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    private Session session = null; // 每个链接实际使用Session维护
    public Game game = null;
    private final static String addPlayerUrl = "http://127.0.0.1:3000/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3000/player/remove/";

    public static UserMapper userMapper;
    public  static RecordMapper recordMapper;
    private static SnakeMapper snakeMapper;
    public static RestTemplate restTemplate;
    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper = recordMapper;
    }
    @Autowired
    public void setBotMapper(SnakeMapper snakeMapper){
        WebSocketServer.snakeMapper = snakeMapper;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate = restTemplate;
    }
    /**
     * 建立连接，链接建立时触发
     * @param session
     * @param token
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session; // 保存当前session
        int userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);

        if (this.user != null){
            users.put(userId,this);
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
        }
    }

    public static void startGame(Integer aId,Integer aBotId,Integer bId,Integer bBotId){
        // 获取玩家
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);
        // 获取snake
        Snake snakeA = snakeMapper.selectById(aBotId);
        Snake snakeB = snakeMapper.selectById(bBotId);

        // 创建地图 并添加至对应用户的链接中
        Game game = new Game(
                13,
                14,
                20,
                a.getId(),
                snakeA,
                b.getId(),
                snakeB);
        game.createGameMap();
        if (users.get(a.getId()) != null)
            users.get(a.getId()).game = game;
        if (users.get(b.getId()) != null)
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
        if (users.get(a.getId()) != null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        // 将配对消息发送给 浏览器B
        JSONObject respB = new JSONObject();
        respB.put("event","start-matching");
        respB.put("opponent_username",a.getUsername());
        respB.put("opponent_photo",a.getPhoto());
        respB.put("game",respGame);
        if (users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());

//        System.out.println(a.getUsername() + " matching with " + b.getUsername() + " successfully!");
    }

    /**
     * 开始匹配
     **/
    private void startMatching(Integer botId){
//        System.out.println("start matching!");
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString()); // 获得当前用户id
        data.add("rating",this.user.getRating().toString()); // 获得当前用户排名
        data.add("bot_id",botId.toString());
        restTemplate.postForObject(addPlayerUrl,data,String.class);
    }

    /**
     * 取消匹配
     */
    private void stopMatching(){
//        System.out.println("stop matching");
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl,data,String.class);
    }

    /**
     * 蛇移动操作 —— 设置game线程中的变量
     */
    private void move(int direction){
        if (game.getPlayerA().getId().equals(user.getId())){
            if (game.getPlayerA().getBotId().equals(-1))  // 亲自出马
                game.setNextStepA(direction);
        }else if (game.getPlayerB().getId().equals(user.getId())){
            if (game.getPlayerB().getBotId().equals(-1))  // 亲自出马
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
            startMatching(data.getInteger("bot_id"));
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