package kr.hhplus.be.server.interfaces;

import kr.hhplus.be.server.common.exception.BadRequestException;
import kr.hhplus.be.server.common.exception.ErrorResponse;
import kr.hhplus.be.server.common.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBindException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(ErrorResponse.of(400, BAD_REQUEST, e.getFieldErrors().get(0).getDefaultMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        return new ResponseEntity<>(ErrorResponse.of(400, BAD_REQUEST, e.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        return new ResponseEntity<>(ErrorResponse.of(400, BAD_REQUEST, e.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(ErrorResponse.of(404, NOT_FOUND, e.getMessage()), NOT_FOUND);
    }

}
