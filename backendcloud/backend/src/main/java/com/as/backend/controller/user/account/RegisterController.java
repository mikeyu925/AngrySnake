package com.as.backend.controller.user.account;

import com.as.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户注册
 */
@RestController
public class RegisterController {
    @Autowired
    private  RegisterService registerService;

    @PostMapping("/api/user/account/register/")
    public Map<String,String> register(@RequestParam Map<String,String> info){
        String username = info.get("username");
        String password = info.get("password");
        String confirmedPassword = info.get("confirmedPassword");

        return registerService.register(username,password,confirmedPassword);

    }
}
