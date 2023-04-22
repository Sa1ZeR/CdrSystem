package me.sa1zer.cdrsystem.crm.payload.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * class for custom exceptions with http status code
 */
public class ErrorMessageException extends RuntimeException {

    @Getter
    private final HttpStatus status;

    public ErrorMessageException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
