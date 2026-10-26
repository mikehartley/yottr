package uk.co.yottr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class IllegalOperationException extends RuntimeException {
    public IllegalOperationException(String message) {
        super(message);
    }
}
