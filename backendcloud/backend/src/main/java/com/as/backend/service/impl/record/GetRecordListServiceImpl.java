package com.as.backend.service.impl.record;

import com.alibaba.fastjson.JSONObject;
import com.as.backend.mapper.RecordMapper;
import com.as.backend.mapper.UserMapper;
import com.as.backend.pojo.Record;
import com.as.backend.pojo.User;
import com.as.backend.service.record.GetRecordListService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class GetRecordListServiceImpl implements GetRecordListService {
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getList(Integer page) {
        // 假定每条页面展示前10条记录 1 : 0~9  2 : 10~19
        IPage<Record> recordIPage = new Page<>(page,10);
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();

        // 按照id降序排序
        queryWrapper.orderByDesc("id");
        List<Record> records = recordMapper.selectPage(recordIPage,queryWrapper).getRecords();
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (Record record : records){
            User userA = userMapper.selectById(record.getAId());
            User userB = userMapper.selectById(record.getBId());
            JSONObject item = new JSONObject();
            item.put("a_photo",userA.getPhoto());
            item.put("a_username",userA.getUsername());
            item.put("b_photo",userB.getPhoto());
            item.put("b_username",userB.getUsername());

            String result = "平局";
            if ("A".equals(record.getLoser())){
                result = "B胜";
            }else if ("B".equals(record.getLoser())){
                result = "A胜";
            }
            item.put("result",result);
            item.put("record",record);
            items.add(item);
        }
        resp.put("records",items);
        // 获得records总数
        resp.put("records_count",recordMapper.selectCount(null));

        return resp;
    }
}
