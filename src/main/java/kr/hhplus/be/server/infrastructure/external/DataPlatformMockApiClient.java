package kr.hhplus.be.server.infrastructure.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataPlatformMockApiClient {

    public void sendData() {
        log.info("예약 내역 데이터 플랫폼 전송 성공!!");
    }

}
