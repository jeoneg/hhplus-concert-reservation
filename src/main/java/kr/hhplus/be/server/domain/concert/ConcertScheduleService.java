package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.ConcertScheduleInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static kr.hhplus.be.server.common.exception.ErrorMessage.CONCERT_NOT_FOUND;
import static kr.hhplus.be.server.common.exception.ErrorMessage.SCHEDULE_LIST_EMPTY;

@Service
@RequiredArgsConstructor
public class ConcertScheduleService {

    private final ConcertReader concertReader;
    private final ConcertScheduleReader concertScheduleReader;

    @Cacheable(value = "concertSchedules", key = "'concert' + ':' + #concertId + ':' + 'schedules'", cacheManager = "cacheManager")
    public List<ConcertScheduleInfo.GetConcertSchedule> getSchedules(Long concertId) {
        Concert concert = concertReader.findById(concertId)
            .orElseThrow(() -> new NotFoundException(CONCERT_NOT_FOUND.getMessage()));

        List<ConcertSchedule> concertSchedules = concertScheduleReader.findAllByConcertId(concert.getId());
        if (concertSchedules == null || concertSchedules.isEmpty()) {
            throw new NotFoundException(SCHEDULE_LIST_EMPTY.getMessage());
        }

        return concertSchedules.stream()
            .map(ConcertScheduleInfo.GetConcertSchedule::from)
            .toList();
    }

}
