package kr.hhplus.be.server.infrastructure.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisLockRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public Boolean tryLock(String key, long leaseTime, TimeUnit timeUnit) {
        return redisTemplate
            .opsForValue()
            .setIfAbsent(key, "lock", leaseTime, timeUnit);
    }

    public Boolean unlock(String key) {
        return redisTemplate.delete(key);
    }

}
