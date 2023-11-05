package com.bxtdata.interview.interview.listener;

import com.bxtdata.interview.interview.mapper.BreakRecordMapper;
import com.bxtdata.interview.interview.service.impl.CommodityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private BreakRecordMapper breakRecordMapper;


    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }


    @Override
    protected void doRegister(RedisMessageListenerContainer listenerContainer) {
        super.doRegister(listenerContainer);
        //初始化expired key
        redisTemplate.opsForValue().set(CommodityServiceImpl.EXPIRED_PULLED_KEY, 1);
        redisTemplate.expire(CommodityServiceImpl.EXPIRED_PULLED_KEY, Duration.ofMinutes(2));
        breakRecordMapper.BatchUpdateState(0, 1);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        //是不是监听的key
        if (!CommodityServiceImpl.EXPIRED_PULLED_KEY.equals(expiredKey)) return;
        //获取未处理破价链接
        Set<Object> members = redisTemplate.opsForSet().members(CommodityServiceImpl.PULLED_KEY);
        if (members != null && !members.isEmpty()) {
            long[] array = members.stream().mapToLong(value -> Long.parseLong(value.toString())).toArray();
            breakRecordMapper.BatchUpdateStateById(array, 0, 1);
        }
        //删除缓存中破价链接
        redisTemplate.delete(CommodityServiceImpl.PULLED_KEY);
        redisTemplate.opsForValue().set(CommodityServiceImpl.EXPIRED_PULLED_KEY, 1);
        redisTemplate.expire(CommodityServiceImpl.EXPIRED_PULLED_KEY, Duration.ofMinutes(2));
    }

}
