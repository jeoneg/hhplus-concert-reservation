package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.concert.ConcertScheduleWriter;
import kr.hhplus.be.server.domain.concert.ConcertWriter;
import kr.hhplus.be.server.domain.concert.SeatWriter;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.entity.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import kr.hhplus.be.server.domain.user.UserWriter;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static kr.hhplus.be.server.domain.concert.model.SeatStatus.AVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
class ReservationServiceConcurrencyTest {

    @Autowired
    private ReservationService sut;

    @Autowired
    private UserWriter userWriter;

    @Autowired
    private ConcertWriter concertWriter;

    @Autowired
    private ConcertScheduleWriter concertScheduleWriter;

    @Autowired
    private SeatWriter seatWriter;

    @Test
    void 특정_좌석을_예약할_때_동시에_10개의_요청이_들어오면_1개의_요청만_성공한다() throws InterruptedException {
        // given
        Long concertId = 1L;
        Long scheduleId = 1L;
        Long seatId = 1L;

        int threadCount = 10;

        for (int i = 0; i <= threadCount; i++) {
            userWriter.save(createUser("user" + i));
        }

        concertWriter.save(createConcert("title"));
        concertScheduleWriter.save(createSchedule(concertId, 1L, LocalDateTime.now()));
        seatWriter.save(createSeat(1L, AVAILABLE, 130000));

        // when
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
        List<Long> durations = new ArrayList<>();
        IntStream.range(1, threadCount + 1).forEach(i -> {
            Long userId = (long) i;
            es.submit(() -> {
                long taskStartTime = System.currentTimeMillis();
                try {
                    sut.reserve(ReservationCommand.Create.of(userId, concertId, scheduleId, seatId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.error("예약 실패: 사용자 ID = {}", userId);
                } finally {
                    durations.add(System.currentTimeMillis() - taskStartTime);
                    latch.countDown();
                }
            });
        });

        latch.await();

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(9);

        log.info("전체 소요 시간: {}ms", System.currentTimeMillis() - startTime);
        log.info("최소 소요 시간: {}ms", durations.stream().min(Long::compare).orElse(0L));
        log.info("평균 소요 시간: {}ms", durations.stream().mapToLong(Long::longValue).average().orElse(0L));
        log.info("최대 소요 시간: {}ms", durations.stream().max(Long::compare).orElse(0L));
    }

    private User createUser(String username) {
        return User.builder()
                .username(username)
                .build();
    }

    private Concert createConcert(String title) {
        return Concert.builder()
                .title(title)
                .build();
    }

    private ConcertSchedule createSchedule(Long concertId, Long placeId, LocalDateTime scheduledAt) {
        return ConcertSchedule.builder()
                .concertId(concertId)
                .placeId(placeId)
                .scheduledAt(scheduledAt)
                .build();
    }

    private Seat createSeat(Long placeId, SeatStatus status, int price) {
        return Seat.builder()
                .placeId(placeId)
                .status(status)
                .price(price)
                .build();
    }

}