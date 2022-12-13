package ch.heig.amtteam10.service.dataobject.service;

import ch.heig.amtteam10.core.Env;
import ch.heig.amtteam10.core.cloud.AWSClient;
import ch.heig.amtteam10.core.exceptions.NoObjectFoundException;
import ch.heig.amtteam10.service.dataobject.service.storage.StorageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;

import java.io.File;
import java.nio.file.Path;

@Service
public class DataObjectService {

    public ByteArrayResource getObject(String objectName) throws NoObjectFoundException {
        return new ByteArrayResource(AWSClient.getInstance().dataObject().get(objectName));
    }

    public void createObject(String objectName, MultipartFile file, StorageService storageService) throws NoObjectFoundException {
        storageService.store(file, objectName);
        Path filepath = storageService.load(objectName);
        File objectFile = new File(filepath.toUri());
        AWSClient.getInstance().dataObject().create(objectName, objectFile);
        storageService.delete(objectName);
    }

    public void deleteObject(String objectName) throws NoObjectFoundException {
        AWSClient.getInstance().dataObject().delete(objectName);
    }

    public String getPublishLink(String objectName) throws NoObjectFoundException {
        return AWSClient.getInstance().dataObject().publish(objectName);
    }

    public boolean createRootObject() {
        try {
            AWSClient.getInstance().dataObject().createRootObject(Env.get("AWS_BUCKET_NAME"));
            return true;
        } catch(BucketAlreadyExistsException e) {
            return false;
        }
    }
}
