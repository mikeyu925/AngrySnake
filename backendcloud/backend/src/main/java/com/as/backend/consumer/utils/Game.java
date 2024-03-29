package com.as.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.as.backend.consumer.WebSocketServer;
import com.as.backend.pojo.Record;
import com.as.backend.pojo.Snake;
import com.as.backend.pojo.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    /**
     * final 修饰 ?
     */
    // 地图信息
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int [][] walls;
    private final static int [] dx = {-1,0,1,0},dy = {0,1,0,-1};
    // 玩家信息
    private final Player player_A,player_B;

    // 游戏pk过程信息
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    // 由于 nextStepA 和 nextStepB 可能被两个线程同时读写，因此需要加锁
    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing"; // 进行: playing 结束: finshed
    private String loser = ""; // all : 平局  A: A输  B: B输
    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";


    public  Game(int r, int c, int cnt, int idA, Snake snakeA,int idB,Snake snakeB){
        this.rows = r;
        this.cols = c;
        this.inner_walls_count = cnt;
        this.walls = new int[r][c];

        Integer botId_A = -1,botId_B = -1;
        String botCodeA = "",botCodeB = "";

        if (snakeA != null){
            botId_A = snakeA.getId();
            botCodeA = snakeA.getContent();
        }
        if (snakeB != null){
            botId_B = snakeB.getId();
            botCodeB = snakeB.getContent();
        }
        // 初始化A B玩家起始坐标
        this.player_A = new Player(idA,botId_A,botCodeA,this.rows-2,1,new ArrayList<>());
        this.player_B = new Player(idB,botId_B,botCodeB,1,this.cols-2,new ArrayList<>());
    }

    public int[][] getGameMap(){
        return this.walls;
    }
    public Player getPlayerA(){
        return this.player_A;
    }
    public Player getPlayerB(){
        return this.player_B;
    }

    public void setNextStepA(Integer nextStepA){
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        }finally {
            lock.unlock();
        }
    }
    public void setNextStepB(Integer nextStepB){
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        }finally {
            lock.unlock();
        }
    }

    /**
     * 检查创建的地图的连通性
     * @param sx
     * @param sy
     * @param tx
     * @param ty
     * @return
     */
    private boolean check_connectivity(int sx,int sy,int tx,int ty){
        if (sx == tx && sy == ty) return true;
        this.walls[sx][sy] = 1;

        for (int i = 0;i < 4;i++){
            int x = sx + dx[i],y = sy + dy[i];
            if (x < 0 || x >= this.rows || y < 0 || y >= this.cols || this.walls[x][y] == 1) continue;
            if (check_connectivity(x,y,tx,ty)){
                walls[sx][sy] = 0; // 记得恢复现场
                return true;
            }
        }
        walls[sx][sy] = 0;
        return false;
    }

    /**
     * 绘制地图
     * @return
     */
    private boolean drawGameMap(){
        // 初始化
        for (int i = 0;i < this.rows;i++)
            Arrays.fill(this.walls[i],0);

        // 画四周围墙
        for (int r = 0;r < this.rows;r++){
            this.walls[r][0] = this.walls[r][this.cols-1] = 1;
        }
        for (int c = 0;c < this.cols;c++){
            this.walls[0][c] = this.walls[this.rows-1][c] = 1;
        }
        Random random = new Random();
        // 内部随机产生inner_walls_count个中心对称墙体
        for (int i = 0;i < this.inner_walls_count / 2;i++){
            int r,c;
            do {
                r = random.nextInt(this.rows);
                c = random.nextInt(this.cols);
            }while (this.walls[r][c] == 1 || this.walls[this.rows - 1 - r][this.cols - 1 - c] == 1 || (r == this.rows - 2 && c == 1) || (r == 1 && c == this.cols - 2));

            this.walls[r][c] = this.walls[this.rows - 1 - r][this.cols - 1 - c] = 1;
        }
        return check_connectivity(this.rows-2,1,1,this.cols-2);
    }

    public void createGameMap(){  // 创建地图
        while (!drawGameMap());
    }

    /**
     * 将当前的 对局信息 编码成字符串  # 隔开
     * 第一段：地图
     * 第二段: 自己的起始坐标 x
     * 第三段: 自己的起始坐标 y
     * 第四段: 自己的操作
     * 第五段: 对手的起始坐标 x
     * 第六段: 对手的起始坐标 y
     * 第七段：对手的操作
     * @param player
     * @return
     */
    private String getInput(Player player){
        Player me,you;
        if (player_A.getId().equals(player.getId())){
            me = player_A;
            you = player_B;
        }else{
            me = player_B;
            you = player_A;
        }
        return    getMapString() + "#"
                + me.getSx() + "#"
                + me.getSy() + "#("
                + me.getStepsString() + ")#"
                + you.getSx() + "#"
                + you.getSy() + "#("
                + you.getStepsString() + ")";
    }

    /**
     * 向AI自动对战 3002端口 发送Bot信息
     * @param player
     */
    private void sendBotCode(Player player){
        if (player.getBotId().equals(-1)) return ; // 选手亲自出马，不需要执行代码
        // Bot出战 ：打包【玩家ID、snake代码、对局信息】
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",player.getId().toString());
        data.add("bot_code",player.getBotCode());
        data.add("input",getInput(player));
        // 对botrunningsystem发送请求 addboturl
        WebSocketServer.restTemplate.postForObject(addBotUrl,data,String.class);
    }

    /**
     * 等待两名玩家下一步操作
     * @return
     */
    private boolean nextStep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 如果不是人工出战，设置Bot方向
        sendBotCode(player_A);
        sendBotCode(player_B);

        // 等待是否检测到玩家AB的输入
        for (int i = 0;i < 50;i++){
            try {
                Thread.sleep(100); // 睡100毫秒
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null){
                        player_A.getSteps().add(nextStepA);
                        player_B.getSteps().add(nextStepB);
                        return true;
                    }
                }finally {
                    lock.unlock();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 广播发送消息
     * @param message
     */
    public void sendAllMessage(String message){
        if (WebSocketServer.users.get(player_A.getId()) != null)
            WebSocketServer.users.get(player_A.getId()).sendMessage(message);
        if (WebSocketServer.users.get(player_B.getId()) != null)
            WebSocketServer.users.get(player_B.getId()).sendMessage(message);
    }

    /**
     * 获得地图字符串形式
     * @return
     */
    private String getMapString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < this.rows;i++){
            for (int j = 0;j < this.cols;j++){
                sb.append(this.walls[i][j]);
            }
        }
        return sb.toString();
    }

    /**
     * 更新玩家天梯积分
     * @param player
     * @param rating
     */
    private void updateRating(Player player,Integer rating){
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    /**
     * 将此次对局信息保存至数据库
     */
    private void saveToDatabase(){
        Integer ratingA = WebSocketServer.userMapper.selectById(player_A.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(player_B.getId()).getRating();

        if ("A".equals(loser)){
            ratingA -= 2;
            ratingB += 3;
        }else if ("B".equals(loser)){
            ratingA += 3;
            ratingB -= 2;
        }
        updateRating(player_A,ratingA);
        updateRating(player_B,ratingB);

        Record record = new Record(
                null,
                player_A.getId(),
                player_A.getSx(),
                player_A.getSy(),
                player_B.getId(),
                player_B.getSx(),
                player_B.getSy(),
                player_A.getStepsString(),
                player_B.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    /**
     * 向两个client广播结果
     */
    private void sendResult(){
        JSONObject resp = new JSONObject();
        resp.put("event","result");
        resp.put("loser",this.loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    /**
     * 判断蛇cellsA当前Step的有效性
     * @param cellsA
     * @param cellsB
     * @return
     */
    private boolean checkVaild(List<Cell> cellsA,List<Cell> cellsB){
        int n = cellsA.size();
        Cell headA = cellsA.get(n-1); // 蛇头
        if (this.walls[headA.x][headA.y] == 1) return false; // 判断是否撞到了墙
        // 判断是否撞到了自己 或者 撞到了 对方
        for (int i = 0;i < n-1;i++){
            if (cellsA.get(i).x == headA.x && cellsA.get(i).y == headA.y){
                return false;
            }
            if (cellsB.get(i).x == headA.x && cellsB.get(i).y == headA.y){
                return false;
            }
        }
        return true;
    }


    /**
     * 判断两条蛇下一步操作是否合法
     */
    private void judge(){
        List<Cell> cellsA = player_A.getCells();
        List<Cell> cellsB = player_B.getCells();

        boolean validA = checkVaild(cellsA,cellsB);
        boolean validB = checkVaild(cellsB,cellsA);

        if (!validA || !validB){
            this.status = "finished";
            if (!validA && !validB) this.loser = "all";
            else if (!validA) this.loser = "A";
            else this.loser = "B";
        }

    }

    /**
     * 向两个Client广播移动信息
     */
    private void sendMove(){
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event","move");
            resp.put("a_direction",nextStepA);
            resp.put("b_direction",nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        }finally {
            lock.unlock();
        }
    }

    /**
     * 游戏线程 执行体
     */
    @Override
    public void run() {
        for (int i = 0; i < 1000;i++){
            if (nextStep()){ // 如果检测到两名玩家的下一步输入
                judge(); // 判断是否合法
                if ("playing".equals(this.status)){ // 游戏没有结束
                    sendMove(); // 向前端发送 a b 的下一步操作
                }else{
                    sendResult();  // 游戏结束
                    break;
                }
            }else{
                this.status = "finished";
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null){
                        this.loser = "all";
                    }else if (nextStepA == null){
                        this.loser = "A";
                    }else{
                        this.loser = "B";
                    }
                }finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
