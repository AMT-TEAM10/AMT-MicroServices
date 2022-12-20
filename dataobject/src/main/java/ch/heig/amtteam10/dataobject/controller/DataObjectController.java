package ch.heig.amtteam10.dataobject.controller;

import ch.heig.amtteam10.dataobject.dto.ErrorResponseDTO;
import ch.heig.amtteam10.dataobject.dto.LinkDTO;
import ch.heig.amtteam10.dataobject.dto.ResponseDTO;
import ch.heig.amtteam10.dataobject.service.DataObjectService;
import ch.heig.amtteam10.core.exceptions.NoObjectFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

// TODO: delete bucket
// TODO: objectName in query for paths

@RestController
@RequestMapping("v1")
public class DataObjectController {
    private final DataObjectService dataObjectService;

    @Autowired
    public DataObjectController(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    @PostMapping("/root-objects")
    public ResponseEntity<ResponseDTO> createRootObject() {
        if (dataObjectService.createRootObject()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true, "Root object created"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(false, "Root object already exists"));
    }



    @GetMapping("/objects/{objectName}")
    public ResponseEntity<?> index(@PathVariable String objectName) {
        if (objectName.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(HttpStatus.BAD_REQUEST, "Missing objectName"));
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
    public ResponseEntity<ResponseDTO> create(@PathVariable String objectName, @RequestParam("file") MultipartFile file) {
        if (objectName.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(HttpStatus.BAD_REQUEST, "Missing objectName"));
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(HttpStatus.BAD_REQUEST, "Missing file"));
        }

        try {
            dataObjectService.createObject(objectName, file);
        } catch (NoObjectFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(true, "Object created"));
    }

    @PatchMapping("/objects/{objectName}")
    public ResponseEntity update(@PathVariable String objectName, @RequestParam("file") MultipartFile file) {
        return create(objectName, file);
    }

    @DeleteMapping("/objects/{objectName}")
    public ResponseEntity delete(@PathVariable String objectName) throws NoObjectFoundException {
        if (objectName.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(HttpStatus.BAD_REQUEST, "Missing objectName"));
        }

        dataObjectService.deleteObject(objectName);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(true, "Object deleted"));
    }

    @GetMapping("/objects/{objectName}/publish")
    public ResponseEntity<?> publish(@PathVariable String objectName, @RequestParam("expiration") int expirationTime) {
        if (objectName.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(HttpStatus.BAD_REQUEST, "Missing objectName"));
        }

        String url;
        try {
            url = dataObjectService.getPublishLink(objectName, expirationTime);
        } catch (NoObjectFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(expirationTime);
        return ResponseEntity.status(HttpStatus.OK).body(new LinkDTO(url, expirationDate));
    }
}
