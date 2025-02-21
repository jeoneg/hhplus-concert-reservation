package kr.hhplus.be.server.application.reservation.request;

import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public class ReservationOutboxCommand {

    @Builder
    public record Create(
        Long id,
        Long userId,
        Long concertId,
        Long scheduleId,
        Long seatId,
        int paymentAmount,
        LocalDateTime reservationAt,
        ReservationStatus status
    ) {
    }

    @Builder
    public record PublishMessage(
        String topic,
        String key,
        String message
    ) {
        public static PublishMessage of(String topic, String key, String message) {
            return PublishMessage.builder()
                .topic(topic)
                .key(key)
                .message(message)
                .build();
        }
    }

}
