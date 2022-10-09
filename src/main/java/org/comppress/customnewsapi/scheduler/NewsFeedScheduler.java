package org.comppress.customnewsapi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.comppress.customnewsapi.service.ArticleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class NewsFeedScheduler {

    @Value("${scheduler.news-feed.enabled}")
    private boolean enabled;

    private final ArticleService articleService;

    @Scheduled(fixedDelayString = "${scheduler.news-feed.triggeringIntervalMilliSeconds}",
            initialDelayString = "${scheduler.news-feed.initialDelayIntervalMilliSeconds}")
    @SchedulerLock(name = "newsFeedingScheduler")
    public void saveNewsFeed(){
        try {
            if(enabled){
                log.info("News Scheduler Running!");
                articleService.fetchArticlesFromRssFeeds();
            }
        } catch (Throwable e){
            log.error("Scheduled Task error", e);
        }
    }

}
