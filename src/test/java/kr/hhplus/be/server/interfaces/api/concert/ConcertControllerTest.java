package kr.hhplus.be.server.interfaces.api.concert;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.concert.ConcertScheduleService;
import kr.hhplus.be.server.domain.concert.ConcertService;
import kr.hhplus.be.server.domain.concert.SeatService;
import kr.hhplus.be.server.domain.concert.model.ConcertScheduleInfo;
import kr.hhplus.be.server.domain.concert.model.SeatInfo;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import kr.hhplus.be.server.domain.queue.WaitingQueueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.domain.concert.model.SeatStatus.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ConcertController.class)
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WaitingQueueService waitingQueueService;

    @MockitoBean
    private ConcertService concertService;

    @MockitoBean
    private ConcertScheduleService concertScheduleService;

    @MockitoBean
    private SeatService seatService;

    @Test
    void 특정_콘서트_일정을_조회하면_일정_목록을_반환한다() throws Exception {
        // given
        Long concertId = 1L;
        List<ConcertScheduleInfo.GetConcertSchedule> response = List.of(
                createConcertSchedule(1L, 1L, LocalDateTime.of(2025, 1, 1, 16, 0, 0)),
                createConcertSchedule(2L, 1L, LocalDateTime.of(2025, 1, 2, 17, 0, 0)),
                createConcertSchedule(3L, 1L, LocalDateTime.of(2025, 1, 3, 18, 0, 0))
        );
        when(concertScheduleService.getSchedules(concertId)).thenReturn(response);

        // when, then
        mockMvc.perform(
                        get("/api/v1/concerts/{concertId}/schedules", concertId)
                                .header("Queue-Token", "token")
                )
                .andDo(print())
                .andExpect(jsonPath("$[0].scheduleId").value("1"))
                .andExpect(jsonPath("$[0].placeId").value("1"))
                .andExpect(jsonPath("$[0].scheduledAt").value("2025-01-01T16:00:00"))
                .andExpect(jsonPath("$[1].scheduleId").value("2"))
                .andExpect(jsonPath("$[1].placeId").value("1"))
                .andExpect(jsonPath("$[1].scheduledAt").value("2025-01-02T17:00:00"))
                .andExpect(jsonPath("$[2].scheduleId").value("3"))
                .andExpect(jsonPath("$[2].placeId").value("1"))
                .andExpect(jsonPath("$[2].scheduledAt").value("2025-01-03T18:00:00"));
    }

    @Test
    void 특정_일정의_좌석_목록을_조회하면_좌석_목록을_반환한다() throws Exception {
        // given
        Long concertId = 1L;
        Long scheduleId = 2L;

        List<SeatInfo.GetSeat> response = List.of(
                createSeat(1L, 2L, AVAILABLE, 130000),
                createSeat(2L, 2L, TEMPORARY_RESERVED, 130000),
                createSeat(3L, 2L, RESERVED, 130000)
        );
        when(seatService.getSeats(scheduleId)).thenReturn(response);

        // when, then
        mockMvc.perform(
                        get("/api/v1/concerts/{concertId}/schedules/{scheduleId}/seats", concertId, scheduleId)
                                .header("Queue-Token", "token")
                )
                .andDo(print())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].availableYn").value("Y"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].availableYn").value("N"))
                .andExpect(jsonPath("$[2].id").value("3"))
                .andExpect(jsonPath("$[2].availableYn").value("N"));
    }

    private ConcertScheduleInfo.GetConcertSchedule createConcertSchedule(long scheduleId, long placeId, LocalDateTime scheduledAt) {
        return ConcertScheduleInfo.GetConcertSchedule.builder()
                .scheduleId(scheduleId)
                .placeId(placeId)
                .scheduledAt(scheduledAt)
                .build();
    }

    private SeatInfo.GetSeat createSeat(long id, long placeId, SeatStatus status, int price) {
        return SeatInfo.GetSeat.builder()
                .id(id)
                .placeId(placeId)
                .status(status)
                .price(price)
                .build();
    }

}