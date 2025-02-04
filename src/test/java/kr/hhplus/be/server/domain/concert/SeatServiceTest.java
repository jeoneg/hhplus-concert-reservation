package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.common.time.TimeProvider;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatInfo;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.server.common.exception.ErrorMessage.*;
import static kr.hhplus.be.server.domain.concert.model.SeatStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @InjectMocks
    private SeatService sut;

    @Mock
    private ConcertScheduleReader concertScheduleReader;

    @Mock
    private SeatReader seatReader;

    @Mock
    private TimeProvider timeProvider;

    @Test
    void 특정_일정의_좌석_목록을_조회할_때_일정이_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long scheduleId = 999L;
        Long placeId = 1L;
        when(concertScheduleReader.findById(scheduleId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.getSeats(scheduleId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(SCHEDULE_NOT_FOUND.getMessage());

        verify(seatReader, never()).findAllByPlaceId(placeId);
    }

    @Test
    void 특정_일정의_좌석_목록을_조회할_때_일정이_존재하면_좌석_목록을_반환한다() {
        // given
        Long scheduleId = 1L;
        Long concertId = 2L;
        Long placeId = 3L;
        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .id(scheduleId)
                .concertId(concertId)
                .placeId(placeId)
                .build();
        List<Seat> seats = List.of(
                createSeat(1L, placeId, AVAILABLE, null, 130000),
                createSeat(2L, placeId, TEMPORARY_RESERVED, null, 130000),
                createSeat(3L, placeId, RESERVED, null, 130000)
        );

        when(concertScheduleReader.findById(scheduleId)).thenReturn(Optional.of(concertSchedule));
        when(seatReader.findAllByPlaceId(placeId)).thenReturn(seats);

        // when
        List<SeatInfo.GetSeat> result = sut.getSeats(scheduleId);

        // then
        assertThat(result).hasSize(3)
                .extracting("id", "placeId", "status", "price")
                .containsExactlyInAnyOrder(
                        tuple(1L, placeId, AVAILABLE, 130000),
                        tuple(2L, placeId, TEMPORARY_RESERVED, 130000),
                        tuple(3L, placeId, RESERVED, 130000)
                );

        verify(seatReader, times(1)).findAllByPlaceId(placeId);
    }

    @Test
    void 좌석_임시_배정_만료_여부를_확인할_때_좌석이_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long id = 999L;
        when(seatReader.findById(id)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.checkExpiredTemporaryReserved(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(SEAT_NOT_FOUND.getMessage());

        verify(seatReader, times(1)).findById(id);
    }

    @Test
    void 좌석_임시_배정_만료_여부를_확인할_때_좌석_임시_배정_시간이_만료되었으면_IllegalStateException을_반환한다() {
        // given
        Long id = 1L;
        LocalDateTime now = LocalDateTime.now();
        when(seatReader.findById(id)).thenReturn(Optional.of(createSeat(1L, 2L, TEMPORARY_RESERVED, now.minusMinutes(1), 130000)));
        when(timeProvider.now()).thenReturn(now);

        // when, then
        assertThatThrownBy(() -> sut.checkExpiredTemporaryReserved(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(SEAT_TEMPORARY_RESERVED_EXPIRED.getMessage());

        verify(seatReader, times(1)).findById(id);
    }

    private Seat createSeat(Long id, Long placeId, SeatStatus status, LocalDateTime expiredAt, int price) {
        return Seat.builder()
                .id(id)
                .placeId(placeId)
                .status(status)
                .expiredAt(expiredAt)
                .price(price)
                .build();
    }

}