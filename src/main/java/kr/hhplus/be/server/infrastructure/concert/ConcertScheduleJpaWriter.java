package kr.hhplus.be.server.infrastructure.concert;

import kr.hhplus.be.server.domain.concert.ConcertScheduleWriter;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertScheduleJpaWriter implements ConcertScheduleWriter {

    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Override
    public ConcertSchedule save(ConcertSchedule concertSchedule) {
        return concertScheduleJpaRepository.save(concertSchedule);
    }

}
