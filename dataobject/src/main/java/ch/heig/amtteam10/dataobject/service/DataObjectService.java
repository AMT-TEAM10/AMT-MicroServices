package ch.heig.amtteam10.dataobject.service;

import ch.heig.amtteam10.core.Env;
import ch.heig.amtteam10.dataobject.core.exceptions.BucketAlreadyCreatedException;
import ch.heig.amtteam10.dataobject.core.exceptions.NoObjectFoundException;
import ch.heig.amtteam10.dataobject.core.cloud.AWSClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;

@Service
public class DataObjectService {

    public ByteArrayResource getObject(String objectName) throws NoObjectFoundException {
        return new ByteArrayResource(AWSClient.getInstance().dataObject().get(objectName));
    }

    public String getObjectType(String objectName) throws NoObjectFoundException {
        return AWSClient.getInstance().dataObject().objectContentType(objectName);
    }

    public void createObject(String objectName, MultipartFile file) throws NoObjectFoundException, IOException {
        AWSClient.getInstance().dataObject().create(objectName, file.getBytes(), file.getContentType());
    }

    public void deleteObject(String objectName) throws NoObjectFoundException {
        AWSClient.getInstance().dataObject().delete(objectName);
    }

    public String getPublishLink(String objectName, Duration expirationTime) throws NoObjectFoundException {
        return AWSClient.getInstance().dataObject().publish(objectName, expirationTime);
    }

    public boolean createRootObject() {
        try {
            AWSClient.getInstance().dataObject().createRootObject(Env.get("AWS_BUCKET_NAME"));
            return true;
        } catch (BucketAlreadyCreatedException e) {
            return false;
        }
    }
}
