package com.as.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Snake {
    @TableId(type = IdType.AUTO)
    private Integer id;  // 主键自增
    private Integer userId; // 数据库: user_id 后端：userId(驼峰)
    private String title;
    private String description;
    private String content;
    private Integer rating;
    @JsonFormat(pattern = "yyyy-MM-d HH:mm:ss",timezone = "Asia/Shanghai")
    private Date createtime;
    @JsonFormat(pattern = "yyyy-MM-d HH:mm:ss",timezone = "Asia/Shanghai")
    private Date modifytime;
}
