package kr.hhplus.be.server.interfaces.scheduler;

import kr.hhplus.be.server.domain.concert.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertScheduler {

    private final SeatService seatService;

    @Scheduled(fixedDelay = 60000)
    public void expireTemporaryReservations() {
        seatService.expireTemporaryReservations();
    }

}
