package com.as.backend.controller.user.snake;

import com.as.backend.service.user.snake.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 删除一条蛇
 */
@RestController
public class RemoveController {
    @Autowired
    private RemoveService removeService;

    @PostMapping("/api/user/snake/remove/")
    public Map<String,String> remove(@RequestParam Map<String,String> data){
        return removeService.remove(data);
    }
}
