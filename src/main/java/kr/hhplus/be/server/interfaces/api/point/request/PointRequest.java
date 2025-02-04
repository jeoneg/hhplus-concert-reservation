package kr.hhplus.be.server.interfaces.api.point.request;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.point.PointCommand;
import lombok.Builder;

public class PointRequest {

    @Builder
    public record Charge(
            @NotNull(message = "사용자 아이디는 필수입니다.")
            Long userId,
            @NotNull(message = "충전 금액은 필수입니다.")
            Long amount
    ) {
        public PointCommand.Charge toCommand() {
            return PointCommand.Charge.builder()
                    .userId(userId)
                    .amount(amount.intValue())
                    .build();
        }
    }

}
