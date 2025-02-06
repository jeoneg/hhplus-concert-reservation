package kr.hhplus.be.server.interfaces.api.concert.response;

import kr.hhplus.be.server.domain.concert.model.ConcertInfo;
import lombok.Builder;

public class ConcertResponse {

    @Builder
    public record GetConcert(
        Long id,
        String title
    ) {
        public static GetConcert from(ConcertInfo.GetConcert info) {
            return GetConcert.builder()
                .id(info.id())
                .title(info.title())
                .build();
        }
    }

}
