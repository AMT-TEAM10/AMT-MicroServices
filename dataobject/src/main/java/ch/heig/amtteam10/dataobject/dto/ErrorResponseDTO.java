package ch.heig.amtteam10.dataobject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

class APIError {
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

public class ErrorResponseDTO extends ResponseDTO {
    private final APIError error;

    public ErrorResponseDTO(
            HttpStatus status,
            String message
    ) {
        super(false, message);
        this.error = new APIError(status, message);
    }

    public APIError error() {
        return error;
    }

}
