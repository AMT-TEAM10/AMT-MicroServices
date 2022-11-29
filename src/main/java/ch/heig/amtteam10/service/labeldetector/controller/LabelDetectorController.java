package ch.heig.amtteam10.service.labeldetector.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LabelDetectorController {
    @GetMapping("/")
    public String index() {
        return "This is the label detector!";
    }
}
