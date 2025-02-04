package kr.hhplus.be.server.interfaces.api.concert.response;

import kr.hhplus.be.server.domain.concert.model.ConcertScheduleInfo;
import lombok.Builder;

import java.time.LocalDateTime;

public class ConcertScheduleResponse {

    @Builder
    public record GetConcertSchedule(
            Long scheduleId,
            Long placeId,
            LocalDateTime scheduledAt
    ) {
        public static GetConcertSchedule from(ConcertScheduleInfo.GetConcertSchedule schedule) {
            return GetConcertSchedule.builder()
                    .scheduleId(schedule.scheduleId())
                    .placeId(schedule.placeId())
                    .scheduledAt(schedule.scheduledAt())
                    .build();
        }
    }

}
