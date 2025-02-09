package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.common.exception.ErrorMessage.CONCERT_NOT_FOUND;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertReader concertReader;

    @Cacheable(value = "concerts", key = "#concertId", cacheManager = "cacheManager")
    public ConcertInfo.GetConcert getConcert(Long concertId) {
        Concert concert = concertReader.findById(concertId)
            .orElseThrow(() -> new NotFoundException(CONCERT_NOT_FOUND.getMessage()));
        return ConcertInfo.GetConcert.from(concert);
    }

}
