package kr.hhplus.be.server.domain.reservation.outbox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationOutboxReader {

    List<ReservationOutbox> findMessagesToPublish(ReservationOutboxStatus status, LocalDateTime time);

    Optional<ReservationOutbox> findByKey(String key);

}
