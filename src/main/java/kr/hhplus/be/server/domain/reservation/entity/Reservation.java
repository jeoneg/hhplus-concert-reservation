package kr.hhplus.be.server.domain.reservation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static kr.hhplus.be.server.domain.reservation.model.ReservationStatus.CONFIRMED;
import static kr.hhplus.be.server.domain.reservation.model.ReservationStatus.PAYMENT_PENDING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private Long concertId;
    private Long scheduleId;
    private Long seatId;
    private int paymentAmount;

    @Enumerated(STRING)
    private ReservationStatus status;

    @Builder
    public Reservation(Long id, Long userId, Long concertId, Long scheduleId, Long seatId, int paymentAmount, ReservationStatus status) {
        this.id = id;
        this.userId = userId;
        this.concertId = concertId;
        this.scheduleId = scheduleId;
        this.seatId = seatId;
        this.paymentAmount = paymentAmount;
        this.status = status;
    }

    public static Reservation create(Long userId, Long concertId, Long scheduleId, Long seatId, int paymentAmount) {
        return Reservation.builder()
                .userId(userId)
                .concertId(concertId)
                .scheduleId(scheduleId)
                .seatId(seatId)
                .paymentAmount(paymentAmount)
                .status(PAYMENT_PENDING)
                .build();
    }

    public void confirm() {
        this.status = CONFIRMED;
    }

}
