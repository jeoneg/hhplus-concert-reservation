package kr.hhplus.be.server.infrastructure.spring.reservation;

import kr.hhplus.be.server.domain.reservation.event.ReservationCompletedEvent;
import kr.hhplus.be.server.domain.reservation.event.ReservationCompletedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationCompletedSpringEventPublisher implements ReservationCompletedEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(ReservationCompletedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

}
