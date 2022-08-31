package com.as.backend.controller.user.snake;

import com.as.backend.pojo.Snake;
import com.as.backend.service.user.snake.GetListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class GetListController {
    @Autowired
    private GetListService getListService;

    @GetMapping("/api/user/snake/getlist/")
    List<Snake> getList(){
        return getListService.getList();
    }
}
