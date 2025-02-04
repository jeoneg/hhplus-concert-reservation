package kr.hhplus.be.server.interfaces.api.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.application.payment.request.PaymentCriteria;
import kr.hhplus.be.server.application.payment.response.PaymentResult;
import kr.hhplus.be.server.domain.queue.WaitingQueueService;
import kr.hhplus.be.server.interfaces.api.payment.request.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WaitingQueueService waitingQueueService;

    @MockitoBean
    private PaymentFacade paymentFacade;

    @Test
    void 특정_예약_정보를_결제할_때_사용자_아이디가_존재하지_않으면_MethodArgumentNotValidException을_반환한다() throws Exception {
        // given
        Long userId = 1L;
        Long reservationId = 2L;
        PaymentRequest.Create request = PaymentRequest.Create.builder()
                .reservationId(reservationId)
                .build();

        // when, then
        mockMvc.perform(
                        post("/api/v1/payments")
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
    void 특정_예약_정보를_결제할_때_예약_아이디가_존재하지_않으면_MethodArgumentNotValidException을_반환한다() throws Exception {
        // given
        Long userId = 1L;
        PaymentRequest.Create request = PaymentRequest.Create.builder()
                .userId(userId)
                .build();

        // when, then
        mockMvc.perform(
                        post("/api/v1/payments")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Queue-Token", "token")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("예약 아이디는 필수입니다."));
    }

    @Test
    void 특정_예약_정보를_결제할_때_결제에_성공하면_결제_정보를_반환한다() throws Exception {
        // given
        Long userId = 1L;
        Long reservationId = 2L;
        PaymentRequest.Create request = PaymentRequest.Create.builder()
                .userId(userId)
                .reservationId(reservationId)
                .build();

        PaymentResult.Create response = PaymentResult.Create.builder()
                .userId(userId)
                .reservationId(reservationId)
                .build();
        when(paymentFacade.payment(any(PaymentCriteria.Create.class))).thenReturn(response);

        // when, then
        mockMvc.perform(
                        post("/api/v1/payments")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Queue-Token", "token")
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.reservationId").value(reservationId.toString()));
    }

}