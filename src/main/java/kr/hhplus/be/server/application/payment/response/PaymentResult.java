package kr.hhplus.be.server.application.payment.response;

import lombok.Builder;

public class PaymentResult {

    @Builder
    public record Create(
            Long userId,
            Long reservationId
    ) {
        public static Create of(Long userId, Long reservationId) {
            return Create.builder()
                    .userId(userId)
                    .reservationId(reservationId)
                    .build();
        }
    }

}
