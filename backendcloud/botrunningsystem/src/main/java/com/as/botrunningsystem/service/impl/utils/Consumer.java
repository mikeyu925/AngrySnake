package com.as.botrunningsystem.service.impl.utils;

import com.as.botrunningsystem.utils.BotInterface;
import com.as.botrunningsystem.utils.CompilerUtil;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class Consumer extends Thread{
    private Bot bot;
    private static RestTemplate restTemplate;

    private final static String receiveBotMoveUrl = "http://127.0.0.1:6969/pk/receive/bot/move/";
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        Consumer.restTemplate = restTemplate;
    }
    /**
     * 定时启动
     * @param timeout
     * @param bot
     */
    public void startTimeout(long timeout,Bot bot){
        this.bot = bot;
        // 执行当前线程任务
        this.start();
        try {
            this.join(timeout); // 最多等待 timeout毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.interrupt(); // 中断当前线程
    }

    /**
     * 在Code中Bot类名后添加uid
     * @param code
     * @param uid
     * @return
     */
    private String addUid(String code,String uid){
        int k = code.indexOf(" implements java.util.function.Supplier<Integer>");
        return code.substring(0,k) + uid + code.substring(k);
    }

    /**
     * 线程执行体
     */
    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0,8);

        Supplier<Integer> botInterface = Reflect.compile(
                "com.as.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();

        System.out.println(bot.getUserId() + " : " + bot.getInput());


        File file = new File("input.txt");
        try (PrintWriter fout = new PrintWriter(file)) {
            fout.println(bot.getInput()); // 将对局信息保存至文件
            fout.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Integer direction = botInterface.get();
        // 封装信息【用户ID、snake的下一步方向】
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());
        // 发送至后端 ReceiveBotMoveController
        restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
    }

    /** 废弃

     @Override
     public void run() {
     UUID uuid = UUID.randomUUID();
     String uid = uuid.toString().substring(0,8);
     String code = addUid(bot.getBotCode(),uid);
     // 创建这个类
     createFile("Bot" + uid,code);
     try{
     Supplier<Integer> botInterface = CompilerUtil.generateClass("Bot" + uid, "com.kob.botrunningsystem.utils", code);

     File file = new File("input.txt");
     try(PrintWriter fout = new PrintWriter(file)) {
     fout.println(bot.getInput());
     fout.flush();
     }catch (FileNotFoundException fe){
     fe.printStackTrace();
     }

     Integer direction = botInterface.get();
     System.out.println(bot.getUserId() + " move-dir : " + direction);

     MultiValueMap<String ,String> data = new LinkedMultiValueMap<>();
     data.add("user_id",bot.getUserId().toString());
     data.add("direction",direction.toString());
     deleteFile("Bot" + uid);

     restTemplate.postForObject(receiveBotMoveUrl,data,String.class);
     }catch (Exception e){
     e.printStackTrace();
     }

     }
    private void deleteFile(String name) {
        // 这个路径是自己写的，自己定义即可
        File file = new File("F:\\Java\\JavaProjects\\AngrySnake\\backendcloud\\botrunningsystem/src/main/java/com/as/botrunningsystem/utils/" + name + ".java");
        if (file.exists()) {
            System.out.println("----");
            file.delete();
        }
    }

    private void createFile(String name, String code) {
        try (FileWriter file = new FileWriter("F:\\Java\\JavaProjects\\AngrySnake\\backendcloud\\botrunningsystem/src/main/java/com/as/botrunningsystem/utils/" + name + ".java");) {
            file.write(code);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     **/

}
