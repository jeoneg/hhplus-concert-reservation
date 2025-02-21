package kr.hhplus.be.server.infrastructure.persistence.reservation;

import kr.hhplus.be.server.domain.reservation.outbox.ReservationOutbox;
import kr.hhplus.be.server.domain.reservation.outbox.ReservationOutboxWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationOutboxJpaWriter implements ReservationOutboxWriter {

    private final ReservationOutboxJpaRepository outboxJpaRepository;

    @Override
    public ReservationOutbox save(ReservationOutbox reservationOutbox) {
        return outboxJpaRepository.save(reservationOutbox);
    }

}
