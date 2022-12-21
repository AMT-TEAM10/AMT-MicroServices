package ch.heig.amtteam10.core.exceptions;

public class BucketAlreadyCreatedException extends Exception {

    public BucketAlreadyCreatedException(String objectName) {
        super("Bucket " + objectName + " not found");
    }
}
