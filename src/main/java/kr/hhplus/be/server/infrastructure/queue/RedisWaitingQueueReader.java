package kr.hhplus.be.server.infrastructure.queue;

import kr.hhplus.be.server.domain.queue.WaitingQueueReader;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus.ACTIVATED;
import static kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus.WAITING;

@Component
@RequiredArgsConstructor
public class RedisWaitingQueueReader implements WaitingQueueReader {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String WAITING_QUEUE_KEY = "waiting-queues";
    private static final String ACTIVE_QUEUE_KEY = "active-queues";
    private static final String QUEUE_USER_KEY_PREFIX = "queue-user:";

    @Override
    public Optional<WaitingQueue> findByToken(String token) {
        Long rank = redisTemplate.opsForZSet().rank(WAITING_QUEUE_KEY, token);
        if (rank != null) {
            return createWaitingQueue(token, WAITING, rank);
        }

        if (redisTemplate.opsForSet().isMember(ACTIVE_QUEUE_KEY, token)) {
            return createWaitingQueue(token, ACTIVATED, 0L);
        }

        return Optional.empty();
    }

    private Optional<WaitingQueue> createWaitingQueue(String token, WaitingQueueStatus status, Long waitingNumber) {
        Long userId = (Long) redisTemplate.opsForHash().get(QUEUE_USER_KEY_PREFIX + token, "userId");
        WaitingQueue waitingQueue = WaitingQueue.builder()
            .userId(userId)
            .token(token)
            .status(status)
            .waitingNumber(waitingNumber)
            .build();
        return Optional.of(waitingQueue);
    }

}
