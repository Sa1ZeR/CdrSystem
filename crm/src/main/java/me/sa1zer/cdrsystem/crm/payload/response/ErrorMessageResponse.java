package me.sa1zer.cdrsystem.crm.payload.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorMessageResponse {

    private final String message;
    private final String error;
    private final int status;

    public ErrorMessageResponse(HttpStatus status, String message) {
        this.message = message;
        error = status.name();
        this.status = status.value();
    }
}
