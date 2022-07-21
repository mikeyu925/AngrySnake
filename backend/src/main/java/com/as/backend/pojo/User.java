package com.as.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// 一个表对应一个 pojo
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // 最好用对象类型
    private Integer id;
    private String username;
    private String password;

}
