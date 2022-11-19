package com.as.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// 一个表对应一个 pojo
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // 最好用对象类型
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String photo;
    private Integer rating;
    private String openid; // 增加的字段
}
