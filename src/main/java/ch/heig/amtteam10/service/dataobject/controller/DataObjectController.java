package ch.heig.amtteam10.service.dataobject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataObjectController {
    @GetMapping("/")
    public String index() {
        return "This is the DataObject controller !";
    }
}
