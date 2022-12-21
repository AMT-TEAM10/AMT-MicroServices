package ch.heig.amtteam10.labeldetector.controller;

import ch.heig.amtteam10.labeldetector.dto.ProcessDTO;
import ch.heig.amtteam10.labeldetector.core.Label;
import ch.heig.amtteam10.labeldetector.core.exceptions.FailDownloadFileException;
import ch.heig.amtteam10.labeldetector.dto.LabelResultDTO;
import ch.heig.amtteam10.labeldetector.service.LabelDetectorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("v1")
public class LabelDetectorController {

    private LabelDetectorService labelDetectorService;

    public LabelDetectorController(LabelDetectorService labelDetectorService) {
        this.labelDetectorService = labelDetectorService;
    }

    @PostMapping("/labels")
    public @ResponseBody ResponseEntity<LabelResultDTO> processImage(@Valid @RequestBody ProcessDTO params) {
        try {
            Label[] labels = labelDetectorService.executeLabelDetection(params);
            var response = new LabelResultDTO(labels.length, params.imageUrl(), labels);
            return ResponseEntity.ok(response);
        } catch (FailDownloadFileException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
