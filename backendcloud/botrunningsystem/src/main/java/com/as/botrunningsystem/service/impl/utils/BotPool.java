package com.as.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{
    private final ReentrantLock lock = new ReentrantLock();   // 锁
    private final Condition condition = lock.newCondition();  // 条件变量
    private final Queue<Bot> bots = new LinkedList<>();

    public void addBot(Integer userId,String botCode,String input){
        lock.lock();
        try {
            bots.add(new Bot(userId,botCode,input));
            // 向队列加入了一个Bot，非空，唤醒沉睡的线程
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }

    /**
     * 目前仅仅支持java代码，
     * 如果想要支持其他语言，可以改成Docker的执行，通过java执行终端命令，设置并启动Docker
     * @param bot
     */
    private void consume(Bot bot){
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000,bot);
    }
    // 本质上是一个消息队列
    @Override
    public void run() {
        while (true){
            lock.lock();
            // 当前队列为控，阻塞当前线程
            if (bots.isEmpty()){
                try {
                    // 当前线程阻塞，直到被唤醒（当阻塞后，会自动释放当前锁）
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            }else{
                Bot bot = bots.remove();
                lock.unlock();

                consume(bot); // 比较耗时，可能执行好几秒
            }

        }
    }
}
