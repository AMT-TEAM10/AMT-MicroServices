package ch.heig.amtteam10.dataobject.controller.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class APIError {
    @JsonFormat
    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime timestamp;

    @JsonFormat
    private String message;

    private APIError() {
        timestamp = LocalDateTime.now();
    }

    public APIError(HttpStatus status) {
        this();
        this.status = status;
        this.message = "Unexpected error";
    }

    public APIError(HttpStatus status, String message) {
        this(status);
        this.message = message;
    }
}