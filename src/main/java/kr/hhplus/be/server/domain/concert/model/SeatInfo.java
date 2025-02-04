package kr.hhplus.be.server.domain.concert.model;

import kr.hhplus.be.server.domain.concert.entity.Seat;
import lombok.Builder;

public class SeatInfo {

    @Builder
    public record GetSeat(
            Long id,
            Long placeId,
            SeatStatus status,
            int price
    ) {
        public static GetSeat from(Seat seat) {
            return GetSeat.builder()
                    .id(seat.getId())
                    .placeId(seat.getPlaceId())
                    .status(seat.getStatus())
                    .price(seat.getPrice())
                    .build();
        }
    }

}
