package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.payment.request.PaymentCriteria;
import kr.hhplus.be.server.application.payment.response.PaymentResult;
import kr.hhplus.be.server.domain.concert.SeatService;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.queue.WaitingQueueService;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.model.ReservationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final ReservationService reservationService;
    private final SeatService seatService;
    private final PointService pointService;
    private final WaitingQueueService waitingQueueService;

    @Transactional
    public PaymentResult.Create payment(PaymentCriteria.Create criteria) {
        criteria.validate();

        ReservationInfo.GetReservation reservation = reservationService.getReservation(criteria.reservationId());
        seatService.checkExpiredTemporaryReserved(reservation.seatId());
        pointService.use(PointCommand.Use.of(criteria.userId(), reservation.paymentAmount()));
        reservationService.confirm(reservation.id());
        waitingQueueService.expire(criteria.token());

        return PaymentResult.Create.of(criteria.userId(), reservation.id());
    }

}
