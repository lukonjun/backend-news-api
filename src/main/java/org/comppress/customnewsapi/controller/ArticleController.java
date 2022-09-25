package org.comppress.customnewsapi.controller;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            @RequestParam(value = "filterOutPaywallArticles", required = false, defaultValue = "false") Boolean filterOutPaywallArticles,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ) {
        log.info("Request Parameter for /articles");
        log.info("page: {}, size: {}, title: {}, category: {}, publisherNewsPaper: {}, lang: {}, filterOutPaywallArticles: {}, fromDate: {}, toDate: {}",
                page, size, title, category, publisherNewsPaper, lang, filterOutPaywallArticles, fromDate, toDate);
        return articleService.getArticles(page, size, title, category, publisherNewsPaper, lang, filterOutPaywallArticles, fromDate, toDate);
    }

    @GetMapping("/unrated")
    public ResponseEntity<GenericPage<CustomArticleDto>> getArticlesNotRated(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size", defaultValue = "100") int size,
            @RequestParam(value = "categoryId") Long categoryId,
            @RequestParam(value = "listPublisherIds", required = false) List<Long> listPublisherIds,
            @RequestParam(value = "lang", required = false, defaultValue = "en") String lang,
            @RequestParam(value = "filterOutPaywallArticles", required = false, defaultValue = "false") Boolean filterOutPaywallArticles,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ) {
        log.info("Request Parameter for /articles/unrated");
        log.info("page: {}, size: {}, categoryId: {}, listPublisherIds: {}, lang: {}, filterOutPaywallArticles: {}, fromDate: {}, toDate: {}",
                page, size, categoryId, listPublisherIds, lang, filterOutPaywallArticles, fromDate, toDate);
        return articleService.getArticlesNotRated(page, size, categoryId, listPublisherIds, lang, filterOutPaywallArticles, fromDate, toDate);
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
            @RequestParam(value = "filterOutPaywallArticles", required = false, defaultValue = "false") Boolean filterOutPaywallArticles,
            @RequestParam(value = "guid", required = false) String guid
    ) {
        try {
            log.info("Request Parameter for /articles/rated");
            log.info("page: {}, size: {}, categoryId: {}, listPublisherIds: {}, lang: {}, fromDate: {}, toDate: {}, filterOutPaywallArticles: {}, guid: {}",
                    page, size, categoryId, listPublisherIds, lang, fromDate, toDate, filterOutPaywallArticles, guid);
            return articleService.getRatedArticles(page, size, categoryId, listPublisherIds, lang, fromDate, toDate, filterOutPaywallArticles, guid);
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
        log.info("Request Parameter for /articles/rated/user");
        log.info("page: {}, size: {}, fromDate: {}, toDate: {}",
                page, size, fromDate, toDate);

        return articleService.getRatedArticlesFromUser(page, size, fromDate, toDate);
    }


}