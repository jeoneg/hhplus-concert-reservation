package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.domain.concert.entity.Concert;

import java.util.Optional;

public interface ConcertReader {

    Optional<Concert> findById(Long id);

}
