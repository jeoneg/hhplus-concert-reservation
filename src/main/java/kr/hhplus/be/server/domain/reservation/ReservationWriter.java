package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.reservation.entity.Reservation;

import java.util.Optional;

public interface ReservationWriter {

    Reservation save(Reservation reservation);

    Optional<Reservation> findById(Long id);

}
