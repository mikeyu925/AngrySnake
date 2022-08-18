package com.as.backend.controller.user.account;

import com.as.backend.service.user.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 验证用户名密码，验证成功后返回jwt token（令牌）
 */
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/user/account/token/")
    public Map<String,String> getToken(@RequestParam Map<String,String> info){
        String username = info.get("username");
        String password = info.get("password");
        return loginService.getToken(username,password);
    }
}
