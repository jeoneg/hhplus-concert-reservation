package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.common.aop.LockType;
import kr.hhplus.be.server.common.aop.annotation.DistributedLock;
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
import kr.hhplus.be.server.domain.reservation.event.ReservationCompletedEvent;
import kr.hhplus.be.server.domain.reservation.event.ReservationCompletedEventPublisher;
import kr.hhplus.be.server.domain.reservation.model.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.user.UserReader;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.interfaces.api.reservation.response.ReservationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.common.exception.ErrorMessage.*;

@Slf4j
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
    private final ReservationCompletedEventPublisher eventPublisher;

    @DistributedLock(key = "#command.seatId()", leaseTime = 5, lockType = LockType.SIMPLE_LOCK)
    public ReservationInfo.Create reserve(ReservationCommand.Create command) {
        command.validate();

        User user = userReader.findById(command.userId())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));
        Concert concert = concertReader.findById(command.concertId())
                .orElseThrow(() -> new NotFoundException(CONCERT_NOT_FOUND.getMessage()));
        ConcertSchedule schedule = concertScheduleReader.findById(command.scheduleId())
                .orElseThrow(() -> new NotFoundException(SCHEDULE_NOT_FOUND.getMessage()));

        Seat seat = seatReader.findById(command.seatId())
            .orElseThrow(() -> new NotFoundException(SEAT_NOT_FOUND.getMessage()));

        if (seat.isReserved()) {
            throw new IllegalStateException(SEAT_ALREADY_RESERVED.getMessage());
        }

        seat.temporaryReserve(timeProvider.now());
        seatWriter.save(seat);

        Reservation reservation = Reservation.create(command.userId(), command.concertId(), command.scheduleId(), command.seatId(), seat.getPrice());
        Reservation savedReservation = reservationWriter.save(reservation);

        eventPublisher.publish(ReservationCompletedEvent.from(savedReservation));

        return ReservationInfo.Create.from(savedReservation);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<ReservationResponse.Search> search(ReservationStatus status, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        return reservationReader.search(status, startDateTime, endDateTime);
    }

}
