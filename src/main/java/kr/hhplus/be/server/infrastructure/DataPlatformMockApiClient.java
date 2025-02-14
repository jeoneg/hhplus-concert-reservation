package kr.hhplus.be.server.infrastructure;

import org.springframework.stereotype.Component;

@Component
public class DataPlatformMockApiClient {

    public String sendData() {
//        throw new IllegalStateException("데이터 플랫폼 데이터 전송 실패!!");
        return "데이터 플랫폼 데이터 전송 성공!!";
    }

}
