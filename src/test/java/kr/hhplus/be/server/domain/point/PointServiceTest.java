package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.BadRequestException;
import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.entity.PointHistory;
import kr.hhplus.be.server.domain.point.model.PointInfo;
import kr.hhplus.be.server.domain.user.UserReader;
import kr.hhplus.be.server.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kr.hhplus.be.server.common.exception.ErrorMessage.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService sut;

    @Mock
    private UserReader userReader;

    @Mock
    private PointReader pointReader;

    @Mock
    private PointWriter pointWriter;

    @Mock
    private PointHistoryWriter pointHistoryWriter;

    @Test
    void 특정_사용자의_포인트를_조회할_때_사용자가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long userId = 999L;
        when(userReader.findById(userId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.getPoint(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());

        verify(userReader, times(1)).findById(userId);
    }

    @Test
    void 특정_사용자의_포인트를_조회할_때_포인트_정보가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long userId = 1L;
        when(userReader.findById(userId)).thenReturn(Optional.of(createUser(userId)));
        when(pointReader.findByUserId(userId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.getPoint(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(POINT_NOT_FOUND.getMessage());

        verify(userReader, times(1)).findById(userId);
        verify(pointReader, times(1)).findByUserId(userId);
    }

    @Test
    void 특정_사용자의_포인트를_조회할_때_포인트_정보가_존재하면_포인트를_반환한다() {
        // given
        Long id = 1L;
        Long userId = 2L;
        int balance = 10000;
        when(userReader.findById(userId)).thenReturn(Optional.of(createUser(userId)));
        when(pointReader.findByUserId(userId)).thenReturn(Optional.of(createPoint(id, userId, balance)));

        // when
        PointInfo.GetPoint result = sut.getPoint(userId);

        // then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.balance()).isEqualTo(balance);

        verify(userReader, times(1)).findById(userId);
        verify(pointReader, times(1)).findByUserId(userId);
    }

    @Test
    void 특정_사용자의_포인트를_충전할_때_포인트가_0보다_작거나_같으면_BadRequestException을_반환한다() {
        // given
        Long userId = 1L;
        int amount = 0;

        // when, then
        assertThatThrownBy(() -> sut.charge(PointCommand.Charge.of(userId, amount)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(POINT_CHARGE_AMOUNT_INVALID.getMessage());
    }

    @Test
    void 특정_사용자의_포인트를_충전할_때_사용자_정보가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long userId = 999L;
        int amount = 10000;
        when(userReader.findById(userId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.charge(PointCommand.Charge.of(userId, amount)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());

        verify(userReader, times(1)).findById(userId);
    }

    @Test
    void 특정_사용자의_포인트를_충전할_때_포인트_정보가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long userId = 1L;
        int amount = 10000;
        when(userReader.findById(userId)).thenReturn(Optional.of(createUser(userId)));
        when(pointReader.findByUserIdWithLock(userId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.charge(PointCommand.Charge.of(userId, amount)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(POINT_NOT_FOUND.getMessage());

        verify(userReader, times(1)).findById(userId);
        verify(pointReader, times(1)).findByUserIdWithLock(userId);
    }

    @Test
    void 특정_사용자의_포인트를_충전할_때_10000포인트를_충전하면_잔액은_20000포인트가_된다() {
        // given
        Long id = 1L;
        Long userId = 2L;
        int amount = 10000;
        int balance = 10000;
        when(userReader.findById(userId)).thenReturn(Optional.of(createUser(userId)));
        when(pointReader.findByUserIdWithLock(userId)).thenReturn(Optional.of(createPoint(id, userId, balance)));
        when(pointWriter.save(any(Point.class))).thenReturn(createPoint(id, userId, balance + amount));

        // when
        PointInfo.Charge result = sut.charge(PointCommand.Charge.of(userId, amount));

        // then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.balance()).isEqualTo(balance + amount);

        verify(userReader, times(1)).findById(userId);
        verify(pointReader, times(1)).findByUserIdWithLock(userId);
        verify(pointWriter, times(1)).save(any(Point.class));
        verify(pointHistoryWriter, times(1)).save(any(PointHistory.class));
    }

    @Test
    void 특정_사용자의_포인트를_사용할_때_포인트가_0보다_작거나_같으면_BadRequestException을_반환한다() {
        // given
        Long userId = 1L;
        int amount = 0;

        // when, then
        assertThatThrownBy(() -> sut.use(PointCommand.Use.of(userId, amount)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(POINT_USE_AMOUNT_INVALID.getMessage());
    }

    @Test
    void 특정_사용자의_포인트를_사용할_때_사용자_정보가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long userId = 999L;
        int amount = 10000;
        when(userReader.findById(userId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.use(PointCommand.Use.of(userId, amount)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());

        verify(userReader, times(1)).findById(userId);
    }

    @Test
    void 특정_사용자의_포인트를_사용할_때_포인트_정보가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long userId = 1L;
        int amount = 10000;
        when(userReader.findById(userId)).thenReturn(Optional.of(createUser(userId)));
        when(pointReader.findByUserIdWithLock(userId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.use(PointCommand.Use.of(userId, amount)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(POINT_NOT_FOUND.getMessage());

        verify(userReader, times(1)).findById(userId);
        verify(pointReader, times(1)).findByUserIdWithLock(userId);
    }

    @Test
    void 특정_사용자의_포인트를_사용할_때_20000포인트를_사용하면_잔액은_10000포인트가_된다() {
        // given
        Long id = 1L;
        Long userId = 2L;
        int amount = 20000;
        int balance = 30000;
        when(userReader.findById(userId)).thenReturn(Optional.of(createUser(userId)));
        when(pointReader.findByUserIdWithLock(userId)).thenReturn(Optional.of(createPoint(id, userId, balance)));
        when(pointWriter.save(any(Point.class))).thenReturn(createPoint(id, userId, balance - amount));

        // when
        PointInfo.Use result = sut.use(PointCommand.Use.of(userId, amount));

        // then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.balance()).isEqualTo(balance - amount);

        verify(userReader, times(1)).findById(userId);
        verify(pointReader, times(1)).findByUserIdWithLock(userId);
        verify(pointWriter, times(1)).save(any(Point.class));
        verify(pointHistoryWriter, times(1)).save(any(PointHistory.class));
    }

    private User createUser(Long userId) {
        return User.builder()
                .id(userId)
                .build();
    }

    private Point createPoint(Long id, Long userId, int balance) {
        return Point.builder()
                .id(id)
                .userId(userId)
                .balance(balance)
                .build();
    }

}