package com.as.backend;

import com.as.backend.mapper.UserMapper;
import com.as.backend.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class BackendApplicationTests {
    @Autowired
    private UserMapper userMapper;
    @Test
    void contextLoads() {
        System.out.println("-------Test--------");
        List<User> userList = userMapper.selectList(null);
        userList.forEach(System.out::println);
    }

}
