package kr.hhplus.be.server.domain.point.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import kr.hhplus.be.server.common.exception.BadRequestException;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static kr.hhplus.be.server.common.exception.ErrorMessage.*;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Point extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private int balance;

    @Builder
    public Point(Long id, Long userId, int balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
    }

    public void charge(int amount) {
        if (amount <= 0) {
            throw new BadRequestException(POINT_CHARGE_AMOUNT_INVALID.getMessage());
        }
        this.balance += amount;
    }

    public void use(int amount) {
        if (amount <= 0) {
            throw new BadRequestException(POINT_USE_AMOUNT_INVALID.getMessage());
        }
        if (this.balance - amount < 0) {
            throw new BadRequestException(POINT_NOT_ENOUGH.getMessage());
        }
        this.balance -= amount;
    }

}
