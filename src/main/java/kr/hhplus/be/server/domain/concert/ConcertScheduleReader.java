package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;

import java.util.List;
import java.util.Optional;

public interface ConcertScheduleReader {

    List<ConcertSchedule> findAllByConcertId(Long concertId);

    Optional<ConcertSchedule> findById(Long id);

}
