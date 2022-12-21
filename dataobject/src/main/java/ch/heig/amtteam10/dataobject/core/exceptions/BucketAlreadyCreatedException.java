package ch.heig.amtteam10.dataobject.core.exceptions;

public class BucketAlreadyCreatedException extends DataObjectException {

    public BucketAlreadyCreatedException(String objectName) {
        super("Bucket " + objectName + " not found");
    }
}
