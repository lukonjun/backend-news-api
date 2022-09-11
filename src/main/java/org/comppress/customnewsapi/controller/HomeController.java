package org.comppress.customnewsapi.controller;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.GenericPage;
import org.comppress.customnewsapi.service.home.HomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<GenericPage> getUserPreference(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(defaultValue = "en") String lang,
            @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
            @RequestParam(value = "publisherIds", required = false) List<Long> publisherIds,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate,
            @RequestParam(value = "isAccessible", required = false, defaultValue = "false") Boolean isAccessible
    ) {
        return homeService.getHome(page, size, lang, categoryIds, publisherIds, fromDate, toDate, isAccessible);
    }

}
