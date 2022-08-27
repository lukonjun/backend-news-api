package org.comppress.customnewsapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/dummy")
public class DummyController {

    @GetMapping("message")
    public String getMessage() {
        return "Hello World";
    }

}
