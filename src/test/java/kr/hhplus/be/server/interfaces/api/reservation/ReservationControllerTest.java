package kr.hhplus.be.server.interfaces.api.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.queue.WaitingQueueService;
import kr.hhplus.be.server.domain.reservation.ReservationCommand;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.model.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.interfaces.api.reservation.request.ReservationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static kr.hhplus.be.server.domain.reservation.model.ReservationStatus.PAYMENT_PENDING;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WaitingQueueService waitingQueueService;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    void 특정_좌석을_예약할_때_사용자_아이디가_존재하지_않으면_MethodArgumentNotValidException을_반환한다() throws Exception {
        // given
        ReservationRequest.Create request = ReservationRequest.Create.builder()
                .concertId(2L)
                .scheduleId(3L)
                .seatId(4L)
                .build();

        // when, then
        mockMvc.perform(
                        post("/api/v1/reservations")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Queue-Token", "token")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("사용자 아이디는 필수입니다."));
    }

    @Test
    void 특정_좌석을_예약할_때_콘서트_아이디가_존재하지_않으면_MethodArgumentNotValidException을_반환한다() throws Exception {
        // given
        ReservationRequest.Create request = ReservationRequest.Create.builder()
                .userId(1L)
                .scheduleId(3L)
                .seatId(4L)
                .build();

        // when, then
        mockMvc.perform(
                        post("/api/v1/reservations")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Queue-Token", "token")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("콘서트 아이디는 필수입니다."));
    }

    @Test
    void 특정_좌석을_예약할_때_일정_아이디가_존재하지_않으면_MethodArgumentNotValidException을_반환한다() throws Exception {
        // given
        ReservationRequest.Create request = ReservationRequest.Create.builder()
                .userId(1L)
                .concertId(2L)
                .seatId(4L)
                .build();

        // when, then
        mockMvc.perform(
                        post("/api/v1/reservations")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Queue-Token", "token")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("일정 아이디는 필수입니다."));
    }

    @Test
    void 특정_좌석을_예약할_때_좌석_아이디가_존재하지_않으면_MethodArgumentNotValidException을_반환한다() throws Exception {
        // given
        ReservationRequest.Create request = ReservationRequest.Create.builder()
                .userId(1L)
                .concertId(2L)
                .scheduleId(3L)
                .build();

        // when, then
        mockMvc.perform(
                        post("/api/v1/reservations")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Queue-Token", "token")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("좌석 아이디는 필수입니다."));
    }

    @Test
    void 특정_좌석을_예약하면_예약을_생성하고_생성된_예약_정보를_반환한다() throws Exception {
        // given
        Long id = 1L;
        Long userId = 2L;
        Long concertId = 3L;
        Long scheduleId = 4L;
        Long seatId = 5L;
        int paymentAmount = 130000;
        ReservationStatus status = PAYMENT_PENDING;

        ReservationRequest.Create request = ReservationRequest.Create.builder()
                .userId(userId)
                .concertId(concertId)
                .scheduleId(scheduleId)
                .seatId(seatId)
                .build();

        ReservationInfo.Create response = ReservationInfo.Create.builder()
                .id(id)
                .userId(userId)
                .concertId(concertId)
                .scheduleId(scheduleId)
                .seatId(seatId)
                .paymentAmount(paymentAmount)
                .status(status)
                .build();
        when(reservationService.reserve(any(ReservationCommand.Create.class))).thenReturn(response);

        // when, then
        mockMvc.perform(
                        post("/api/v1/reservations")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Queue-Token", "token")
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.concertId").value(concertId.toString()))
                .andExpect(jsonPath("$.scheduleId").value(scheduleId.toString()))
                .andExpect(jsonPath("$.seatId").value(seatId.toString()))
                .andExpect(jsonPath("$.paymentAmount").value(Integer.toString(paymentAmount)))
                .andExpect(jsonPath("$.status").value(status.toString()));
    }

}