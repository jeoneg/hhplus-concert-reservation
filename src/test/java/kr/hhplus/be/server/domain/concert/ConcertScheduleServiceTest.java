package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.ConcertScheduleInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.server.common.exception.ErrorMessage.CONCERT_NOT_FOUND;
import static kr.hhplus.be.server.common.exception.ErrorMessage.SCHEDULE_LIST_EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcertScheduleServiceTest {

    @InjectMocks
    private ConcertScheduleService sut;

    @Mock
    private ConcertReader concertReader;

    @Mock
    private ConcertScheduleReader concertScheduleReader;

    @Test
    void 특정_콘서트_일정을_조회할_때_콘서트가_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long concertId = 999L;

        // when, then
        assertThatThrownBy(() -> sut.getSchedules(concertId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(CONCERT_NOT_FOUND.getMessage());

        verify(concertReader, times(1)).findById(concertId);
        verify(concertScheduleReader, times(0)).findAllByConcertId(concertId);
    }

    @Test
    void 특정_콘서트_일정을_조회할_때_일정_목록이_존재하지_않으면_NotFoundException을_반환한다() {
        // given
        Long concertId = 1L;
        when(concertReader.findById(concertId)).thenReturn(Optional.of(createConcert(concertId)));
        when(concertScheduleReader.findAllByConcertId(concertId)).thenReturn(List.of());

        // when, then
        assertThatThrownBy(() -> sut.getSchedules(concertId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(SCHEDULE_LIST_EMPTY.getMessage());

        verify(concertReader, times(1)).findById(concertId);
        verify(concertScheduleReader, times(1)).findAllByConcertId(concertId);
    }

    @Test
    void 특정_콘서트_일정을_조회할_때_일정_목록이_존재하면_일정_목록을_반환한다() {
        // given
        Long concertId = 1L;
        Long placeId = 1L;
        List<ConcertSchedule> concertSchedules = List.of(
                createConcertSchedule(1L, concertId, placeId, LocalDateTime.of(2025, 1, 1, 16, 0, 0)),
                createConcertSchedule(2L, concertId, placeId, LocalDateTime.of(2025, 1, 2, 17, 0, 0)),
                createConcertSchedule(3L, concertId, placeId, LocalDateTime.of(2025, 1, 3, 18, 0, 0))
        );
        when(concertReader.findById(concertId)).thenReturn(Optional.of(createConcert(concertId)));
        when(concertScheduleReader.findAllByConcertId(concertId)).thenReturn(concertSchedules);

        // when
        List<ConcertScheduleInfo.GetConcertSchedule> result = sut.getSchedules(concertId);

        // then
        assertThat(result).hasSize(3)
                .extracting("scheduleId", "placeId", "scheduledAt")
                .containsExactlyInAnyOrder(
                        tuple(1L, placeId, LocalDateTime.of(2025, 1, 1, 16, 0, 0)),
                        tuple(2L, placeId, LocalDateTime.of(2025, 1, 2, 17, 0, 0)),
                        tuple(3L, placeId, LocalDateTime.of(2025, 1, 3, 18, 0, 0))
                );
    }

    private Concert createConcert(Long concertId) {
        return Concert.builder()
                .id(concertId)
                .build();
    }

    private ConcertSchedule createConcertSchedule(long id, Long concertId, Long placeId, LocalDateTime scheduledAt) {
        return ConcertSchedule.builder()
                .id(id)
                .concertId(concertId)
                .placeId(placeId)
                .scheduledAt(scheduledAt)
                .build();
    }

}