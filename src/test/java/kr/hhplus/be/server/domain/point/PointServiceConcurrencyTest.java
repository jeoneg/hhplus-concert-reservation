package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.user.UserWriter;
import kr.hhplus.be.server.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
public class PointServiceConcurrencyTest {

    @Autowired
    private PointService sut;

    @Autowired
    private UserWriter userWriter;

    @Autowired
    private PointWriter pointWriter;

    @Autowired
    private PointReader pointReader;

    @Test
    void 특정_사용자의_포인트를_충전할_때_동시에_3개의_10000포인트_충전_요청이_들어오면_잔액은_30000포인트가_된다() throws InterruptedException {
        // given
        User user = User.builder()
                .username("user")
                .build();
        userWriter.save(user);

        Point point = Point.builder()
                .userId(user.getId())
                .balance(0)
                .build();
        pointWriter.save(point);

        // when
        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        IntStream.range(0, 3).forEach(i -> {
            es.submit(() -> {
                try {
                    sut.charge(PointCommand.Charge.of(user.getId(), 10000));
                    successCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        });

        latch.await();

        // then
        Point result = pointReader.findByUserId(user.getId()).get();

        assertThat(result.getBalance()).isEqualTo(30000);
        assertThat(successCount.get()).isEqualTo(3);
    }

    @Test
    void 특정_사용자의_포인트를_사용할_때_동시에_3개의_10000포인트_사용_요청이_들어오면_잔액은_0포인트가_된다() throws InterruptedException {
        // given
        User user = User.builder()
                .username("user")
                .build();
        userWriter.save(user);

        Point point = Point.builder()
                .userId(user.getId())
                .balance(30000)
                .build();
        pointWriter.save(point);

        // when
        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        IntStream.range(0, 3).forEach(i -> {
            es.submit(() -> {
                try {
                    sut.use(PointCommand.Use.of(user.getId(), 10000));
                    successCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        });

        latch.await();

        // then
        Point result = pointReader.findByUserId(user.getId()).get();

        assertThat(result.getBalance()).isEqualTo(0);
        assertThat(successCount.get()).isEqualTo(3);
    }

}
