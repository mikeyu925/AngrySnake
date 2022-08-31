package com.as.backend.controller.user.snake;

import com.as.backend.service.user.snake.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 添加一条蛇
 */
@RestController
public class AddController {
    @Autowired
    private AddService addService;

    @PostMapping("/api/user/snake/add/")
    public Map<String,String> add(@RequestParam Map<String,String> info){
        return addService.add(info);
    }
}
