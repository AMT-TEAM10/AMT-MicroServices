package ch.heig.amtteam10.dataobject.dto;

public class ResponseDTO {
    private boolean success;
    private String message;

    public ResponseDTO(
            boolean success,
            String message
    ) {
        this.success = success;
        this.message = message;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
