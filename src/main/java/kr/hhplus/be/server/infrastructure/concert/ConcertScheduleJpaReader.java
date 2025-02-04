package kr.hhplus.be.server.infrastructure.concert;

import kr.hhplus.be.server.domain.concert.ConcertScheduleReader;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertScheduleJpaReader implements ConcertScheduleReader {

    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Override
    public List<ConcertSchedule> findAllByConcertId(Long concertId) {
        return concertScheduleJpaRepository.findAllByConcertId(concertId);
    }

    @Override
    public Optional<ConcertSchedule> findById(Long id) {
        return concertScheduleJpaRepository.findById(id);
    }

}
