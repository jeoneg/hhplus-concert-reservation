package kr.hhplus.be.server.interfaces.scheduler;

import kr.hhplus.be.server.application.reservation.ReservationOutboxService;
import kr.hhplus.be.server.application.reservation.request.ReservationOutboxCommand;
import kr.hhplus.be.server.application.reservation.response.ReservationOutboxInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationOutboxScheduler {

    private final ReservationOutboxService reservationOutboxService;

//    @Scheduled(fixedDelay = 10000)
    @Scheduled(cron = "0 0/5 * * * *")
    public void pollOutboxMessagesAndPublish() {
        List<ReservationOutboxInfo.GetOutbox> messages = reservationOutboxService.findMessagesToPublish();
        messages.forEach((it) -> reservationOutboxService.publishMessage(ReservationOutboxCommand.PublishMessage.of("concert.reservation-completed", it.key(), it.message())));
    }

}
