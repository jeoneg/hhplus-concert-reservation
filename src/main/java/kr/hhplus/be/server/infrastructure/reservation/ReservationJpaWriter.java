package kr.hhplus.be.server.infrastructure.reservation;

import kr.hhplus.be.server.domain.reservation.ReservationWriter;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationJpaWriter implements ReservationWriter {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Optional<Reservation> findByScheduleIdAndSeatId(Long scheduleId, Long seatId) {
        return reservationJpaRepository.findByScheduleIdAndSeatId(scheduleId, seatId);
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationJpaRepository.findById(id);
    }

}
