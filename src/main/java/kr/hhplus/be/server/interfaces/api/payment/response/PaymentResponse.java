package kr.hhplus.be.server.interfaces.api.payment.response;

import kr.hhplus.be.server.application.payment.response.PaymentResult;
import lombok.Builder;

public class PaymentResponse {

    @Builder
    public record Create(
            Long userId,
            Long reservationId
    ) {
        public static Create from(PaymentResult.Create result) {
            return Create.builder()
                    .userId(result.userId())
                    .reservationId(result.reservationId())
                    .build();
        }
    }

}
