package kr.hhplus.be.server.infrastructure.concert;

import kr.hhplus.be.server.domain.concert.ConcertWriter;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertJpaWriter implements ConcertWriter {

    private final ConcertJpaRepository concertJpaRepository;

    @Override
    public Concert save(Concert concert) {
        return concertJpaRepository.save(concert);
    }

}
