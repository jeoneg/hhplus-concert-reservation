package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.reservation.entity.Reservation;

import java.util.Optional;

public interface ReservationReader {

    Optional<Reservation> findById(Long id);

}
