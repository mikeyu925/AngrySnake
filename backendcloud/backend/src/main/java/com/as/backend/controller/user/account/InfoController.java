package com.as.backend.controller.user.account;

import com.as.backend.service.user.account.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 根据令牌 token 返回用户信息 user info
 */

@RestController
public class InfoController {
    @Autowired
    private InfoService infoService;

    @GetMapping("/api/user/account/info/")
    public Map<String ,String> getInfo(){
        return infoService.getInfo();
    }
}
