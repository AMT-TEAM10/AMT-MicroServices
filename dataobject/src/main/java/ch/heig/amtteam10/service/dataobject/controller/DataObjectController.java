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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;

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

        ByteArrayResource resource;
        try {
            resource = new ByteArrayResource(AWSClient.getInstance().dataObject().get(objectName));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

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
        if (objectName.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            storageService.store(file, objectName);
            Path filepath = storageService.load(objectName);
            File objectFile = new File(filepath.toUri());
            AWSClient.getInstance().dataObject().create(objectName, objectFile);
            storageService.delete(objectName);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/object/{objectName}")
    public ResponseEntity<Resource> update(@PathVariable String objectName, @RequestParam("file") MultipartFile file) {
        return create(objectName, file);
    }

    @DeleteMapping("/object/{objectName}")
    public ResponseEntity<Resource> delete(@PathVariable String objectName) {
        if (objectName.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        AWSClient.getInstance().dataObject().delete(objectName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/object/{objectName}/publish")
    public ResponseEntity<?> publish(@PathVariable String objectName) {
        if (objectName.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String url;
        try {
            url = AWSClient.getInstance().dataObject().publish(objectName);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(url);
    }

    @ExceptionHandler(StorageNotFoundException.class)
    public ResponseEntity<HttpClientErrorException.NotFound> handleStorageFileNotFound(StorageNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
