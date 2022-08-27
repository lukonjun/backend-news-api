package org.comppress.customnewsapi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.comppress.customnewsapi.repository.TwitterRepository;
import org.comppress.customnewsapi.service.twitter.TwitterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class TwitterCommentCountScheduler {

    @Value("${scheduler.twitter.enabled}")
    private boolean enabled;

    private final TwitterRepository twitterRepository;
    private final TwitterService twitterService;

    @Scheduled(fixedDelayString = "${scheduler.twitter.triggeringIntervalMilliSeconds}",
            initialDelayString = "${scheduler.twitter.initialDelayIntervalMilliSeconds}")
    @SchedulerLock(name = "twitterScheduler")
    public void saveNewsFeed() {
        if(enabled){
            log.info("Twitter Scheduler Running!");
            twitterService.getTweetDetails();
        }
    }

}
