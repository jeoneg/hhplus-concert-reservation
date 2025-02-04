package kr.hhplus.be.server.infrastructure.concert;

import kr.hhplus.be.server.domain.concert.ConcertReader;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertJpaReader implements ConcertReader {

    private final ConcertJpaRepository concertJpaRepository;

    @Override
    public Optional<Concert> findById(Long id) {
        return concertJpaRepository.findById(id);
    }

}
