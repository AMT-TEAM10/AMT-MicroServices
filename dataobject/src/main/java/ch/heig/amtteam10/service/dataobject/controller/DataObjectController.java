package ch.heig.amtteam10.service.dataobject.controller;

import ch.heig.amtteam10.service.dataobject.service.DataObjectService;
import ch.heig.amtteam10.core.exceptions.NoObjectFoundException;
import ch.heig.amtteam10.service.dataobject.service.storage.StorageNotFoundException;
import ch.heig.amtteam10.service.dataobject.service.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class DataObjectController {
    private final StorageService storageService;

    private final DataObjectService dataObjectService;

    @Autowired
    public DataObjectController(StorageService storageService, DataObjectService dataObjectService) {
        this.storageService = storageService;
        this.dataObjectService = dataObjectService;
    }

    @PutMapping("/root-object")
    public ResponseEntity<String> createRootObject() {
        if (dataObjectService.createRootObject()) {
            return ResponseEntity.status(HttpStatus.OK).body("Bucket created\n");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bucket already exists\n");
    }

    @GetMapping("/object/{objectName}")
    public ResponseEntity<Resource> index(@PathVariable String objectName) {
        if (objectName.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ByteArrayResource resource;
        try {
            resource = dataObjectService.getObject(objectName);
        } catch (NoObjectFoundException e) {
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
            dataObjectService.createObject(objectName, file);
        } catch (NoObjectFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/object/{objectName}")
    public ResponseEntity<Resource> update(@PathVariable String objectName, @RequestParam("file") MultipartFile file) {
        return create(objectName, file);
    }

    @DeleteMapping("/object/{objectName}")
    public ResponseEntity<Resource> delete(@PathVariable String objectName) throws NoObjectFoundException {
        if (objectName.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        dataObjectService.deleteObject(objectName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/object/{objectName}/publish")
    public ResponseEntity<?> publish(@PathVariable String objectName) {
        if (objectName.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String url;
        try {
            url = dataObjectService.getPublishLink(objectName);
        } catch (NoObjectFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(url);
    }

    @ExceptionHandler(StorageNotFoundException.class)
    public ResponseEntity<HttpClientErrorException.NotFound> handleStorageFileNotFound(StorageNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
