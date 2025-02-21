package kr.hhplus.be.server.application.reservation.response;

import kr.hhplus.be.server.domain.reservation.outbox.ReservationOutbox;
import kr.hhplus.be.server.domain.reservation.outbox.ReservationOutboxStatus;
import lombok.Builder;

public class ReservationOutboxInfo {

    @Builder
    public record GetOutbox(
        Long id,
        String topic,
        String key,
        String message,
        ReservationOutboxStatus status
    ) {
        public static GetOutbox from(ReservationOutbox outbox) {
            return GetOutbox.builder()
                .id(outbox.getId())
                .topic(outbox.getTopic())
                .key(outbox.getKey())
                .message(outbox.getMessage())
                .build();
        }
    }

}
