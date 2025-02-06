package kr.hhplus.be.server.infrastructure.queue;

import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static kr.hhplus.be.server.common.exception.ErrorMessage.QUEUE_TOKEN_NOT_FOUND;
import static kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus.ACTIVATED;
import static kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class RedisWaitingQueueReaderTest {

    @Autowired
    private RedisWaitingQueueReader sut;

    @Autowired
    private RedisWaitingQueueWriter redisWaitingQueueWriter;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String WAITING_QUEUE_KEY = "waiting_queues";
    private static final String ACTIVE_QUEUE_KEY = "active_queues";
    private static final String QUEUE_USER_KEY_PREFIX = "queue-user:";

    @Test
    void 특정_대기열_정보를_조회할_때_대기열이_존재하지_않으면_비어있는_Optional를_반환한다() {
        // given
        String token = UUID.randomUUID().toString();

        // when
        Optional<WaitingQueue> result = sut.findByToken(token);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 특정_대기열_정보를_조회할_때_대기열이_활성화가_되지_않았고_순서가_2번째이면_상태는_WAITING이고_순서는_1L이다() {
        // given
        Long userId1 = 1L;
        String token1 = UUID.randomUUID().toString();
        WaitingQueueStatus status1 = WAITING;
        redisWaitingQueueWriter.save(createWaitingQueue(userId1, token1, WAITING));

        Long userId2 = 2L;
        String token2 = UUID.randomUUID().toString();
        WaitingQueueStatus status2 = WAITING;
        redisWaitingQueueWriter.save(createWaitingQueue(userId2, token2, status2));

        // when
        WaitingQueue result = sut.findByToken(token2)
            .orElseThrow(() -> new NotFoundException(QUEUE_TOKEN_NOT_FOUND.getMessage()));

        // then
        assertThat(result.getStatus()).isEqualTo(WAITING);
        assertThat(result.getWaitingNumber()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(userId2);
        assertThat(result.getToken()).isEqualTo(token2);
    }

    @Test
    void 특정_대기열_정보를_조회할_때_대기열이_활성화가_되었으면_상태는_ACTIVATED이고_순서는_0L이다() {
        // given
        Long userId = 1L;
        String token = UUID.randomUUID().toString();
        WaitingQueueStatus status = ACTIVATED;

        redisTemplate.opsForSet().add(ACTIVE_QUEUE_KEY, token);
        redisTemplate.opsForHash().put(QUEUE_USER_KEY_PREFIX + token, "userId", userId);

        // when
        WaitingQueue result = sut.findByToken(token)
            .orElseThrow(() -> new NotFoundException(QUEUE_TOKEN_NOT_FOUND.getMessage()));

        // then
        assertThat(result.getStatus()).isEqualTo(status);
        assertThat(result.getWaitingNumber()).isEqualTo(0L);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getToken()).isEqualTo(token);
    }

    private WaitingQueue createWaitingQueue(Long userId, String token, WaitingQueueStatus status) {
        return WaitingQueue.builder()
            .userId(userId)
            .token(token)
            .status(status)
            .build();
    }

}