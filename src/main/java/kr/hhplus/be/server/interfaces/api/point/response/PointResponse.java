package kr.hhplus.be.server.interfaces.api.point.response;

import kr.hhplus.be.server.domain.point.model.PointInfo;
import lombok.Builder;

public class PointResponse {

    @Builder
    public record GetPoint(
        Long userId,
        int balance
    ) {
        public static GetPoint from(PointInfo.GetPoint point) {
            return GetPoint.builder()
                .userId(point.userId())
                .balance(point.balance())
                .build();
        }
    }

    @Builder
    public record Charge(
        Long userId,
        int balance
    ) {
        public static Charge from(PointInfo.Charge point) {
            return Charge.builder()
                .userId(point.userId())
                .balance(point.balance())
                .build();
        }
    }

}
