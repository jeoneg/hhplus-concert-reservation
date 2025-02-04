package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.common.time.TimeProvider;
import kr.hhplus.be.server.domain.concert.ConcertReader;
import kr.hhplus.be.server.domain.concert.ConcertScheduleReader;
import kr.hhplus.be.server.domain.concert.SeatReader;
import kr.hhplus.be.server.domain.concert.SeatWriter;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationInfo;
import kr.hhplus.be.server.domain.user.UserReader;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.common.exception.ErrorMessage.*;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final UserReader userReader;
    private final ConcertReader concertReader;
    private final ConcertScheduleReader concertScheduleReader;
    private final SeatReader seatReader;
    private final SeatWriter seatWriter;
    private final ReservationReader reservationReader;
    private final ReservationWriter reservationWriter;
    private final TimeProvider timeProvider;

    @Transactional
    public ReservationInfo.Create reserve(ReservationCommand.Create command) {
        command.validate();

        User user = userReader.findById(command.userId())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));
        Concert concert = concertReader.findById(command.concertId())
                .orElseThrow(() -> new NotFoundException(CONCERT_NOT_FOUND.getMessage()));
        ConcertSchedule schedule = concertScheduleReader.findById(command.scheduleId())
                .orElseThrow(() -> new NotFoundException(SCHEDULE_NOT_FOUND.getMessage()));

        Seat seat = seatReader.findByIdWithLock(command.seatId())
                .orElseThrow(() -> new NotFoundException(SEAT_NOT_FOUND.getMessage()));

        if (seat.isReserved()) {
            throw new IllegalStateException(SEAT_ALREADY_RESERVED.getMessage());
        }

        seat.temporaryReserve(timeProvider.now());
        seatWriter.save(seat);

        Reservation reservation = Reservation.create(command.userId(), command.concertId(), command.scheduleId(), command.seatId(), seat.getPrice());
        Reservation savedReservation = reservationWriter.save(reservation);

        log.info("예약 성공: 사용자 ID = {}, 좌석 ID = {}, 예약 ID = {}", user.getId(), savedReservation.getSeatId(), savedReservation.getId());
        return ReservationInfo.Create.from(savedReservation);
    }

    public ReservationInfo.GetReservation getReservation(Long id) {
        Reservation reservation = reservationReader.findById(id)
            .orElseThrow(() -> new NotFoundException(RESERVATION_NOT_FOUND.getMessage()));
        return ReservationInfo.GetReservation.from(reservation);
    }

    @Transactional
    public void confirm(Long id) {
        Reservation reservation = reservationReader.findById(id)
                .orElseThrow(() -> new NotFoundException(RESERVATION_NOT_FOUND.getMessage()));
        Seat seat = seatReader.findById(reservation.getSeatId())
                .orElseThrow(() -> new NotFoundException(SEAT_NOT_FOUND.getMessage()));

        reservation.confirm();
        seat.confirm();

        reservationWriter.save(reservation);
        seatWriter.save(seat);
    }

}
