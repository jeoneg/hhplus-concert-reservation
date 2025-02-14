package kr.hhplus.be.server.config;

import jakarta.annotation.PostConstruct;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.queue.WaitingQueueWriter;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.point.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;
import static kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus.WAITING;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitConfig {

    private final WaitingQueueWriter waitingQueueWriter;
    private final UserJpaRepository userJpaRepository;
    private final PointJpaRepository pointJpaRepository;
    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    @PostConstruct
    public void createActiveWaitingQueue() {
        String token = "c9bac468-2ce9-4b84-87b6-befe10f05c69";
        WaitingQueue waitingQueue = WaitingQueue.of(1L, token, WAITING);
        waitingQueueWriter.save(waitingQueue);
    }

//    @PostConstruct
    public void init() {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 100000; i++) {
            User user = User.builder()
                .username("username" + i)
                .build();
            users.add(user);
        }
        userJpaRepository.saveAll(users);

        List<Point> points = new ArrayList<>();
        for (int i = 1; i <= 100000; i++) {
            Point point = Point.builder()
                .userId((long) i)
                .balance(10000)
                .build();
            points.add(point);
        }
        pointJpaRepository.saveAll(points);

        List<Concert> concerts = new ArrayList<>();
        for (int i = 1; i <= 10000; i++) {
            Concert concert = Concert.builder()
                .title("title" + i)
                .build();
            concerts.add(concert);
        }
        concertJpaRepository.saveAll(concerts);

        List<ConcertSchedule> schedules = new ArrayList<>();
        for (int i = 0; i <= 99999; i++) {
            ConcertSchedule schedule = ConcertSchedule.builder()
                .concertId((long) i / 10 + 1)
                .placeId((long) i / 10 + 1)
                .scheduledAt(LocalDateTime.now())
                .build();
            schedules.add(schedule);
        }
        concertScheduleJpaRepository.saveAll(schedules);

        List<Reservation> reservations = new ArrayList<>();
        List<ReservationStatus> statuses = Arrays.asList(ReservationStatus.values());
        for (int i = 1; i <= 28; i++) {
            for (int j = 0; j < 20000; j++) {
                Reservation reservation = Reservation.builder()
                    .concertId((long) i)
                    .status(statuses.get((int) (random() * 3)))
                    .reservationAt(LocalDateTime.of(2025, 2, (int) (random() * 27) + 2, (int) (random() * 24), (int) (random() * 60), (int) (random() * 60)))
                    .build();
                reservations.add(reservation);
            }
        }
        reservationJpaRepository.saveAll(reservations);
    }

}
