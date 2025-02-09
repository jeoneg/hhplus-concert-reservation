package kr.hhplus.be.server.domain.queue.model;

import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public class WaitingQueueInfo {

    @Builder
    public record Create(
            Long id,
            Long userId,
            String token,
            WaitingQueueStatus status,
            LocalDateTime createdAt
    ) {
        public static Create from(WaitingQueue waitingQueue) {
            return Create.builder()
                    .id(waitingQueue.getId())
                    .userId(waitingQueue.getId())
                    .token(waitingQueue.getToken())
                    .status(waitingQueue.getStatus())
                    .createdAt(waitingQueue.getCreatedAt())
                    .build();
        }
    }

    @Builder
    public record GetWaitingQueue(
            Long id,
            Long userId,
            String token,
            WaitingQueueStatus status,
            LocalDateTime activatedAt,
            LocalDateTime expiredAt,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt,
            Long waitingNumber
    ) {
        public static GetWaitingQueue from(WaitingQueue waitingQueue) {
            return GetWaitingQueue.builder()
                    .id(waitingQueue.getId())
                    .userId(waitingQueue.getUserId())
                    .token(waitingQueue.getToken())
                    .status(waitingQueue.getStatus())
                    .activatedAt(waitingQueue.getActivatedAt())
                    .expiredAt(waitingQueue.getExpiredAt())
                    .createdAt(waitingQueue.getCreatedAt())
                    .modifiedAt(waitingQueue.getModifiedAt())
                    .build();
        }

        public static GetWaitingQueue of(WaitingQueue waitingQueue) {
            return GetWaitingQueue.builder()
                    .id(waitingQueue.getId())
                    .userId(waitingQueue.getUserId())
                    .token(waitingQueue.getToken())
                    .status(waitingQueue.getStatus())
                    .activatedAt(waitingQueue.getActivatedAt())
                    .expiredAt(waitingQueue.getExpiredAt())
                    .createdAt(waitingQueue.getCreatedAt())
                    .modifiedAt(waitingQueue.getModifiedAt())
                    .waitingNumber(waitingQueue.getWaitingNumber())
                    .build();
        }
    }

}
