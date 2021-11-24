package org.comppress.customnewsapi.controller;

import org.comppress.customnewsapi.dto.CustomRatedArticleDto;
import org.comppress.customnewsapi.dto.GenericPage;
import org.comppress.customnewsapi.dto.UserPreferenceDto;
import org.comppress.customnewsapi.service.home.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/home")
public class HomeController {

    private final HomeService homeService;

    @Autowired
    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public ResponseEntity<UserPreferenceDto> getUserPreference(@RequestParam(defaultValue = "en") String lang,
                                                               @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                                               @RequestParam(value = "publisherIds", required = false) List<Long> publisherIds,
                                                               @RequestParam(value = "fromDate", required = false) String fromDate,
                                                               @RequestParam(value = "toDate", required = false) String toDate

    ){
        return homeService.getUserPreference(lang,categoryIds,publisherIds,fromDate,toDate);
    }

    @GetMapping("/category")
    public ResponseEntity<GenericPage<CustomRatedArticleDto>> getArticleForCategory(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
            @RequestParam(value = "publisherIds", required = false) List<Long> publisherIds,
            @RequestParam(value = "lang", required = false, defaultValue = "en") String lang,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ){
        return homeService.getArticleForCategory(page,size,categoryIds,publisherIds,lang,fromDate,toDate);
    }

}
