package kr.hhplus.be.server.domain.concert.model;

import kr.hhplus.be.server.domain.concert.entity.Concert;
import lombok.Builder;

public class ConcertInfo {

    @Builder
    public record GetConcert(
        Long id,
        String title
    ) {
        public static GetConcert from(Concert concert) {
            return GetConcert.builder()
                .id(concert.getId())
                .title(concert.getTitle())
                .build();
        }
    }

}
