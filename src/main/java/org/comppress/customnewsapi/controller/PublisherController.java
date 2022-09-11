package org.comppress.customnewsapi.controller;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.PublisherDto;
import org.comppress.customnewsapi.service.publisher.PublisherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<List<PublisherDto>> getPublisher(
            @RequestParam(value = "lang", required = false, defaultValue = "en") String lang
    ){
        return publisherService.getPublisher(lang);
    }

    @GetMapping("/user")
    public ResponseEntity<List<PublisherDto>> getPublisherUser(
            @RequestParam(value = "lang", required = false, defaultValue = "en") String lang
    ){
        return publisherService.getPublisherUser(lang);
    }

}
