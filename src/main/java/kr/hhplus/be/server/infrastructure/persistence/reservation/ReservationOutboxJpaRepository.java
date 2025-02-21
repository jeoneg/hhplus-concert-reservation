package kr.hhplus.be.server.infrastructure.persistence.reservation;

import kr.hhplus.be.server.domain.reservation.outbox.ReservationOutbox;
import kr.hhplus.be.server.domain.reservation.outbox.ReservationOutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationOutboxJpaRepository extends JpaRepository<ReservationOutbox, Long> {

    @Query(value = "select o from ReservationOutbox o where o.status = :status and o.modifiedAt <= :time")
    List<ReservationOutbox> findMessagesToPublish(ReservationOutboxStatus status, LocalDateTime time);

    Optional<ReservationOutbox> findByKey(String key);

}
