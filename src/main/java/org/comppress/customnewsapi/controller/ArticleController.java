package org.comppress.customnewsapi.controller;

import org.comppress.customnewsapi.dto.GenericPage;
import org.comppress.customnewsapi.service.article.ArticleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/articles")
public class ArticleController {

    private final ArticleServiceImpl articleServiceImpl;

    @Autowired
    public ArticleController(ArticleServiceImpl articleServiceImpl) {
        this.articleServiceImpl = articleServiceImpl;
    }

    @GetMapping
    public ResponseEntity<GenericPage> getArticles(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "publisherNewsPaper", required = false) String publisherNewsPaper,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ){
        return articleServiceImpl.getArticles(page,size,title,category,publisherNewsPaper,fromDate,toDate);
    }

    @GetMapping("/rated")
    public ResponseEntity<GenericPage> getRatedArticles(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "publisherNewsPaper", required = false) String publisherNewsPaper,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ){
        return articleServiceImpl.getRatedArticles(page,size,title,category,publisherNewsPaper,fromDate,toDate);
    }

}