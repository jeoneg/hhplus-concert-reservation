package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.domain.concert.entity.Concert;

public interface ConcertWriter {

    Concert save(Concert concert);

}
