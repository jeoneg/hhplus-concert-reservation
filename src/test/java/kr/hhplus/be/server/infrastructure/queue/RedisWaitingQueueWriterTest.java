package kr.hhplus.be.server.infrastructure.queue;

import kr.hhplus.be.server.common.time.SystemTimeProvider;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.UUID;

import static kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus.ACTIVATED;
import static kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
class RedisWaitingQueueWriterTest {

    @Autowired
    private RedisWaitingQueueWriter sut;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SystemTimeProvider timeProvider;

    private static final String WAITING_QUEUE_KEY = "waiting_queues";
    private static final String ACTIVE_QUEUE_KEY = "active_queues";
    private static final String QUEUE_USER_KEY_PREFIX = "queue-user:";

    @BeforeEach
    void setup() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void 대기열을_대기_대기열에_저장하면_대기열_정보와_사용자_정보가_저장된다() {
        // given
        Long userId = 1L;
        String token = UUID.randomUUID().toString();
        WaitingQueueStatus status = WaitingQueueStatus.WAITING;
        WaitingQueue waitingQueue = WaitingQueue.builder()
            .userId(userId)
            .token(token)
            .status(status)
            .build();

        // when
        WaitingQueue result = sut.save(waitingQueue);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getToken()).isEqualTo(token);
        assertThat(result.getStatus()).isEqualTo(status);

        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().rangeWithScores(WAITING_QUEUE_KEY, 0, -1);
        assertThat(typedTuples).hasSize(1);
        for (ZSetOperations.TypedTuple<Object> tuple: typedTuples) {
            String resultToken = (String) tuple.getValue();
            assertThat(token).isEqualTo(resultToken);

            log.info("score: {}", timeProvider.fromEpochMilliKST(tuple.getScore().longValue()));
        }

        Long resultUserId = (Long) redisTemplate.opsForHash().get(QUEUE_USER_KEY_PREFIX + token, "userId");
        assertThat(userId).isEqualTo(resultUserId);
    }

    @Test
    void 대기_대기열에서_100개를_활성화_시키면_활성_대기열로_100개가_저장된다() {
        // given
        int activateCount = 100;
        for (int i = 0; i < activateCount; i++) {
            sut.save(createWaitingQueue((long) i, UUID.randomUUID().toString(), WAITING));
        }

        // when
        sut.activateWaitingQueues(activateCount);

        // then
        Set<Object> result = redisTemplate.opsForSet().members(ACTIVE_QUEUE_KEY);
        assertThat(result).hasSize(100);
    }

    @Test
    void 활성_대기열에_존재하는_대기열_정보를_만료시키면_대기열_정보가_삭제된다() {
        // given
        Long userId = 1L;
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForSet().add(ACTIVE_QUEUE_KEY, token);

        // when
        sut.expire(createWaitingQueue(userId, token, ACTIVATED));

        //then
        Boolean result = redisTemplate.opsForSet().isMember(ACTIVE_QUEUE_KEY, token);
        assertThat(result).isFalse();
    }

    private WaitingQueue createWaitingQueue(Long userId, String token, WaitingQueueStatus status) {
        return WaitingQueue.builder()
            .userId(userId)
            .token(token)
            .status(status)
            .build();
    }

}