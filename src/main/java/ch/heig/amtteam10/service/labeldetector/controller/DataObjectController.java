package ch.heig.amtteam10.service.labeldetector.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataObjectController {
    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}