package kr.hhplus.be.server.domain.concert.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Table(name = "concert_schedule", indexes = @Index(name = "idx_concert_id", columnList = "concert_id"))
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)

    private Long id;
    private Long concertId;
    private Long placeId;
    private LocalDateTime scheduledAt;

    @Builder
    public ConcertSchedule(Long id, Long concertId, Long placeId, LocalDateTime scheduledAt) {
        this.id = id;
        this.concertId = concertId;
        this.placeId = placeId;
        this.scheduledAt = scheduledAt;
    }

}
