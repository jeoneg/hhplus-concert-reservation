package kr.hhplus.be.server.domain.reservation.event;

import kr.hhplus.be.server.application.reservation.request.ReservationOutboxCommand;
import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReservationCompletedEvent(
    Long id,
    Long userId,
    Long concertId,
    Long scheduleId,
    Long seatId,
    int paymentAmount,
    LocalDateTime reservationAt,
    ReservationStatus status
) {
    public static ReservationCompletedEvent from(Reservation reservation) {
        return ReservationCompletedEvent.builder()
            .id(reservation.getId())
            .userId(reservation.getUserId())
            .concertId(reservation.getConcertId())
            .scheduleId(reservation.getScheduleId())
            .seatId(reservation.getSeatId())
            .paymentAmount(reservation.getPaymentAmount())
            .reservationAt(reservation.getReservationAt())
            .status(reservation.getStatus())
            .build();
    }

    public ReservationOutboxCommand.Create toCreateOutboxCommand() {
        return ReservationOutboxCommand.Create.builder()
            .id(id)
            .userId(userId)
            .concertId(concertId)
            .scheduleId(scheduleId)
            .seatId(seatId)
            .paymentAmount(paymentAmount)
            .reservationAt(reservationAt)
            .status(status)
            .build();
    }
}
