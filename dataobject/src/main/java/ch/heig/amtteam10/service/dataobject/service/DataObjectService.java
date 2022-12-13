package ch.heig.amtteam10.service.dataobject.service;

import ch.heig.amtteam10.core.cloud.AWSClient;
import ch.heig.amtteam10.service.dataobject.service.storage.StorageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;

@Service
public class DataObjectService {

    public ByteArrayResource getObject(String objectName) {
        return new ByteArrayResource(AWSClient.getInstance().dataObject().get(objectName));
    }

    public void createObject(String objectName, MultipartFile file, StorageService storageService) {
        storageService.store(file, objectName);
        Path filepath = storageService.load(objectName);
        File objectFile = new File(filepath.toUri());
        AWSClient.getInstance().dataObject().create(objectName, objectFile);
        storageService.delete(objectName);
    }

    public void deleteObject(String objectName) {
        AWSClient.getInstance().dataObject().delete(objectName);
    }

    public String getPublishLink(String objectName) {
        return AWSClient.getInstance().dataObject().publish(objectName);
    }
}
