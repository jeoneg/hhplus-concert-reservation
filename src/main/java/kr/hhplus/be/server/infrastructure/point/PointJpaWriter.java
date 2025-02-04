package kr.hhplus.be.server.infrastructure.point;

import kr.hhplus.be.server.domain.point.PointWriter;
import kr.hhplus.be.server.domain.point.entity.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointJpaWriter implements PointWriter {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }

}
