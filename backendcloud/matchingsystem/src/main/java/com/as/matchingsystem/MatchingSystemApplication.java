package com.as.matchingsystem;


import com.as.matchingsystem.service.impl.MatchingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//入口注解
@SpringBootApplication
public class MatchingSystemApplication {
    public static void main(String [] args){
        MatchingServiceImpl.matchingPool.start(); // 启动匹配线程
        SpringApplication.run(MatchingSystemApplication.class,args);
    }
}
