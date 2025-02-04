package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.BadRequestException;
import lombok.Builder;

import static kr.hhplus.be.server.common.exception.ErrorMessage.*;

public class PointCommand {

    @Builder
    public record Charge(
        Long userId,
        int amount
    ) {
        public void validate() {
            if (userId == null || userId <= 0) {
                throw new BadRequestException(USER_ID_INVALID.getMessage());
            }
            if (amount <= 0) {
                throw new BadRequestException(POINT_CHARGE_AMOUNT_INVALID.getMessage());
            }
        }

        public static Charge of(Long userId, int amount) {
            return Charge.builder()
                    .userId(userId)
                    .amount(amount)
                    .build();
        }
    }

    @Builder
    public record Use(
        Long userId,
        int amount
    ) {
        public void validate() {
            if (userId == null || userId <= 0) {
                throw new BadRequestException(USER_ID_INVALID.getMessage());
            }
            if (amount <= 0) {
                throw new BadRequestException(POINT_USE_AMOUNT_INVALID.getMessage());
            }
        }

        public static Use of(Long userId, int amount) {
            return Use.builder()
                .userId(userId)
                .amount(amount)
                .build();
        }
    }

}
