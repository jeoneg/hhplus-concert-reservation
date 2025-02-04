package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.payment.request.PaymentCriteria;
import kr.hhplus.be.server.application.payment.response.PaymentResult;
import kr.hhplus.be.server.domain.concert.SeatService;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.queue.WaitingQueueService;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.model.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static kr.hhplus.be.server.domain.reservation.model.ReservationStatus.PAYMENT_PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentFacadeTest {

    @InjectMocks
    private PaymentFacade sut;

    @Mock
    private ReservationService reservationService;

    @Mock
    private SeatService seatService;

    @Mock
    private PointService pointService;

    @Mock
    private WaitingQueueService waitingQueueService;

    @Test
    void 특정_예약_정보를_결제할_때_결제에_성공하면_결제_정보를_반환한다() {
        // given
        Long reservationId = 1L;
        Long userId = 2L;
        Long concertId = 3L;
        Long scheduleId = 4L;
        Long seatId = 5L;
        ReservationStatus status = PAYMENT_PENDING;
        int paymentAmount = 130000;
        String token = UUID.randomUUID().toString();

        ReservationInfo.GetReservation reservation = ReservationInfo.GetReservation.builder()
                .id(reservationId)
                .userId(userId)
                .concertId(concertId)
                .scheduleId(scheduleId)
                .seatId(seatId)
                .status(status)
                .paymentAmount(paymentAmount)
                .build();
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);

        // when
        PaymentResult.Create result = sut.payment(PaymentCriteria.Create.of(userId, reservationId, token));

        // then
        assertThat(result.reservationId()).isEqualTo(reservationId);
        assertThat(result.userId()).isEqualTo(userId);

        verify(reservationService, times(1)).getReservation(reservationId);
        verify(seatService, times(1)).checkExpiredTemporaryReserved(seatId);
        verify(pointService, times(1)).use(any(PointCommand.Use.class));
        verify(reservationService, times(1)).confirm(reservationId);
        verify(waitingQueueService, times(1)).expire(token);
    }

}