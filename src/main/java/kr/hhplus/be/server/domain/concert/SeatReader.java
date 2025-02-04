package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.domain.concert.entity.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatReader {

    List<Seat> findAllByPlaceId(Long placeId);

    Optional<Seat> findByIdWithLock(Long id);

    Optional<Seat> findById(Long id);

}
