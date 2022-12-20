package ch.heig.amtteam10.dataobject.controller;

import ch.heig.amtteam10.dataobject.controller.error.APIError;
import ch.heig.amtteam10.dataobject.service.DataObjectService;
import ch.heig.amtteam10.core.exceptions.NoObjectFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// TODO: delete bucket
// TODO: objectName in query for paths

@RestController
@RequestMapping("v1/data-object")
public class DataObjectController {
    private final DataObjectService dataObjectService;

    @Autowired
    public DataObjectController(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    @PostMapping("/root-objects")
    public ResponseEntity<String> createRootObject() {
        if (dataObjectService.createRootObject()) {
            return ResponseEntity.status(HttpStatus.OK).body("Bucket created\n");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bucket already exists\n");
    }

    @DeleteMapping("/root-objects/{objectName:.+}")
    public ResponseEntity<?> deleteRootObject(@PathVariable String objectName) {
        // TODO
        return null;
    }


    @GetMapping("/objects/{objectName}")
    public ResponseEntity index(@PathVariable String objectName) {
        if (objectName.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIError(HttpStatus.BAD_REQUEST, "Missing objectName"));
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

    @PostMapping("/objects/{objectName}")
    public ResponseEntity create(@PathVariable String objectName, @RequestParam("file") MultipartFile file) {
        if (objectName.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIError(HttpStatus.BAD_REQUEST, "Missing objectName"));
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIError(HttpStatus.BAD_REQUEST, "Missing file"));
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

    @PatchMapping("/objects/{objectName}")
    public ResponseEntity update(@PathVariable String objectName, @RequestParam("file") MultipartFile file) {
        return create(objectName, file);
    }

    @DeleteMapping("/objects/{objectName}")
    public ResponseEntity delete(@PathVariable String objectName) throws NoObjectFoundException {
        if (objectName.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIError(HttpStatus.BAD_REQUEST, "Missing objectName"));
        }

        dataObjectService.deleteObject(objectName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/objects/{objectName}/publish")
    public ResponseEntity publish(@PathVariable String objectName) {
        if (objectName.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIError(HttpStatus.BAD_REQUEST, "Missing objectName"));
        }
        String url;
        try {
            url = dataObjectService.getPublishLink(objectName);
        } catch (NoObjectFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(url);
    }
}
