package ch.heig.amtteam10.core.exceptions;

public class NoObjectFoundException extends Exception {
    public NoObjectFoundException(String objectName) {
        super("Object " + objectName + " not found");
    }
}
