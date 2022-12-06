package ch.heig.amtteam10.labeldetector.core.exceptions;

public class FailDownloadFileException extends Exception{
    public FailDownloadFileException(String reason) {
        super(reason);
    }
}
