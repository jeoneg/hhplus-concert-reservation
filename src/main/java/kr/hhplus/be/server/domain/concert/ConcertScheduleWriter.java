package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;

public interface ConcertScheduleWriter {

    ConcertSchedule save(ConcertSchedule concertSchedule);

}
