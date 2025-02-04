package kr.hhplus.be.server.interfaces.api.queue.request;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.queue.WaitingQueueCommand;

public class WaitingQueueRequest {

    public record Create(
            @NotNull(message = "사용자 아이디는 필수입니다.")
            Long userId
    ) {
        public WaitingQueueCommand.Create toCommand() {
            return new WaitingQueueCommand.Create(userId);
        }
    }

}
