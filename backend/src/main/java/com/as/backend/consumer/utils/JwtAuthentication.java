package com.as.backend.consumer.utils;

import com.as.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;

public class JwtAuthentication {

    public static Integer getUserId(String token){
        Integer userId = -1;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId =Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userId;
    }

}
