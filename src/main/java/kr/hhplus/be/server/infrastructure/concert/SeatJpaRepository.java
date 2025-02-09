package kr.hhplus.be.server.infrastructure.concert;

import kr.hhplus.be.server.domain.concert.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.LockModeType.OPTIMISTIC;
import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    List<Seat> findAllByPlaceId(Long placeId);

    @Lock(OPTIMISTIC)
    @Query("select s from Seat s where s.id = :id")
    Optional<Seat> findByIdWithLock(Long id);

    @Modifying(clearAutomatically = true)
    @Query("""
        update Seat s
           set s.status = 'AVAILABLE'
         where s.status = 'TEMPORARY_RESERVED'
           and s.expiredAt <= :now
    """)
    int expireTemporaryReservations(LocalDateTime now);

}
