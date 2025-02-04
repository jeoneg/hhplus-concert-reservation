package kr.hhplus.be.server.interfaces.api.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.domain.queue.WaitingQueueCommand;
import kr.hhplus.be.server.domain.queue.WaitingQueueService;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus;
import kr.hhplus.be.server.domain.queue.model.WaitingQueueInfo;
import kr.hhplus.be.server.interfaces.api.queue.request.WaitingQueueRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.hhplus.be.server.common.exception.ErrorMessage.QUEUE_TOKEN_NOT_FOUND;
import static kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus.ACTIVATED;
import static kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus.WAITING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WaitingQueueController.class)
class WaitingQueueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WaitingQueueService waitingQueueService;

    @Test
    void 대기열_정보를_생성할_때_사용자_아이디가_유효하지_않으면_BadRequestException을_반환한다() throws Exception {
        // given
        Long userId = null;
        WaitingQueueRequest.Create request = new WaitingQueueRequest.Create(userId);

        // when, then
        mockMvc.perform(
                        post("/api/v1/waiting-queues")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("사용자 아이디는 필수입니다."));
    }

    @Test
    void 대기열_정보를_생성할_때_사용자_아이디가_유효하면_대기열_토큰을_생성한다() throws Exception {
        // given
        Long id = 1L;
        Long userId = 1L;
        String token = UUID.randomUUID().toString();
        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0);
        WaitingQueueRequest.Create request = new WaitingQueueRequest.Create(userId);

        WaitingQueueInfo.Create response = WaitingQueueInfo.Create.builder()
                .id(id)
                .userId(userId)
                .token(token)
                .status(WAITING)
                .createdAt(createdAt)
                .build();

        when(waitingQueueService.createWaitingQueue(any(WaitingQueueCommand.Create.class))).thenReturn(response);

        // when, then
        mockMvc.perform(
                        post("/api/v1/waiting-queues")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.createdAt").value("2025-01-01T12:00:00"));
    }

    @Test
    void 대기열_정보를_조회할_때_대기열_토큰이_존재하지_않으면_NotFoundException을_반환한다() throws Exception {
        // given
        String token = UUID.randomUUID().toString();

        when(waitingQueueService.getWaitingQueue(token)).thenThrow(new NotFoundException(QUEUE_TOKEN_NOT_FOUND.getMessage()));

        // when, then
        mockMvc.perform(
                        get("/api/v1/waiting-queues")
                                .header("Queue-Token", token)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(QUEUE_TOKEN_NOT_FOUND.getMessage()));
    }

    @Test
    void 대기열_정보를_조회할_때_대기열_토큰이_존재하면_대기열_정보를_반환한다() throws Exception {
        // given
        Long id = 1L;
        Long userId = 1L;
        String token = UUID.randomUUID().toString();
        WaitingQueueStatus status = ACTIVATED;
        LocalDateTime activatedAt = LocalDateTime.of(2025, 1, 1, 12, 3, 0);
        LocalDateTime expiredAt = LocalDateTime.of(2025, 1, 1, 12, 13, 0);
        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0);
        LocalDateTime modifiedAt = LocalDateTime.of(2025, 1, 1, 12, 3, 0);
        Long waitingNumber = 7L;

        WaitingQueueInfo.GetWaitingQueue response = WaitingQueueInfo.GetWaitingQueue.builder()
                .id(id)
                .userId(userId)
                .token(token)
                .status(status)
                .activatedAt(activatedAt)
                .expiredAt(expiredAt)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .waitingNumber(waitingNumber)
                .build();

        when(waitingQueueService.getWaitingQueue(token)).thenReturn(response);

        // when, then
        mockMvc.perform(
                        get("/api/v1/waiting-queues")
                                .header("Queue-Token", token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.activatedAt").value("2025-01-01T12:03:00"))
                .andExpect(jsonPath("$.expiredAt").value("2025-01-01T12:13:00"))
                .andExpect(jsonPath("$.createdAt").value("2025-01-01T12:00:00"))
                .andExpect(jsonPath("$.modifiedAt").value("2025-01-01T12:03:00"))
                .andExpect(jsonPath("$.order").value("7"));
    }

}