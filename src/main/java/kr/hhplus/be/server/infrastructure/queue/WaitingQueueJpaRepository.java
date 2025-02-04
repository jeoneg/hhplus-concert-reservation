package kr.hhplus.be.server.infrastructure.queue;

import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WaitingQueueJpaRepository extends JpaRepository<WaitingQueue, Long> {

    Optional<WaitingQueue> findByToken(String token);

    @Query(value = """
        select wq.id
          from WaitingQueue wq
         where wq.status = 'ACTIVATED'
         order by wq.id asc
         limit 1
    """)
    Optional<WaitingQueue> findLatestActivated();

    @Modifying(clearAutomatically = true)
    @Query(value = """
        update waiting_queue
           set status = 'ACTIVATED'
             , activated_at = now()
             , expired_at = date_add(now(), interval 10 minute)
             , modified_at = now()
         where id in (
            select id from (
                select id
                  from waiting_queue
                 where status = 'WAITING'
                 order by id asc
                 limit :activateSize
            ) as t
         )
    """, nativeQuery = true)
    int activateWaitingQueues(int activateSize);

    @Modifying(clearAutomatically = true)
    @Query(value = """
        update waiting_queue
           set status = 'EXPIRED'
             , expired_at = now()
             , modified_at = now()
         where id in (
            select id from (
               select id
                 from waiting_queue
                where status = 'ACTIVATED'
                  and expired_at <= now()
            ) t
         )
    """, nativeQuery = true)
    int expireWaitingQueues();

}
