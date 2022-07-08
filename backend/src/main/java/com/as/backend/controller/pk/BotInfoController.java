package com.as.backend.controller.pk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pk/")
public class BotInfoController {
    @RequestMapping("getbotinfo")
    public Map<String,String> getBotInfo(){
        Map<String,String> bot = new HashMap<>();
        bot.put("name","Fish");
        bot.put("rating","600");
        return bot;
    }
}
