package kr.hhplus.be.server.infrastructure.concert;

import kr.hhplus.be.server.domain.concert.SeatWriter;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class SeatJpaWriter implements SeatWriter {

    private final SeatJpaRepository seatJpaRepository;

    @Override
    public Seat save(Seat seat) {
        return seatJpaRepository.save(seat);
    }

    @Override
    public int expireTemporaryReservations(LocalDateTime now) {
        return seatJpaRepository.expireTemporaryReservations(now);
    }

}
