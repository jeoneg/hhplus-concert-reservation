package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.domain.concert.entity.Seat;

import java.time.LocalDateTime;

public interface SeatWriter {

    Seat save(Seat seat);

    int expireTemporaryReservations(LocalDateTime now);

}
