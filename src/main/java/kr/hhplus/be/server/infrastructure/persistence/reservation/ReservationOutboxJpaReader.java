package kr.hhplus.be.server.infrastructure.persistence.reservation;

import kr.hhplus.be.server.domain.reservation.outbox.ReservationOutbox;
import kr.hhplus.be.server.domain.reservation.outbox.ReservationOutboxReader;
import kr.hhplus.be.server.domain.reservation.outbox.ReservationOutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationOutboxJpaReader implements ReservationOutboxReader {

    private final ReservationOutboxJpaRepository outboxJpaRepository;

    @Override
    public List<ReservationOutbox> findMessagesToPublish(ReservationOutboxStatus status, LocalDateTime time) {
        return outboxJpaRepository.findMessagesToPublish(status, time);
    }

    @Override
    public Optional<ReservationOutbox> findByKey(String key) {
        return outboxJpaRepository.findByKey(key);
    }

}
