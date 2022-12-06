package ch.heig.amtteam10.core.exceptions;

public class FailDownloadFileException extends Exception {
    public FailDownloadFileException(String reason) {
        super(reason);
    }
}
