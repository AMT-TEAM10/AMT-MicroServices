package ch.heig.amtteam10.dataobject.service;

import ch.heig.amtteam10.core.Env;
import ch.heig.amtteam10.dataobject.core.AWSDataObject;
import ch.heig.amtteam10.dataobject.core.IDataObject;
import ch.heig.amtteam10.dataobject.core.exceptions.BucketAlreadyCreatedException;
import ch.heig.amtteam10.dataobject.core.exceptions.NoObjectFoundException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;

@Service
public class DataObjectService {

    private final IDataObject dataObjectHelper;

    public DataObjectService() {
        this.dataObjectHelper = new AWSDataObject();
    }

    public ByteArrayResource getObject(String objectName) throws NoObjectFoundException {
        return new ByteArrayResource(dataObjectHelper.get(objectName));
    }

    public String getObjectType(String objectName) throws NoObjectFoundException {
        return dataObjectHelper.objectContentType(objectName);
    }

    public void createObject(String objectName, MultipartFile file) throws NoObjectFoundException, IOException {
        dataObjectHelper.create(objectName, file.getBytes(), file.getContentType());
    }

    public void deleteObject(String objectName) throws NoObjectFoundException {
        dataObjectHelper.delete(objectName);
    }

    public String getPublishLink(String objectName, Duration expirationTime) throws NoObjectFoundException {
        return dataObjectHelper.publish(objectName, expirationTime);
    }

    public boolean createRootObject() {
        try {
            dataObjectHelper.createRootObject(Env.get("AWS_BUCKET_NAME"));
            return true;
        } catch (BucketAlreadyCreatedException e) {
            return false;
        }
    }
}
