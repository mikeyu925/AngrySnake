package com.as.backend.service.impl.user.snake;

import com.as.backend.mapper.SnakeMapper;
import com.as.backend.pojo.Snake;
import com.as.backend.pojo.User;
import com.as.backend.service.impl.utils.UserDetailsImpl;
import com.as.backend.service.user.snake.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
public class UpdateServiceImpl implements UpdateService {
    @Autowired
    private SnakeMapper snakeMapper;

    @Override
    public Map<String, String> update(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int snake_id = Integer.parseInt(data.get("snake_id"));

        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String,String> map = new HashMap<>();
        if (title == null || title.length() == 0){
            map.put("error_message","标题不能为空");
            return map;
        }
        if (title.length() >  100){
            map.put("error_message","标题长度不能大于100");
            return map;
        }
        if (description != null && description.length() > 300){
            map.put("error_message","Snake描述长度不能大于300");
            return map;
        }
        if (description == null || description.length() == 0){
            description = "这个用户很懒，什么描述也没有.";
        }
        if (content == null || content.length() == 0){
            map.put("error_message","代码不能为空");
            return map;
        }
        if (content.length() > 10000){
            map.put("error_message","代码长度不能超过10000");
            return map;
        }

        Snake snake = snakeMapper.selectById(snake_id);
        if (snake == null){
            map.put("error_message","snake不存在");
            return map;
        }

        if (!snake.getUserId().equals(user.getId())){
            map.put("error_message","不是你的snake，没有权限修改");
            return map;
        }

        Snake update_snake = new Snake(
                snake.getId(),
                user.getId(),
                title,
                description,
                content,
                snake.getRating(),
                snake.getCreatetime(),
                new Date()
        );

        snakeMapper.updateById(update_snake);

        map.put("error_message","success");
        return map;
    }
}
