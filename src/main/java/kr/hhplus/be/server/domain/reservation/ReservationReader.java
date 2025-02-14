package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.interfaces.api.reservation.response.ReservationResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationReader {

    Optional<Reservation> findById(Long id);

    List<ReservationResponse.Search> search(ReservationStatus status, LocalDateTime startDate, LocalDateTime endDate);

}
