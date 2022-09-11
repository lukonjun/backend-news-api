package org.comppress.customnewsapi.controller;

import org.comppress.customnewsapi.dto.article.CustomArticleDto;
import org.comppress.customnewsapi.dto.GenericPage;
import org.comppress.customnewsapi.exceptions.AuthenticationException;
import org.comppress.customnewsapi.service.article.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<GenericPage<CustomArticleDto>> getArticles(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size", defaultValue = "100") int size,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "publisherNewsPaper", required = false) String publisherNewsPaper,
            @RequestParam(value = "lang", required = false, defaultValue = "en") String lang,
            @RequestParam(value = "isAccessible", required = false, defaultValue = "false") Boolean isAccessible,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ) {
        return articleService.getArticles(page, size, title, category, publisherNewsPaper, lang, isAccessible, fromDate, toDate);
    }

    @GetMapping("/unrated")
    public ResponseEntity<GenericPage<CustomArticleDto>> getArticlesNotRated(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size", defaultValue = "100") int size,
            @RequestParam(value = "categoryId") Long categoryId,
            @RequestParam(value = "listPublisherIds", required = false) List<Long> listPublisherIds,
            @RequestParam(value = "lang", required = false, defaultValue = "en") String lang,
            @RequestParam(value = "isAccessible", required = false, defaultValue = "false") Boolean isAccessible,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ) {
        return articleService.getArticlesNotRated(page, size, categoryId, listPublisherIds, lang, isAccessible, fromDate, toDate);
    }

    @GetMapping("/rated")
    public ResponseEntity<GenericPage> getRatedArticles(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size", defaultValue = "100") int size,
            @RequestParam(value = "categoryId") Long categoryId,
            @RequestParam(value = "listPublisherIds", required = false) List<Long> listPublisherIds,
            @RequestParam(value = "lang", required = false, defaultValue = "en") String lang,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate,
            @RequestParam(value = "isAccessible", required = false, defaultValue = "false") Boolean isAccessible,
            @RequestParam(value = "guid", required = false) String guid
    ) {
        try {
            return articleService.getRatedArticles(page, size, categoryId, listPublisherIds, lang, fromDate, toDate, isAccessible, guid);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/rated/user")
    public ResponseEntity<GenericPage> getRatedArticlesFromUser(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size", defaultValue = "100") int size,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ) {
        return articleService.getRatedArticlesFromUser(page, size, fromDate, toDate);
    }


}