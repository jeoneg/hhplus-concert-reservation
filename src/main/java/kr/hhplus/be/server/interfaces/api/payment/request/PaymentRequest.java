package kr.hhplus.be.server.interfaces.api.payment.request;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.application.payment.request.PaymentCriteria;
import lombok.Builder;

public class PaymentRequest {

    @Builder
    public record Create(
            @NotNull(message = "사용자 아이디는 필수입니다.")
            Long userId,
            @NotNull(message = "예약 아이디는 필수입니다.")
            Long reservationId
    ) {
        public PaymentCriteria.Create toInput(String token) {
            return PaymentCriteria.Create.builder()
                    .userId(userId)
                    .reservationId(reservationId)
                    .token(token)
                    .build();
        }
    }

}
