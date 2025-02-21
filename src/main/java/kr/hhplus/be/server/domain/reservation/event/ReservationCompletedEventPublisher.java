package kr.hhplus.be.server.domain.reservation.event;

public interface ReservationCompletedEventPublisher {

    void publish(ReservationCompletedEvent event);

}
