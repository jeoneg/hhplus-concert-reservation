package kr.hhplus.be.server.infrastructure.concert;

import kr.hhplus.be.server.domain.concert.SeatReader;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SeatJpaReader implements SeatReader {

    private final SeatJpaRepository seatJpaRepository;

    @Override
    public List<Seat> findAllByPlaceId(Long placeId) {
        return seatJpaRepository.findAllByPlaceId(placeId);
    }

    @Override
    public Optional<Seat> findByIdWithLock(Long id) {
        return seatJpaRepository.findByIdWithLock(id);
    }

    @Override
    public Optional<Seat> findById(Long id) {
        return seatJpaRepository.findById(id);
    }

}
