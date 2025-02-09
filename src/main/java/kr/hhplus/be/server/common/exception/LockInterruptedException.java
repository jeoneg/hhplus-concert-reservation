package kr.hhplus.be.server.common.exception;

public class LockInterruptedException extends RuntimeException {

    public LockInterruptedException() {
    }

    public LockInterruptedException(String message) {
        super(message);
    }

    public LockInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

}
