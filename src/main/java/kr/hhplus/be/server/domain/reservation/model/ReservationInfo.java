package kr.hhplus.be.server.domain.reservation.model;

import kr.hhplus.be.server.domain.reservation.entity.Reservation;
import lombok.Builder;

public class ReservationInfo {

    @Builder
    public record Create(
        Long id,
        Long userId,
        Long concertId,
        Long scheduleId,
        Long seatId,
        int paymentAmount,
        ReservationStatus status
    ) {
        public static Create from(Reservation reservation) {
            return Create.builder()
                .id(reservation.getId())
                .userId(reservation.getUserId())
                .concertId(reservation.getConcertId())
                .scheduleId(reservation.getScheduleId())
                .seatId(reservation.getSeatId())
                .paymentAmount(reservation.getPaymentAmount())
                .status(reservation.getStatus())
                .build();
        }
    }

    @Builder
    public record GetReservation(
        Long id,
        Long userId,
        Long concertId,
        Long scheduleId,
        Long seatId,
        int paymentAmount,
        ReservationStatus status
    ) {
        public static GetReservation from(Reservation reservation) {
            return GetReservation.builder()
                .id(reservation.getId())
                .userId(reservation.getUserId())
                .concertId(reservation.getConcertId())
                .scheduleId(reservation.getScheduleId())
                .seatId(reservation.getSeatId())
                .paymentAmount(reservation.getPaymentAmount())
                .status(reservation.getStatus())
                .build();
        }
    }

}
