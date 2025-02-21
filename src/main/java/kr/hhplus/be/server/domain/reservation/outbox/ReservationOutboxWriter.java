package kr.hhplus.be.server.domain.reservation.outbox;

public interface ReservationOutboxWriter {

    ReservationOutbox save(ReservationOutbox reservationOutbox);

}
