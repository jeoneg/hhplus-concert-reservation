package kr.hhplus.be.server.application.payment.request;

import kr.hhplus.be.server.common.exception.BadRequestException;
import lombok.Builder;

import static kr.hhplus.be.server.common.exception.ErrorMessage.RESERVATION_ID_INVALID;
import static kr.hhplus.be.server.common.exception.ErrorMessage.USER_ID_INVALID;

public class PaymentCriteria {

    @Builder
    public record Create(
        Long userId,
        Long reservationId,
        String token
    ) {
        public void validate() {
            if (userId == null || userId <= 0) {
                throw new BadRequestException(USER_ID_INVALID.getMessage());
            }
            if (reservationId == null || reservationId <= 0) {
                throw new BadRequestException(RESERVATION_ID_INVALID.getMessage());
            }
        }

        public static Create of(Long userId, Long reservationId, String token) {
            return Create.builder()
                    .userId(userId)
                    .reservationId(reservationId)
                    .token(token)
                    .build();
        }
    }

}
