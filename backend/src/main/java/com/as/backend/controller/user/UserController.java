package com.as.backend.controller.user;

import com.as.backend.mapper.UserMapper;
import com.as.backend.pojo.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserMapper userMapper;

    @GetMapping("/user/all/")
    public List<User> getAll(){
        return userMapper.selectList(null); // null 表示查询所有
    }

    // springboot 传入参数用 {},获取对应的参数需要用到注解@PathVariable
    @GetMapping("/user/{userId}/")
    public User getUser(@PathVariable int userId){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",userId);
        return userMapper.selectOne(queryWrapper);
    }


    @GetMapping("/user/add/{userId}/{username}/{password}/")
    public String addUser(
                @PathVariable int userId,
                @PathVariable String username,
                @PathVariable String password){
        if (password.length() < 6){
            return "password is so short!";
        }
        // 对明文进行加密
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPwd =  passwordEncoder.encode(password);
        User user = new User(userId,username,encodedPwd);
        userMapper.insert(user);
        return "Add user successfully!";
    }

    @GetMapping("/user/delete/{userId}/")
    public String deleteUser(@PathVariable int userId){
        userMapper.deleteById(userId);
        return "Delete user successfully!";
    }
}
