package com.bxtdata.interview.interview.service.impl;

import com.bxtdata.interview.interview.mapper.BreakRecordMapper;
import com.bxtdata.interview.interview.pojo.po.BreakRecordPO;
import com.bxtdata.interview.interview.service.CommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import reactor.util.annotation.NonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class CommodityServiceImpl implements CommodityService {

    @Autowired
    private BreakRecordMapper breakRecordMapper;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    private static final String PULLED_KEY="pulled_ids";
    @Override
    public  List<BreakRecordPO> getBreakRecords(String batchNo, String platform, Integer count) {
        Set<Object> pulledIds = redisTemplate.opsForSet().members(PULLED_KEY);
        if (pulledIds==null) throw new RuntimeException("服务器内部错误，请稍后重试");
        long[] array = pulledIds.stream().mapToLong(value -> Long.parseLong(value.toString())).toArray();
        List<BreakRecordPO> breakRecordPOS = breakRecordMapper.selectBy(batchNo, platform,array, count);
        long[] luaArgs = breakRecordPOS.stream().mapToLong(BreakRecordPO::getId).toArray();
        DefaultRedisScript<String > script = new DefaultRedisScript<>("""
                --拉取过的数据id
                local values=ARGV[1]
                values=values:gsub("%[", ""):gsub("%]", "")
                
                --过期时间
                local exp=ARGV[2]
                --key
                local pulledKey=KEYS[1]
                --遍历插入
                for element in values:gmatch("[^,%s]+") do
                    redis.call('SADD',pulledKey,element)
                end
                --设置过期时间
                redis.call('EXPIRE',pulledKey,exp,'NX')
                return 'ok'
                """);
        redisTemplate.execute(script, Collections.singletonList(PULLED_KEY),luaArgs,Duration.ofMinutes(30).getSeconds());
        return breakRecordPOS;
    }
}
