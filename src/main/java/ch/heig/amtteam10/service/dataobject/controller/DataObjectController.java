package ch.heig.amtteam10.service.dataobject.controller;

import ch.heig.amtteam10.core.cloud.AWSClient;
import ch.heig.amtteam10.service.dataobject.service.storage.StorageNotFoundException;
import ch.heig.amtteam10.service.dataobject.service.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
public class DataObjectController {
    private final StorageService storageService;

    Logger logger = LoggerFactory.getLogger(DataObjectController.class);

    @Autowired
    public DataObjectController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/object/{objectName}")
    public ResponseEntity<Resource> index(@PathVariable String objectName) {
        if (objectName.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ByteArrayResource resource = new ByteArrayResource(AWSClient.getInstance().dataObject().get(objectName));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(objectName)
                                .build().toString())
                .body(resource);
    }

    @PostMapping("/object/{objectName}")
    public ResponseEntity<Resource> create(@PathVariable String objectName, @RequestParam("file") MultipartFile file) {
        logger.error(file.getOriginalFilename());
        try {
            storageService.store(file, objectName);
            var objectFile = storageService.loadAsResource(objectName);
            AWSClient.getInstance().dataObject().create(objectName, (File) objectFile);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO: PATCH object/:objectName

    // TODO: DELETE object/:objectName

    // TODO: GET object/:objectName/publish

    @ExceptionHandler(StorageNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
