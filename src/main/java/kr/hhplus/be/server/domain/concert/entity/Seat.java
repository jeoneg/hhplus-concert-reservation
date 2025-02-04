package kr.hhplus.be.server.domain.concert.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static kr.hhplus.be.server.domain.concert.model.SeatStatus.RESERVED;
import static kr.hhplus.be.server.domain.concert.model.SeatStatus.TEMPORARY_RESERVED;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Seat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long placeId;

    @Enumerated(STRING)
    private SeatStatus status;

    private LocalDateTime expiredAt;
    private int price;

    @Version
    private Long version;

    @Builder
    public Seat(int price, LocalDateTime expiredAt, SeatStatus status, Long placeId, Long id) {
        this.price = price;
        this.expiredAt = expiredAt;
        this.status = status;
        this.placeId = placeId;
        this.id = id;
    }

    public boolean isReserved() {
        return this.status == TEMPORARY_RESERVED || this.status == RESERVED;
    }

    public void temporaryReserve(LocalDateTime now) {
        this.status = TEMPORARY_RESERVED;
        this.expiredAt = now.plusMinutes(5);
    }

    public void confirm() {
        this.status = RESERVED;
    }

    public boolean isExpiredTemporaryReserved(LocalDateTime now) {
        return this.expiredAt.isBefore(now);
    }

}
