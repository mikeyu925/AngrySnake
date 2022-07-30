package com.as.backend.service.impl.user.snake;

import com.as.backend.mapper.SnakeMapper;
import com.as.backend.pojo.Snake;
import com.as.backend.pojo.User;
import com.as.backend.service.impl.utils.UserDetailsImpl;
import com.as.backend.service.user.snake.GetListService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 查询当前用户的所有snake
 */
@Service
public class GetListServiceImpl implements GetListService {
    @Autowired
    private SnakeMapper snakeMapper;

    @Override
    public List<Snake> getList() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        QueryWrapper<Snake> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",user.getId());
        return snakeMapper.selectList(queryWrapper);
    }
}
