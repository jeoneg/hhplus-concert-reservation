package kr.hhplus.be.server.interfaces.api.reservation.response;

import kr.hhplus.be.server.domain.reservation.model.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import lombok.Builder;

public class ReservationResponse {

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
        public static Create from(ReservationInfo.Create reservation) {
            return Create.builder()
                    .id(reservation.id())
                    .userId(reservation.userId())
                    .concertId(reservation.concertId())
                    .scheduleId(reservation.scheduleId())
                    .seatId(reservation.seatId())
                    .paymentAmount(reservation.paymentAmount())
                    .status(reservation.status())
                    .build();
        }
    }

    @Builder
    public record Search(
        Long concertId,
        Long count
    ) {
    }

}
