package kr.hhplus.be.server.interfaces.api.concert.response;

import kr.hhplus.be.server.domain.concert.model.SeatInfo;
import lombok.Builder;

import static kr.hhplus.be.server.domain.concert.model.SeatStatus.AVAILABLE;

public class SeatResponse {

    @Builder
    public record GetSeat(
            Long id,
            String availableYn
    ) {
        public static GetSeat from(SeatInfo.GetSeat info) {
            return GetSeat.builder()
                    .id(info.id())
                    .availableYn(info.status() == AVAILABLE ? "Y" : "N")
                    .build();
        }
    }

}
