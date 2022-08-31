package com.as.backend.controller.user.snake;

import com.as.backend.service.user.snake.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 更新一条蛇的信息
 */
@RestController
public class UpdateController {
    @Autowired
    private UpdateService updateService;

    @PostMapping("/api/user/snake/update/")
    Map<String,String> update(@RequestParam Map<String,String> data){
        return updateService.update(data);
    }
}
