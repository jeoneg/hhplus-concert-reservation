package kr.hhplus.be.server.domain.point.model;

import kr.hhplus.be.server.domain.point.entity.Point;
import lombok.Builder;

public class PointInfo {

    @Builder
    public record GetPoint(
        Long id,
        Long userId,
        int balance
    ) {
        public static GetPoint from(Point point) {
            return GetPoint.builder()
                .id(point.getId())
                .userId(point.getUserId())
                .balance(point.getBalance())
                .build();
        }
    }

    @Builder
    public record Charge(
        Long id,
        Long userId,
        int balance
    ) {
        public static Charge from(Point point) {
            return Charge.builder()
                .id(point.getId())
                .userId(point.getUserId())
                .balance(point.getBalance())
                .build();
        }
    }

    @Builder
    public record Use(
        Long id,
        Long userId,
        int balance
    ) {
        public static Use from(Point point) {
            return Use.builder()
                .id(point.getId())
                .userId(point.getUserId())
                .balance(point.getBalance())
                .build();
        }
    }

}
