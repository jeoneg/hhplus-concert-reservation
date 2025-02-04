package kr.hhplus.be.server.common.exception;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ErrorResponse(
        int code,
        HttpStatus status,
        String message
) {

    public static ErrorResponse of(int code, HttpStatus status, String message) {
        return ErrorResponse.builder()
                .code(code)
                .status(status)
                .message(message)
                .build();
    }

}
