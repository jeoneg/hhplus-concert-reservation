package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.common.time.TimeProvider;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.common.exception.ErrorMessage.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class SeatService {

    private final ConcertScheduleReader concertScheduleReader;
    private final SeatReader seatReader;
    private final SeatWriter seatWriter;
    private final TimeProvider timeProvider;

    public List<SeatInfo.GetSeat> getSeats(Long scheduleId) {
        ConcertSchedule concertSchedule = concertScheduleReader.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException(SCHEDULE_NOT_FOUND.getMessage()));

        List<Seat> seats = seatReader.findAllByPlaceId(concertSchedule.getPlaceId());

        return seats.stream()
                .map(SeatInfo.GetSeat::from)
                .toList();
    }

    @Transactional
    public void expireTemporaryReservations() {
        seatWriter.expireTemporaryReservations(timeProvider.now());
    }

    public void checkExpiredTemporaryReserved(Long id) {
        Seat seat = seatReader.findById(id)
                .orElseThrow(() -> new NotFoundException(SEAT_NOT_FOUND.getMessage()));
        if (seat.isExpiredTemporaryReserved(timeProvider.now())) {
            throw new IllegalStateException(SEAT_TEMPORARY_RESERVED_EXPIRED.getMessage());
        }
    }

}
