package kr.hhplus.be.server.application.reservation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.reservation.request.ReservationOutboxCommand;
import kr.hhplus.be.server.application.reservation.response.ReservationOutboxInfo;
import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.common.time.TimeProvider;
import kr.hhplus.be.server.domain.reservation.outbox.ReservationOutbox;
import kr.hhplus.be.server.domain.reservation.outbox.ReservationOutboxReader;
import kr.hhplus.be.server.domain.reservation.outbox.ReservationOutboxWriter;
import kr.hhplus.be.server.infrastructure.kafka.reservation.ReservationKafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.common.exception.ErrorMessage.OUTBOX_NOT_FOUND;
import static kr.hhplus.be.server.domain.reservation.outbox.ReservationOutboxStatus.INIT;

@Service
@RequiredArgsConstructor
public class ReservationOutboxService {

    private final ObjectMapper objectMapper;
    private final ReservationOutboxWriter outboxWriter;
    private final ReservationKafkaEventPublisher kafkaEventPublisher;
    private final ReservationOutboxReader outboxReader;
    private final TimeProvider timeProvider;

    @Transactional
    public void createOutbox(ReservationOutboxCommand.Create command) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(command);
        ReservationOutbox outbox = ReservationOutbox.create("concert.reservation-created", String.valueOf(command.seatId()), message);
        outboxWriter.save(outbox);
    }

    public void publishMessage(ReservationOutboxCommand.PublishMessage command) {
        kafkaEventPublisher.publish(command.topic(), command.key(), command.message());
    }

    @Transactional(readOnly = true)
    public List<ReservationOutboxInfo.GetOutbox> findMessagesToPublish() {
        List<ReservationOutbox> outboxs = outboxReader.findMessagesToPublish(INIT, timeProvider.now().minusMinutes(5));
        return outboxs.stream()
                .map(ReservationOutboxInfo.GetOutbox::from)
                .toList();
    }

    @Transactional
    public void publish(String key) {
        ReservationOutbox outbox = outboxReader.findByKey(key)
                .orElseThrow(() -> new NotFoundException(OUTBOX_NOT_FOUND.getMessage()));
        outbox.publish();
        outboxWriter.save(outbox);
    }

}
