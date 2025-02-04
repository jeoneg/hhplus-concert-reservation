package kr.hhplus.be.server.infrastructure.concert;

import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertSchedule, Long> {

    List<ConcertSchedule> findAllByConcertId(Long concertId);

}
