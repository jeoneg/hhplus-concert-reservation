package kr.hhplus.be.server.domain.concert.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue
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
