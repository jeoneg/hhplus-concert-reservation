package kr.hhplus.be.server.domain.point.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class PointHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long userId;
    private Long pointId;
    private int amount;

    @Enumerated(STRING)
    private TransactionType type;

    @Builder
    public PointHistory(Long id, Long userId, Long pointId, int amount, TransactionType type) {
        this.id = id;
        this.userId = userId;
        this.pointId = pointId;
        this.amount = amount;
        this.type = type;
    }

    public static PointHistory create(Long pointId, int amount, TransactionType type) {
        return PointHistory.builder()
            .pointId(pointId)
            .amount(amount)
            .type(type)
            .build();
    }

}
