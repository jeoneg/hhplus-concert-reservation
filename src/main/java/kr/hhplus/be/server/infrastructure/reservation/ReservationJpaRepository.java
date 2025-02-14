package kr.hhplus.be.server.infrastructure.reservation;

import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.interfaces.api.reservation.response.ReservationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "select new kr.hhplus.be.server.interfaces.api.reservation.response.ReservationResponse$Search(r.concertId, count(r)) from Reservation r " +
         "where r.status = :status and r.reservationAt between :startDate and :endDate group by r.concertId order by count(r) desc")
    List<ReservationResponse.Search> search(ReservationStatus status, LocalDateTime startDate, LocalDateTime endDate);

}
