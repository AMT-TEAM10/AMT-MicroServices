package ch.heig.amtteam10.dataobject.dto;

public class ResponseDTO {
    private final boolean success;
    private final String message;

    public ResponseDTO(
            boolean success,
            String message
    ) {
        this.success = success;
        this.message = message;
    }

    public boolean success() {
        return success;
    }

    public String message() {
        return message;
    }
}
