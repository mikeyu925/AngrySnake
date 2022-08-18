package com.as.backend.mapper;

import com.as.backend.pojo.Snake;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SnakeMapper extends BaseMapper<Snake> {
}
