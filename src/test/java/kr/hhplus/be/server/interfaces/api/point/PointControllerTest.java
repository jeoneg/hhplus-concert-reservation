package kr.hhplus.be.server.interfaces.api.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.model.PointInfo;
import kr.hhplus.be.server.domain.queue.WaitingQueueService;
import kr.hhplus.be.server.interfaces.api.point.request.PointRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WaitingQueueService waitingQueueService;

    @MockitoBean
    private PointService pointService;

    @Test
    void 특정_사용자의_포인트를_조회하면_포인트_잔액을_반환한다() throws Exception {
        // given
        Long userId = 1L;
        int balance = 10000;

        PointInfo.GetPoint response = PointInfo.GetPoint.builder()
                .userId(userId)
                .balance(balance)
                .build();
        when(pointService.getPoint(userId)).thenReturn(response);

        // when, then
        mockMvc.perform(
                        get("/api/v1/points")
                                .param("userId", String.valueOf(userId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.balance").value(Integer.toString(balance)));
    }

    @Test
    void 특정_사용자의_포인트를_충전할_때_사용자_아이디가_존재하지_않으면_MethodArgumentNotValidException을_반환한다() throws Exception {
        // given
        Long amount = 10000L;
        PointRequest.Charge request = PointRequest.Charge.builder()
                .amount(amount)
                .build();

        // when, then
        mockMvc.perform(
                        post("/api/v1/points/charge")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("사용자 아이디는 필수입니다."));
    }

    @Test
    void 특정_사용자의_포인트를_충전할_때_충전_금액이_존재하지_않으면_MethodArgumentNotValidException을_반환한다() throws Exception {
        // given
        Long userId = 1L;
        PointRequest.Charge request = PointRequest.Charge.builder()
                .userId(userId)
                .build();

        // when, then
        mockMvc.perform(
                        post("/api/v1/points/charge")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("충전 금액은 필수입니다."));
    }

    @Test
    void 특정_사용자의_포인트를_충전하면_포인트를_충전하고_포인트_잔액을_반환한다() throws Exception {
        // given
        Long userId = 1L;
        Long amount = 10000L;
        int balance = 20000;
        PointRequest.Charge request = PointRequest.Charge.builder()
                .userId(userId)
                .amount(amount)
                .build();

        PointInfo.Charge response = PointInfo.Charge.builder()
                .userId(userId)
                .balance(balance)
                .build();
        when(pointService.charge(any(PointCommand.Charge.class))).thenReturn(response);

        // when, then
        mockMvc.perform(
                        post("/api/v1/points/charge")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.balance").value(Integer.toString(balance)));
    }

}