package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.entity.Point;

import java.util.Optional;

public interface PointReader {

    Optional<Point> findByUserId(Long userId);

    Optional<Point> findByUserIdWithLock(Long userId);

}
