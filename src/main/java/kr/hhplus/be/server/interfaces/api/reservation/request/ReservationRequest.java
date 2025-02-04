package kr.hhplus.be.server.interfaces.api.reservation.request;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.reservation.ReservationCommand;
import lombok.Builder;

public class ReservationRequest {

    @Builder
    public record Create(
            @NotNull(message = "사용자 아이디는 필수입니다.")
            Long userId,
            @NotNull(message = "콘서트 아이디는 필수입니다.")
            Long concertId,
            @NotNull(message = "일정 아이디는 필수입니다.")
            Long scheduleId,
            @NotNull(message = "좌석 아이디는 필수입니다.")
            Long seatId
    ) {
        public ReservationCommand.Create toCommand() {
            return ReservationCommand.Create.builder()
                    .userId(this.userId)
                    .concertId(this.concertId)
                    .scheduleId(this.scheduleId)
                    .seatId(this.seatId)
                    .build();
        }
    }

}
