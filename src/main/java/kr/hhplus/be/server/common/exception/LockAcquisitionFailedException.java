package kr.hhplus.be.server.common.exception;

public class LockAcquisitionFailedException extends RuntimeException{

    public LockAcquisitionFailedException() {
    }

    public LockAcquisitionFailedException(String message) {
        super(message);
    }

    public LockAcquisitionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
