package kr.hhplus.be.server.domain.queue.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus.*;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class WaitingQueue extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long userId;
    private String token;

    @Enumerated(STRING)
    private WaitingQueueStatus status;

    private LocalDateTime activatedAt;
    private LocalDateTime expiredAt;

    @Builder
    public WaitingQueue(Long id, Long userId, String token, WaitingQueueStatus status, LocalDateTime activatedAt, LocalDateTime expiredAt) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.status = status;
        this.activatedAt = activatedAt;
        this.expiredAt = expiredAt;
    }

    public static WaitingQueue of(Long userId, String token, WaitingQueueStatus status) {
        return WaitingQueue.builder()
                .userId(userId)
                .token(token)
                .status(status)
                .build();
    }

    public boolean isWaiting() {
        return status == WAITING;
    }

    public boolean isExpired() {
        return status == EXPIRED || (status == ACTIVATED && expiredAt.isAfter(LocalDateTime.now()));
    }

    public void activate(LocalDateTime activatedAt) {
        this.status = ACTIVATED;
        this.activatedAt = activatedAt;
        this.expiredAt = activatedAt.plusMinutes(10);
    }

    public void expire() {
        this.status = EXPIRED;
    }

}
