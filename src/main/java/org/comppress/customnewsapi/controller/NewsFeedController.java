package org.comppress.customnewsapi.controller;

import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.service.article.ArticleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
public class NewsFeedController {

    private final ArticleService articleService;

    @Value("${scheduler.news-feed.enabled}")
    private boolean enabled;

    @GetMapping
    public ResponseEntity<String> startFeed() {
        if (!enabled) {
            articleService.fetchArticlesFromRssFeeds();
        }
        return ResponseEntity.ok().body("Fetched News");
    }
}
