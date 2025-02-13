package kr.hhplus.be.server.domain.point.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.BadRequestException;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static kr.hhplus.be.server.common.exception.ErrorMessage.*;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Table(name = "point", indexes = @Index(name = "idx_user_id", columnList = "user_id"))
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Point extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
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
