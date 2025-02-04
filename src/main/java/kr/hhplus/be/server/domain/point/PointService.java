package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.domain.point.entity.Point;
import kr.hhplus.be.server.domain.point.entity.PointHistory;
import kr.hhplus.be.server.domain.point.model.PointInfo;
import kr.hhplus.be.server.domain.user.UserReader;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.common.exception.ErrorMessage.POINT_NOT_FOUND;
import static kr.hhplus.be.server.common.exception.ErrorMessage.USER_NOT_FOUND;
import static kr.hhplus.be.server.domain.point.entity.TransactionType.CHARGE;
import static kr.hhplus.be.server.domain.point.entity.TransactionType.USE;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PointService {

    private final UserReader userReader;
    private final PointReader pointReader;
    private final PointWriter pointWriter;
    private final PointHistoryWriter pointHistoryWriter;

    public PointInfo.GetPoint getPoint(Long userId) {
        User user = userReader.findById(userId)
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        Point point = pointReader.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(POINT_NOT_FOUND.getMessage()));
        return PointInfo.GetPoint.from(point);
    }

    @Transactional
    public PointInfo.Charge charge(PointCommand.Charge command) {
        command.validate();

        User user = userReader.findById(command.userId())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        Point point = pointReader.findByUserIdWithLock(command.userId())
                .orElseThrow(() -> new NotFoundException(POINT_NOT_FOUND.getMessage()));
        point.charge(command.amount());
        Point savedPoint = pointWriter.save(point);

        pointHistoryWriter.save(PointHistory.create(point.getId(), command.amount(), CHARGE));

        log.info("포인트 충전 성공: 사용자 ID = {}, 충전 금액 = {}, 잔액 = {}", user.getId(), command.amount(), savedPoint.getBalance());
        return PointInfo.Charge.from(savedPoint);
    }

    @Transactional
    public PointInfo.Use use(PointCommand.Use command) {
        command.validate();

        User user = userReader.findById(command.userId())
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        Point point = pointReader.findByUserIdWithLock(command.userId())
                .orElseThrow(() -> new NotFoundException(POINT_NOT_FOUND.getMessage()));
        point.use(command.amount());
        Point savedPoint = pointWriter.save(point);

        pointHistoryWriter.save(PointHistory.create(point.getId(), command.amount(), USE));

        log.info("포인트 사용 성공: 사용자 ID = {}, 사용 금액 = {}, 잔액 = {}", user.getId(), command.amount(), savedPoint.getBalance());
        return PointInfo.Use.from(savedPoint);
    }

}
