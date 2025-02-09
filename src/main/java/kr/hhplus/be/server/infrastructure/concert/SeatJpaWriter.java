package kr.hhplus.be.server.infrastructure.concert;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.concert.SeatWriter;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class SeatJpaWriter implements SeatWriter {

    private final SeatJpaRepository seatJpaRepository;
    private final EntityManager em;

    @Override
    public Seat save(Seat seat) {
        return seatJpaRepository.save(seat);
    }

    @Override
    public Seat saveAndFlush(Seat seat) {
        Seat savedSeat = seatJpaRepository.save(seat);
        em.flush();
        return savedSeat;
    }

    @Override
    public int expireTemporaryReservations(LocalDateTime now) {
        return seatJpaRepository.expireTemporaryReservations(now);
    }

}
