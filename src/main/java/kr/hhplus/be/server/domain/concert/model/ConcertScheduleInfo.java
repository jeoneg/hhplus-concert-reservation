package kr.hhplus.be.server.domain.concert.model;

import kr.hhplus.be.server.domain.concert.entity.ConcertSchedule;
import lombok.Builder;

import java.time.LocalDateTime;

public class ConcertScheduleInfo {

    @Builder
    public record GetConcertSchedule(
            Long scheduleId,
            Long placeId,
            LocalDateTime scheduledAt
    ) {
        public static GetConcertSchedule from(ConcertSchedule schedule) {
            return GetConcertSchedule.builder()
                    .scheduleId(schedule.getId())
                    .placeId(schedule.getPlaceId())
                    .scheduledAt(schedule.getScheduledAt())
                    .build();
        }
    }

}
