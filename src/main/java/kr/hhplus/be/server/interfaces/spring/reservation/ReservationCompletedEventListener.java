package kr.hhplus.be.server.interfaces.spring.reservation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.reservation.ReservationOutboxService;
import kr.hhplus.be.server.application.reservation.request.ReservationOutboxCommand;
import kr.hhplus.be.server.domain.reservation.event.ReservationCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;
import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCompletedEventListener {

    private final ReservationOutboxService reservationOutboxService;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = BEFORE_COMMIT)
    public void createOutbox(ReservationCompletedEvent event) throws JsonProcessingException {
        reservationOutboxService.createOutbox(event.toCreateOutboxCommand());
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void publishMessage(ReservationCompletedEvent event) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(event);
        reservationOutboxService.publishMessage(ReservationOutboxCommand.PublishMessage.of("concert.reservation-completed", String.valueOf(event.seatId()), message));
    }

}
