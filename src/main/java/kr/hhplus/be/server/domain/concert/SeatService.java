package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.common.time.TimeProvider;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.common.exception.ErrorMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {

    private final ConcertScheduleReader concertScheduleReader;
    private final SeatReader seatReader;
    private final SeatWriter seatWriter;
    private final TimeProvider timeProvider;

    @Transactional(readOnly = true)
    public List<SeatInfo.GetSeat> getSeats(Long scheduleId) {
        ConcertSchedule concertSchedule = concertScheduleReader.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException(SCHEDULE_NOT_FOUND.getMessage()));

        List<Seat> seats = seatReader.findAllByPlaceId(concertSchedule.getPlaceId());

        return seats.stream()
                .map(SeatInfo.GetSeat::from)
                .toList();
    }

    @Transactional
    public SeatInfo.Reserve reserve(SeatCommand.Reserve command) {
        Seat seat = seatReader.findById(command.id())
            .orElseThrow(() -> new NotFoundException(SEAT_NOT_FOUND.getMessage()));

        if (seat.isReserved()) {
            throw new IllegalStateException(SEAT_ALREADY_RESERVED.getMessage());
        }

        seat.temporaryReserve(timeProvider.now());
        Seat savedSeat = seatWriter.save(seat);

        return SeatInfo.Reserve.from(savedSeat);
//        throw new IllegalStateException("예외 발생!!");
    }

    @Transactional
    public void expireTemporaryReservations() {
        seatWriter.expireTemporaryReservations(timeProvider.now());
    }

    @Transactional(readOnly = true)
    public void checkExpiredTemporaryReserved(Long id) {
        Seat seat = seatReader.findById(id)
                .orElseThrow(() -> new NotFoundException(SEAT_NOT_FOUND.getMessage()));
        if (seat.isExpiredTemporaryReserved(timeProvider.now())) {
            throw new IllegalStateException(SEAT_TEMPORARY_RESERVED_EXPIRED.getMessage());
        }
    }

}
