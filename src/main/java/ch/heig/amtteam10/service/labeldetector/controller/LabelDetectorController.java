package ch.heig.amtteam10.service.labeldetector.controller;

import ch.heig.amtteam10.core.cloud.AWSClient;
import ch.heig.amtteam10.core.cloud.Label;
import ch.heig.amtteam10.core.exceptions.FailDownloadFileException;
import ch.heig.amtteam10.service.labeldetector.dao.ProcessDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
public class LabelDetectorController {
    @PostMapping("/process")
    public @ResponseBody ResponseEntity<Label[]> processImage(@RequestBody ProcessDAO tmp) {
        try {
            var labels = AWSClient.getInstance().labelDetector().execute(tmp.imageUrl, tmp.maxLabels, tmp.minConfidence);
            return ResponseEntity.ok(labels);
        }
        catch (FailDownloadFileException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
