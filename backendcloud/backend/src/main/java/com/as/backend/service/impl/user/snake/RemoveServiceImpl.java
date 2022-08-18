package com.as.backend.service.impl.user.snake;

import com.as.backend.mapper.SnakeMapper;
import com.as.backend.pojo.Snake;
import com.as.backend.pojo.User;
import com.as.backend.service.impl.utils.UserDetailsImpl;
import com.as.backend.service.user.snake.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class RemoveServiceImpl implements RemoveService {
    @Autowired
    private SnakeMapper snakeMapper;

    @Override
    public Map<String, String> remove(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int snake_id = Integer.parseInt(data.get("snake_id"));
        Snake snake = snakeMapper.selectById(snake_id);
        Map<String,String> map = new HashMap<>();
        if (snake == null){
            map.put("error_message","Snake不存在或已被删除");
            return map;
        }

        if (!snake.getUserId().equals(user.getId())){
            map.put("error_message","Snake不是你的，没有权限删除");
            return map;
        }

        snakeMapper.deleteById(snake_id);
        map.put("error_message","success");
        return map;
    }
}
