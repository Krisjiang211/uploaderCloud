package com.xiangyueEducation.uploaderCloud.DTO;



import com.xiangyueEducation.uploaderCloud.Utils.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    public int setHash(String key,Object value,long time, TimeUnit unit)  {
        redisTemplate.opsForHash().put(key, UUID.getUUID(),value);
        boolean expire = redisTemplate.expire(key, time, unit);
        if (expire){
            return 1;
        }
        return 0;
    }

    public int setHashSecond(String key,Object value,long time)  {
        redisTemplate.opsForHash().put(key, UUID.getUUID(),value);
        boolean expire = redisTemplate.expire(key, time, TimeUnit.SECONDS);
        if (expire){
            return 1;
        }
        return 0;
    }
}
