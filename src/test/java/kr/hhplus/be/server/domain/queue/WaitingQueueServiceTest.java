package kr.hhplus.be.server.domain.queue;

import kr.hhplus.be.server.common.exception.BadRequestException;
import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.common.uuid.UUIDGenerator;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus;
import kr.hhplus.be.server.domain.queue.model.WaitingQueueInfo;
import kr.hhplus.be.server.domain.user.UserReader;
import kr.hhplus.be.server.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static kr.hhplus.be.server.common.exception.ErrorMessage.*;
import static kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus.EXPIRED;
import static kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaitingQueueServiceTest {

    @InjectMocks
    private WaitingQueueService sut;

    @Mock
    private UserReader userReader;

    @Mock
    private WaitingQueueWriter waitingQueueWriter;

    @Mock
    private WaitingQueueReader waitingQueueReader;

    @Mock
    private UUIDGenerator uuidGenerator;

    @Test
    void 대기열_정보를_생성할_때_사용자_아이디가_유효하지_않으면_BadRequestException을_반환한다() {
        // given
        Long userId = 0L;

        // when, then
        assertThatThrownBy(() -> sut.createWaitingQueue(new WaitingQueueCommand.Create(userId)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(USER_ID_INVALID.getMessage());

        verify(uuidGenerator, never()).generate();
        verify(waitingQueueWriter, never()).save(any(WaitingQueue.class));
    }

    @Test
    void 대기열_정보를_생성할_때_사용자가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long userId = 999L;
        when(userReader.findById(userId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.createWaitingQueue(new WaitingQueueCommand.Create(userId)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());

        verify(uuidGenerator, never()).generate();
        verify(waitingQueueWriter, never()).save(any(WaitingQueue.class));
    }

    @Test
    void 대기열_정보를_생성할_때_사용자가_존재하면_대기열_정보를_생성한다() {
        // given
        Long id = 1L;
        Long userId = 1L;
        String token = UUID.randomUUID().toString();
        WaitingQueueStatus status = WAITING;
        when(userReader.findById(userId)).thenReturn(Optional.of(createUser(userId)));
        when(uuidGenerator.generate()).thenReturn(token);
        when(waitingQueueWriter.save(any(WaitingQueue.class))).thenReturn(createWaitingQueue(id, userId, token, status));

        // when
        WaitingQueueInfo.Create result = sut.createWaitingQueue(new WaitingQueueCommand.Create(userId));

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.token()).isEqualTo(token);
        assertThat(result.status()).isEqualTo(status);

        verify(uuidGenerator, times(1)).generate();
        verify(waitingQueueWriter, times(1)).save(any(WaitingQueue.class));
    }

    @Test
    void 특정_대기열_정보를_조회할_때_토큰값이_유효하지_않으면_BadRequestException을_반환한다() {
        // given
        String token = null;

        // when, then
        assertThatThrownBy(() -> sut.getWaitingQueue(token))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(QUEUE_TOKEN_VALUE_INVALID.getMessage());

        verify(waitingQueueReader, never()).findByToken(token);
    }

    @Test
    void 특정_대기열_정보를_조회할_때_대기열_정보가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        String token = UUID.randomUUID().toString();
        when(waitingQueueReader.findByToken(token)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.getWaitingQueue(token))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(QUEUE_TOKEN_NOT_FOUND.getMessage());

        verify(waitingQueueReader, times(1)).findByToken(token);
    }

    @Test
    void 특정_대기열을_만료할_때_대기열_정보가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        String token = "empty";
        when(waitingQueueReader.findByToken(token)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.expire(token))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(QUEUE_TOKEN_NOT_FOUND.getMessage());

        verify(waitingQueueReader, times(1)).findByToken(token);
    }

    @Test
    void 특정_대기열을_만료할_때_대기열_정보가_존재하면_대기열을_만료시킨다() {
        // given
        Long id = 1L;
        Long userId = 1L;
        String token = UUID.randomUUID().toString();
        WaitingQueueStatus status = WAITING;
        WaitingQueue waitingQueue = createWaitingQueue(id, userId, token, status);
        when(waitingQueueReader.findByToken(token)).thenReturn(Optional.of(waitingQueue));

        // when
        sut.expire(token);

        // then
        verify(waitingQueueReader, times(1)).findByToken(token);
    }

    private User createUser(Long userId) {
        return User.builder()
                .id(userId)
                .build();
    }

    private WaitingQueue createWaitingQueue(Long id, Long userId, String token, WaitingQueueStatus status) {
        return WaitingQueue.builder()
                .id(id)
                .userId(userId)
                .token(token)
                .status(status)
                .build();
    }

}