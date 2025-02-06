package kr.hhplus.be.server.infrastructure.queue;

import kr.hhplus.be.server.common.time.TimeProvider;
import kr.hhplus.be.server.domain.queue.WaitingQueueWriter;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisWaitingQueueWriter implements WaitingQueueWriter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TimeProvider timeProvider;

    private static final String WAITING_QUEUE_KEY = "waiting-queues";
    private static final String ACTIVE_QUEUE_KEY = "active-queues";
    private static final String QUEUE_USER_KEY_PREFIX = "queue-user:";

    @Override
    public WaitingQueue save(WaitingQueue waitingQueue) {
        long timestamp = timeProvider.getCurrentTimestampInMillisKST();
        redisTemplate.opsForZSet().add(WAITING_QUEUE_KEY, waitingQueue.getToken(), timestamp);
        redisTemplate.opsForHash().put(QUEUE_USER_KEY_PREFIX + waitingQueue.getToken(), "userId", waitingQueue.getUserId());
        return waitingQueue;
    }

    @Override
    public int activateWaitingQueues(int activateSize) {
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().popMin(WAITING_QUEUE_KEY, activateSize);
        typedTuples.forEach((it) -> {
            redisTemplate.opsForSet().add(ACTIVE_QUEUE_KEY, it.getValue());
            redisTemplate.expire(ACTIVE_QUEUE_KEY, Duration.ofMinutes(10));
        });
        return activateSize;
    }

    @Override
    public void expire(WaitingQueue waitingQueue) {
        redisTemplate.opsForSet().remove(ACTIVE_QUEUE_KEY, waitingQueue.getToken());
        redisTemplate.delete(QUEUE_USER_KEY_PREFIX + waitingQueue.getToken());
    }

}
