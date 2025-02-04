package kr.hhplus.be.server.interfaces.api.queue.response;

import kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus;
import kr.hhplus.be.server.domain.queue.model.WaitingQueueInfo;
import lombok.Builder;

import java.time.LocalDateTime;

public class WaitingQueueResponse {

    @Builder
    public record Create(
            Long id,
            Long userId,
            String token,
            WaitingQueueStatus status,
            LocalDateTime createdAt
    ) {
        public static Create from(WaitingQueueInfo.Create info) {
            return Create.builder()
                    .id(info.id())
                    .userId(info.userId())
                    .token(info.token())
                    .status(info.status())
                    .createdAt(info.createdAt())
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
            Long order
    ) {
        public static GetWaitingQueue from(WaitingQueueInfo.GetWaitingQueue info) {
            return GetWaitingQueue.builder()
                    .id(info.id())
                    .userId(info.userId())
                    .token(info.token())
                    .status(info.status())
                    .activatedAt(info.activatedAt())
                    .expiredAt(info.expiredAt())
                    .createdAt(info.createdAt())
                    .modifiedAt(info.modifiedAt())
                    .order(info.waitingNumber())
                    .build();
        }
    }

}
