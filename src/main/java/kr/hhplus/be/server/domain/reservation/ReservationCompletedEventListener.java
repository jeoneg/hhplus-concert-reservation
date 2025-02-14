package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.infrastructure.DataPlatformMockApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCompletedEventListener {

    private final DataPlatformMockApiClient dataPlatformMockApiClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationCompletedEventHandler(ReservationCompletedEvent event) {
        try {
            log.info("예약 완료 내역 데이터 플랫폼 전송: 사용자 ID = {}, 좌석 ID = {}, 예약 ID = {}", event.userId(), event.seatId(), event.id());
            String response = dataPlatformMockApiClient.sendData();
            log.info("예약 완료 내역 데이터 플랫폼 전송 결과: {}", response);
        } catch (Exception e) {
            log.error("예약 완료 내역 데이터 플랫폼 전송 오류: {}", e.getMessage());
        }
    }

}
