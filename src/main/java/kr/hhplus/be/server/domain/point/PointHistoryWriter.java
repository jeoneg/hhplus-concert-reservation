package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.entity.PointHistory;

public interface PointHistoryWriter {

    PointHistory save(PointHistory pointHistory);

}
