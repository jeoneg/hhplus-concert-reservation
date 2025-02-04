package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.common.time.TimeProvider;
import kr.hhplus.be.server.domain.concert.ConcertReader;
import kr.hhplus.be.server.domain.concert.ConcertScheduleReader;
import kr.hhplus.be.server.domain.concert.SeatReader;
import kr.hhplus.be.server.domain.concert.SeatWriter;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.user.UserReader;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static kr.hhplus.be.server.common.exception.ErrorMessage.*;
import static kr.hhplus.be.server.domain.concert.model.SeatStatus.*;
import static kr.hhplus.be.server.domain.reservation.model.ReservationStatus.CONFIRMED;
import static kr.hhplus.be.server.domain.reservation.model.ReservationStatus.PAYMENT_PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService sut;

    @Mock
    private UserReader userReader;

    @Mock
    private ConcertReader concertReader;

    @Mock
    private ConcertScheduleReader concertScheduleReader;

    @Mock
    private SeatReader seatReader;

    @Mock
    private SeatWriter seatWriter;

    @Mock
    private ReservationReader reservationReader;

    @Mock
    private ReservationWriter reservationWriter;

    @Mock
    private TimeProvider timeProvider;

    @Test
    void 특정_좌석을_예약할_때_사용자가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long userId = 999L;
        Long concertId = 1L;
        Long scheduleId = 2L;
        Long seatId = 3L;
        when(userReader.findById(userId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.reserve(ReservationCommand.Create.of(userId, concertId, scheduleId, seatId)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());

        verify(userReader, times(1)).findById(userId);
    }

    @Test
    void 특정_좌석을_예약할_때_콘서트가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long userId = 1L;
        Long concertId = 999L;
        Long scheduleId = 2L;
        Long seatId = 3L;
        when(userReader.findById(userId)).thenReturn(Optional.of(createUser(userId)));
        when(concertReader.findById(concertId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.reserve(ReservationCommand.Create.of(userId, concertId, scheduleId, seatId)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(CONCERT_NOT_FOUND.getMessage());

        verify(userReader, times(1)).findById(userId);
        verify(concertReader, times(1)).findById(concertId);
    }

    @Test
    void 특정_좌석을_예약할_때_일정이_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long userId = 1L;
        Long concertId = 2L;
        Long scheduleId = 999L;
        Long seatId = 3L;
        when(userReader.findById(userId)).thenReturn(Optional.of(createUser(userId)));
        when(concertReader.findById(concertId)).thenReturn(Optional.of(new Concert(concertId, "콘서트")));
        when(concertScheduleReader.findById(scheduleId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.reserve(ReservationCommand.Create.of(userId, concertId, scheduleId, seatId)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(SCHEDULE_NOT_FOUND.getMessage());

        verify(userReader, times(1)).findById(userId);
        verify(concertReader, times(1)).findById(concertId);
        verify(concertScheduleReader, times(1)).findById(scheduleId);
    }

    @Test
    void 특정_좌석을_예약할_때_좌석이_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long userId = 1L;
        Long concertId = 2L;
        Long scheduleId = 3L;
        Long seatId = 999L;
        when(userReader.findById(userId)).thenReturn(Optional.of(createUser(userId)));
        when(concertReader.findById(concertId)).thenReturn(Optional.of(new Concert(concertId, "콘서트")));
        when(concertScheduleReader.findById(scheduleId)).thenReturn(Optional.ofNullable(createSchedule(scheduleId)));
        when(seatReader.findByIdWithLock(seatId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.reserve(ReservationCommand.Create.of(userId, concertId, scheduleId, seatId)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(SEAT_NOT_FOUND.getMessage());

        verify(userReader, times(1)).findById(userId);
        verify(concertReader, times(1)).findById(concertId);
        verify(concertScheduleReader, times(1)).findById(scheduleId);
        verify(seatReader, times(1)).findByIdWithLock(seatId);
    }

    @Test
    void 특정_좌석을_예약할_때_예약할_수_없는_좌석이면_IllegalStateException을_반환한다() {
        // given
        Long userId = 1L;
        Long concertId = 2L;
        Long scheduleId = 3L;
        Long seatId = 999L;

        when(userReader.findById(userId)).thenReturn(Optional.of(createUser(userId)));
        when(concertReader.findById(concertId)).thenReturn(Optional.of(new Concert(concertId, "콘서트")));
        when(concertScheduleReader.findById(scheduleId)).thenReturn(Optional.ofNullable(createSchedule(scheduleId)));
        when(seatReader.findByIdWithLock(seatId)).thenReturn(Optional.of(createSeat(seatId, 1L, TEMPORARY_RESERVED, null, 130000)));

        // when, then
        assertThatThrownBy(() -> sut.reserve(ReservationCommand.Create.of(userId, concertId, scheduleId, seatId)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(SEAT_ALREADY_RESERVED.getMessage());

        verify(userReader, times(1)).findById(userId);
        verify(concertReader, times(1)).findById(concertId);
        verify(concertScheduleReader, times(1)).findById(scheduleId);
        verify(seatReader, times(1)).findByIdWithLock(seatId);
    }

    @Test
    void 특정_좌석을_예약할_때_예약할_수_있는_좌석이면_예약을_생성한다() {
        // given
        Long id = 1L;
        Long userId = 2L;
        Long concertId = 3L;
        Long scheduleId = 4L;
        Long seatId = 5L;
        int paymentAmount = 130000;
        ReservationStatus status = PAYMENT_PENDING;

        when(userReader.findById(userId)).thenReturn(Optional.of(createUser(userId)));
        when(concertReader.findById(concertId)).thenReturn(Optional.of(new Concert(concertId, "콘서트")));
        when(concertScheduleReader.findById(scheduleId)).thenReturn(Optional.ofNullable(createSchedule(scheduleId)));
        when(seatReader.findByIdWithLock(seatId)).thenReturn(Optional.of(createSeat(seatId, 1L, AVAILABLE, null, paymentAmount)));
        when(timeProvider.now()).thenReturn(LocalDateTime.now());
        when(reservationWriter.save(any(Reservation.class))).thenReturn(createReservation(id, userId, concertId, scheduleId, seatId, paymentAmount, status));

        // when
        ReservationInfo.Create result = sut.reserve(ReservationCommand.Create.of(userId, concertId, scheduleId, seatId));

        // then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.concertId()).isEqualTo(concertId);
        assertThat(result.scheduleId()).isEqualTo(scheduleId);
        assertThat(result.seatId()).isEqualTo(seatId);
        assertThat(result.paymentAmount()).isEqualTo(paymentAmount);
        assertThat(result.status()).isEqualTo(status);

        verify(userReader, times(1)).findById(userId);
        verify(concertReader, times(1)).findById(concertId);
        verify(concertScheduleReader, times(1)).findById(scheduleId);
        verify(seatReader, times(1)).findByIdWithLock(seatId);
        verify(seatWriter, times(1)).save(any(Seat.class));
        verify(reservationWriter, times(1)).save(any(Reservation.class));
    }

    @Test
    void 특정_예약을_조회할_때_예약이_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long id = 999L;
        when(reservationReader.findById(id)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.getReservation(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(RESERVATION_NOT_FOUND.getMessage());

        verify(reservationReader, times(1)).findById(id);
    }

    @Test
    void 특정_예약을_조회할_때_예약이_존재하면_예약을_반환한다() {
        // given
        Long id = 1L;
        Long userId = 2L;
        Long concertId = 3L;
        Long scheduleId = 4L;
        Long seatId = 5L;
        int paymentAmount = 130000;
        ReservationStatus status = PAYMENT_PENDING;
        when(reservationReader.findById(id)).thenReturn(Optional.of(createReservation(id, userId, concertId, scheduleId, seatId, paymentAmount, status)));

        // when
        ReservationInfo.GetReservation result = sut.getReservation(id);

        // then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.concertId()).isEqualTo(concertId);
        assertThat(result.scheduleId()).isEqualTo(scheduleId);
        assertThat(result.seatId()).isEqualTo(seatId);
        assertThat(result.paymentAmount()).isEqualTo(paymentAmount);
        assertThat(result.status()).isEqualTo(status);

        verify(reservationReader, times(1)).findById(id);
    }

    @Test
    void 특정_예약을_확정할_때_예약_정보가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long id = 999L;
        when(reservationReader.findById(id)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.confirm(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(RESERVATION_NOT_FOUND.getMessage());

        verify(reservationReader, times(1)).findById(id);
    }

    @Test
    void 특정_예약을_확정할_때_좌석_정보가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long id = 1L;
        Long userId = 2L;
        Long concertId = 3L;
        Long scheduleId = 4L;
        Long seatId = 5L;
        int paymentAmount = 130000;
        ReservationStatus status = PAYMENT_PENDING;
        when(reservationReader.findById(id)).thenReturn(Optional.of(createReservation(id, userId, concertId, scheduleId, seatId, paymentAmount, status)));
        when(seatReader.findById(seatId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.confirm(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(SEAT_NOT_FOUND.getMessage());

        verify(reservationReader, times(1)).findById(id);
        verify(seatReader, times(1)).findById(seatId);
    }

    @Test
    void 특정_예약을_확정하면_예약과_좌석_상태가_각각_확정_예약으로_변경된다() {
        // given
        Long id = 1L;
        Long userId = 2L;
        Long concertId = 3L;
        Long scheduleId = 4L;
        Long seatId = 5L;
        int paymentAmount = 130000;
        ReservationStatus status = PAYMENT_PENDING;

        Reservation reservation = createReservation(id, userId, concertId, scheduleId, seatId, paymentAmount, status);
        Seat seat = createSeat(seatId, 1L, TEMPORARY_RESERVED, null, 130000);

        when(reservationReader.findById(id)).thenReturn(Optional.of(reservation));
        when(seatReader.findById(seatId)).thenReturn(Optional.of(seat));

        // when
        sut.confirm(id);

        // then
        assertThat(reservation.getStatus()).isEqualTo(CONFIRMED);
        assertThat(seat.getStatus()).isEqualTo(RESERVED);

        verify(reservationReader, times(1)).findById(id);
        verify(seatReader, times(1)).findById(seatId);
        verify(reservationWriter, times(1)).save(any(Reservation.class));
        verify(seatWriter, times(1)).save(any(Seat.class));
    }

    private User createUser(Long userId) {
        return User.builder()
                .id(userId)
                .build();
    }

    private ConcertSchedule createSchedule(Long scheduleId) {
        return ConcertSchedule.builder()
                .id(scheduleId)
                .build();
    }

    private Seat createSeat(Long seatId, Long placeId, SeatStatus status, LocalDateTime expiredAt, int price) {
        return Seat.builder()
                .id(seatId)
                .placeId(placeId)
                .status(status)
                .expiredAt(expiredAt)
                .price(price)
                .build();
    }

    private Reservation createReservation(Long id, Long userId, Long concertId, Long scheduleId, Long seatId, int paymentAmount, ReservationStatus status) {
        return Reservation.builder()
                .id(id)
                .userId(userId)
                .concertId(concertId)
                .scheduleId(scheduleId)
                .seatId(seatId)
                .paymentAmount(paymentAmount)
                .status(status)
                .build();
    }

}