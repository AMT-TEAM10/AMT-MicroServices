package ch.heig.amtteam10.service.dataobject.controller;

import ch.heig.amtteam10.core.cloud.AWSClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class DataObjectController {
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
    public ResponseEntity<Resource> create(@PathVariable String objectName) {

        // AWSClient.getInstance().dataObject().create(objectName, );

    }
}
