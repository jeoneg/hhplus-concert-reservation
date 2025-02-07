package kr.hhplus.be.server.infrastructure.queue;

import kr.hhplus.be.server.common.time.TimeProvider;
import kr.hhplus.be.server.domain.queue.WaitingQueueWriter;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<TypedTuple<Object>> tuples = redisTemplate.opsForZSet().popMin(WAITING_QUEUE_KEY, activateSize);

        if (tuples.isEmpty()) {
            return 0;
        }

        // 값 추출
        List<Object> values = tuples.stream()
            .map(TypedTuple::getValue)
            .collect(Collectors.toList());

        // Set에 일괄 추가
        redisTemplate.opsForSet().add(ACTIVE_QUEUE_KEY, values.toArray());

        // 파이프라인으로 TTL 설정
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            values.forEach(value -> {
                byte[] key = redisTemplate.getStringSerializer().serialize(QUEUE_USER_KEY_PREFIX + value);
                connection.expire(key, Duration.ofMinutes(10).getSeconds());
            });
            return null;
        });

        return values.size();
    }

    @Override
    public void expire(WaitingQueue waitingQueue) {
        redisTemplate.opsForSet().remove(ACTIVE_QUEUE_KEY, waitingQueue.getToken());
        redisTemplate.delete(QUEUE_USER_KEY_PREFIX + waitingQueue.getToken());
    }

}
