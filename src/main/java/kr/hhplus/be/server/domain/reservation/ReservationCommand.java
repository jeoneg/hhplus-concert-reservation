package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.common.exception.BadRequestException;
import kr.hhplus.be.server.common.exception.ErrorMessage;
import lombok.Builder;

public class ReservationCommand {

    @Builder
    public record Create(
            Long userId,
            Long concertId,
            Long scheduleId,
            Long seatId
    ) {
        public void validate() {
            if (userId == null || userId <= 0) {
                throw new BadRequestException(ErrorMessage.USER_ID_INVALID.getMessage());
            }
            if (concertId == null || concertId <= 0) {
                throw new BadRequestException(ErrorMessage.CONCERT_ID_INVALID.getMessage());
            }
            if (scheduleId == null || scheduleId <= 0) {
                throw new BadRequestException(ErrorMessage.SCHEDULE_ID_INVALID.getMessage());
            }
            if (seatId == null || seatId <= 0) {
                throw new BadRequestException(ErrorMessage.SEAT_ID_INVALID.getMessage());
            }
        }

        public static Create of(Long userId, Long concertId, Long scheduleId, Long seatId) {
            return Create.builder()
                    .userId(userId)
                    .concertId(concertId)
                    .scheduleId(scheduleId)
                    .seatId(seatId)
                    .build();
        }
    }

}
