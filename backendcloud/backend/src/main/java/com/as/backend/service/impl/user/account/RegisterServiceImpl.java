package com.as.backend.service.impl.user.account;

import com.as.backend.mapper.UserMapper;
import com.as.backend.pojo.User;
import com.as.backend.service.user.account.RegisterService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private  UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmedpwd) {
        Map<String,String> map = new HashMap<>();
        if (username == null){
            map.put("error_message","请输入用户名!");
            return map;
        }
        if (password == null || confirmedpwd == null){
            if (password == null){
                map.put("error_message","请输入密码!");
            }else{
                map.put("error_message","请确认密码!");
            }
            return map;
        }

        if (!password.equals(confirmedpwd)){
            map.put("error_message","两次输入密码不一致!");
            return map;
        }

        if (password.length() == 0){
                map.put("error_message","密码不能为空!");
            return map;
        }
        username = username.trim();
        if (username.length() == 0){
            map.put("error_message","用户名不能为空!");
            return map;
        }

        if (username.length() > 100){
            map.put("error_message","用户名太长!请长度输入小于100!");
            return map;
        }



        if (password.length() > 100 ){
            map.put("error_message","密码太长!请长度输入小于100!");
            return map;
        }

        // 判断用户名是否已经被使用
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        List<User> users = userMapper.selectList(queryWrapper);
        if (!users.isEmpty()){
            map.put("error_message","用户名已存在!");
            return map;
        }
        String encodedpwd = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/114192_lg_53b8529a0a.jpg";

        User user = new User(null,username,encodedpwd,photo,1500);
        userMapper.insert(user);
        map.put("error_message","用户名注册成功!");
        return map;
    }
}
