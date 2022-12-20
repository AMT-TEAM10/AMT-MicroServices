package ch.heig.amtteam10.dataobject.service;

import ch.heig.amtteam10.core.Env;
import ch.heig.amtteam10.core.cloud.AWSClient;
import ch.heig.amtteam10.core.exceptions.BucketAlreadyCreatedException;
import ch.heig.amtteam10.core.exceptions.NoObjectFoundException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DataObjectService {

    public ByteArrayResource getObject(String objectName) throws NoObjectFoundException {
        return new ByteArrayResource(AWSClient.getInstance().dataObject().get(objectName));
    }

    public void createObject(String objectName, MultipartFile file) throws NoObjectFoundException, IOException {
        AWSClient.getInstance().dataObject().create(objectName, file.getBytes());
    }

    public void deleteObject(String objectName) throws NoObjectFoundException {
        AWSClient.getInstance().dataObject().delete(objectName);
    }

    public String getPublishLink(String objectName, int expirationTime) throws NoObjectFoundException {
        return AWSClient.getInstance().dataObject().publish(objectName, expirationTime);
    }

    public boolean createRootObject() {
        try {
            AWSClient.getInstance().dataObject().createRootObject(Env.get("AWS_BUCKET_NAME"));
            return true;
        } catch(BucketAlreadyCreatedException e) {
            return false;
        }
    }
}
