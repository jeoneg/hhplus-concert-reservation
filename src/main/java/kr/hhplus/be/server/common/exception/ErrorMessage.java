package kr.hhplus.be.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    QUEUE_TOKEN_VALUE_INVALID("유효하지 않은 토큰입니다."),
    QUEUE_TOKEN_NOT_FOUND("존재하지 않는 대기열 토큰입니다."),
    QUEUE_TOKEN_EXPIRED("만료된 대기열 토큰입니다."),

    CONCERT_ID_INVALID("유효하지 않은 콘서트 아이디입니다."),
    CONCERT_NOT_FOUND("존재하지 않는 콘서트입니다."),

    SCHEDULE_ID_INVALID("유효하지 않은 일정 아이디입니다."),
    SCHEDULE_NOT_FOUND("존재하지 않는 일정입니다."),
    SCHEDULE_LIST_EMPTY("일정 목록이 존재하지 않습니다."),

    SEAT_ID_INVALID("유효하지 않은 좌석 아이디입니다."),
    SEAT_NOT_FOUND("존재하지 않는 좌석입니다."),
    SEAT_ALREADY_RESERVED("이미 예약된 좌석입니다."),
    SEAT_TEMPORARY_RESERVED_EXPIRED("임시 배정 시간이 만료된 좌석입니다."),

    USER_ID_INVALID("유효하지 않은 사용자 아이디입니다."),
    USER_NOT_FOUND("존재하지 않는 사용자입니다."),

    RESERVATION_ID_INVALID("유효하지 않은 예약 아이디입니다."),
    RESERVATION_NOT_FOUND("존재하지 않는 예약입니다."),

    POINT_CHARGE_AMOUNT_INVALID("충전할 포인트는 0보다 커야합니다."),
    POINT_USE_AMOUNT_INVALID("사용할 포인트는 0보다 커야합니다."),
    POINT_NOT_FOUND("존재하지 않는 포인트 정보입니다."),
    POINT_NOT_ENOUGH("포인트가 부족합니다."),

    OUTBOX_NOT_FOUND("존재하지 않는 발신 메시지입니다."),
    ;

    private final String message;

}
