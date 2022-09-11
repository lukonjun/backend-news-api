package org.comppress.customnewsapi.controller;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.GenericPage;
import org.comppress.customnewsapi.dto.PublisherDto;
import org.comppress.customnewsapi.service.publisher.PublisherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<GenericPage<PublisherDto>> getPublisher(
            @RequestParam(value = "lang", required = false, defaultValue = "en") String lang,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
        return publisherService.getPublisher(lang, page, size);
    }

    @GetMapping("/user")
    public ResponseEntity<GenericPage> getPublisherUser(
            @RequestParam(value = "lang", required = false, defaultValue = "en") String lang,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
        return publisherService.getPublisherUser(lang, page, size);
    }

}
