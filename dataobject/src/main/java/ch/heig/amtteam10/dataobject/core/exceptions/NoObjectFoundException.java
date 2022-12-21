package ch.heig.amtteam10.dataobject.core.exceptions;

public class NoObjectFoundException extends DataObjectException {
    public NoObjectFoundException(String objectName) {
        super("Object " + objectName + " not found");
    }
}
