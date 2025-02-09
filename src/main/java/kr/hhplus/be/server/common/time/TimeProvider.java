package kr.hhplus.be.server.common.time;

import java.time.LocalDateTime;

public interface TimeProvider {

    LocalDateTime now();

    long getCurrentTimestampInSecondsKST();

    long getCurrentTimestampInMillisKST();

    LocalDateTime fromEpochSecondKST(long epochSecond);

    LocalDateTime fromEpochMilliKST(long epochMillis);

}
