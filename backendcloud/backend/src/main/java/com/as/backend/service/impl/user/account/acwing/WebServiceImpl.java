package com.as.backend.service.impl.user.account.acwing;
import com.alibaba.fastjson.JSONObject;
import com.as.backend.mapper.UserMapper;
import com.as.backend.pojo.User;
import com.as.backend.service.impl.user.account.acwing.utils.HttpClientUtil;
import com.as.backend.service.user.account.acwing.WebService;
import com.as.backend.utils.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
public class WebServiceImpl implements WebService {
    private final static String appId = "3235";
    private final static String appSecret = "8f4e3d248a1e48d1b6a6cfafe6044b3d";
    private final static String redirectUrl = "https://app3235.acapp.acwing.com.cn/user/account/acwing/web/receive_code/";
    private final static String applyAccessTokenUrl = "https://www.acwing.com/third_party/api/oauth2/access_token/";
    private final static String getUserInfoUrl = "https://www.acwing.com/third_party/api/meta/identity/getinfo/";

    private final static Random random = new Random();
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    // Web请求AcWing账号登录处理函数
    @Override
    public JSONObject applyCode() {
        JSONObject resp = new JSONObject();
        String encodeUrl = "";
        try {
            // 重定向链接编码
            encodeUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resp.put("result", "failed");
            return resp;
        }
        StringBuilder state = new StringBuilder();
        for (int i = 0; i < 10; i ++ )
            state.append((char) (random.nextInt(10) + '0'));
        resp.put("result", "success");
        // 存至 redis，保存时间为10分钟
        redisTemplate.opsForValue().set(state.toString(), "true");
        redisTemplate.expire(state.toString(), Duration.ofMinutes(10));

        String applyCodeUrl = "https://www.acwing.com/third_party/api/oauth2/web/authorize/?appid=" + appId
                            + "&redirect_uri=" + encodeUrl
                            + "&scope=userinfo"
                            + "&state=" + state;

        resp.put("apply_code_url", applyCodeUrl);

        return resp;
    }

    // 传入的是授权码 和 state:用于判断请求和回调的一致性，授权成功后后原样返回。
    @Override
    public JSONObject receiveCode(String code, String state) {
        JSONObject resp = new JSONObject();
        resp.put("result", "failed");
        if (code == null || state == null) return resp;
        // 如果请求和回调不一致，返回failed
        if (Boolean.FALSE.equals(redisTemplate.hasKey(state))) return resp;
        redisTemplate.delete(state);
        // 准备申请授权令牌access_token 和 用户openid
        List<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("appid", appId)); // 应用唯一id
        nameValuePairs.add(new BasicNameValuePair("secret", appSecret)); // 应用密钥
        nameValuePairs.add(new BasicNameValuePair("code", code)); //  第一步中获取的授权码
        // 申请授权令牌和用于openid
        String getString = HttpClientUtil.get(applyAccessTokenUrl, nameValuePairs);

        if (getString == null) return resp; // 如果返回为空，则申请失败
        JSONObject getResp = JSONObject.parseObject(getString); // 解析成JSON对象
        String accessToken = getResp.getString("access_token");
        String openid = getResp.getString("openid");
        if (accessToken == null || openid == null) return resp; // 申请失败

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid); // 根据用户openid查询用户（新增字段）
        List<User> users = userMapper.selectList(queryWrapper);
        if (!users.isEmpty()) {
            User user = users.get(0); //如果该用户已经存在（已经被授权了）
            String jwt = JwtUtil.createJWT(user.getId().toString());
            resp.put("result", "success");
            resp.put("jwt_token", jwt);
            return resp;
        }
        // 申请用户信息
        nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
        nameValuePairs.add(new BasicNameValuePair("openid", openid));
        getString = HttpClientUtil.get(getUserInfoUrl, nameValuePairs);
        if (getString == null) return resp;
        getResp = JSONObject.parseObject(getString);
        String username = getResp.getString("username");
        String photo = getResp.getString("photo");

        if (username == null || photo == null) return resp;

        // 防止用户名重复
        for (int i = 0; i < 100; i ++ ) {
            QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
            usernameQueryWrapper.eq("username", username);
            if (userMapper.selectList(usernameQueryWrapper).isEmpty()) break;
            username += (char)(random.nextInt(10) + '0');
            if (i == 99) return resp;
        }
        // 将该用户信息插入数据库
        User user = new User(
                null,
                username,
                null,
                photo,
                1500,
                openid
        );
        userMapper.insert(user);
        String jwt = JwtUtil.createJWT(user.getId().toString());
        resp.put("result", "success");
        resp.put("jwt_token", jwt);

        return resp;
    }
}
