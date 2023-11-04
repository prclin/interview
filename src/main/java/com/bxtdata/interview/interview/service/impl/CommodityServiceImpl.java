package com.bxtdata.interview.interview.service.impl;

import com.bxtdata.interview.interview.mapper.BreakRecordMapper;
import com.bxtdata.interview.interview.pojo.po.BreakRecordPO;
import com.bxtdata.interview.interview.service.CommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommodityServiceImpl implements CommodityService {

    public static final String PULLED_KEY = "pulled_ids";
    public static final String EXPIRED_PULLED_KEY = "expired_pulled_ids";
    @Autowired
    private BreakRecordMapper breakRecordMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public List<BreakRecordPO> getBreakRecords(String batchNo, String platform, Integer count) {
        List<BreakRecordPO> breakRecordPOS;
        //开启事务
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            //拉取
            breakRecordPOS = breakRecordMapper.selectForUpdateBy(batchNo, platform, count);
            long[] luaArgs = breakRecordPOS.stream().mapToLong(BreakRecordPO::getId).toArray();

            //设被拉取的破价链接为pending
            breakRecordMapper.BatchUpdateStateById(luaArgs, 1, 0);
            //pending key存入redis
            DefaultRedisScript<Boolean> script = new DefaultRedisScript<>("""
                    local lockKey=KEYS[1]
                    --获取锁
                    local lockResult = redis.call("GET",lockKey)
                                        
                    --没获取到锁，直接返回
                    if not lockResult then
                    	return false
                    end
                                  
                    --刚拉取过的数据id
                    local values=ARGV[1]
                    values=values:gsub("%[", ""):gsub("%]", "")
                                        
                    --被拉取数据集合key值
                    local pulledKey=KEYS[2]
                    --遍历插入
                    for value in values:gmatch("[^,%s]+") do
                        redis.call('SADD',pulledKey,value)
                    end                   
                    return true
                    """, Boolean.class);

            if (luaArgs.length == 0) return new ArrayList<>();

            boolean succeed = false;
            List<String> keys = new ArrayList<>();
            keys.add(EXPIRED_PULLED_KEY);
            keys.add(PULLED_KEY);

            //10次重试机会
            for (int i = 0; i < 10; i++) {
                succeed = Boolean.TRUE.equals(redisTemplate.execute(script, keys, luaArgs));
                if (succeed) break;
            }
            //成功则提交
            if (succeed) transactionManager.commit(transaction);
            else {
                //拉取失败
                transactionManager.rollback(transaction);
                return new ArrayList<>();
            }
        } catch (Exception e) {
            //回滚
            transactionManager.rollback(transaction);
            throw new RuntimeException(e);
        }

        return breakRecordPOS;
    }
}
