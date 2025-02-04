package kr.hhplus.be.server.domain.queue;

import kr.hhplus.be.server.common.exception.BadRequestException;

import static kr.hhplus.be.server.common.exception.ErrorMessage.USER_ID_INVALID;

public class WaitingQueueCommand {

    public record Create(
            Long userId
    ) {
        public void validate() {
            if (userId == null || userId <= 0) {
                throw new BadRequestException(USER_ID_INVALID.getMessage());
            }
        }
    }

}
