package kr.hhplus.be.server.interfaces.consumer;

import kr.hhplus.be.server.application.reservation.ReservationOutboxService;
import kr.hhplus.be.server.infrastructure.external.DataPlatformMockApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationKafkaConsumer {

    private final ReservationOutboxService outboxFacade;
    private final DataPlatformMockApiClient dataPlatformMockApiClient;

    @KafkaListener(topics = "concert.reservation-completed", groupId = "reservation-outbox-group")
    public void consumeOutbox(ConsumerRecord<String, String> record) {
        outboxFacade.publish(record.key());
    }

    @KafkaListener(topics = "concert.reservation-completed", groupId = "data-platform-group")
    public void consumeDataPlatform(ConsumerRecord<String, String> record) {
        dataPlatformMockApiClient.sendData();
    }

}
