package kr.hhplus.be.server.common.time;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class SystemTimeProvider implements TimeProvider {

    private static final ZoneId KST_ZONE = ZoneId.of("Asia/Seoul");

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * KST 기준 현재 시간을 초 단위 Unix 타임스탬프로 반환
     */
    public long getCurrentTimestampInSecondsKST() {
        return ZonedDateTime.now(KST_ZONE).toEpochSecond();
    }

    /**
     * KST 기준 현재 시간을 밀리초 단위 Unix 타임스탬프로 반환
     */
    public long getCurrentTimestampInMillisKST() {
        return ZonedDateTime.now(KST_ZONE).toInstant().toEpochMilli();
    }

    /**
     * Unix 타임스탬프(초 단위)를 KST 기준 LocalDateTime으로 변환
     * @param epochSecond Unix 타임스탬프 (초 단위)
     */
    @Override
    public LocalDateTime fromEpochSecondKST(long epochSecond) {
        return Instant.ofEpochSecond(epochSecond)
            .atZone(KST_ZONE)
            .toLocalDateTime();
    }

    /**
     * Unix 타임스탬프(밀리초 단위)를 KST 기준 LocalDateTime으로 변환
     * @param epochMillis Unix 타임스탬프 (밀리초 단위)
     */
    @Override
    public LocalDateTime fromEpochMilliKST(long epochMillis) {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(KST_ZONE)
            .toLocalDateTime();
    }

}
