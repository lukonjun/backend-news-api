package org.comppress.customnewsapi.scheduler;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.comppress.customnewsapi.entity.Article;
import org.comppress.customnewsapi.repository.ArticleRepository;
import org.comppress.customnewsapi.service.article.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.URISyntaxException;

@Configuration
@EnableScheduling
@Slf4j
public class PaywallUpdateScheduler {

    @Value("${scheduler.paywall.enabled}")
    private boolean enabled;
    @Value("${scheduler.paywall.page-size}")
    private int pageSize;

    private final ArticleRepository articleRepository;
    private final ArticleService articleService;

    @Autowired
    public PaywallUpdateScheduler(ArticleRepository articleRepository, ArticleService articleService) {
        this.articleRepository = articleRepository;
        this.articleService = articleService;
    }

    @Scheduled(fixedDelayString = "${scheduler.paywall.triggeringIntervalMilliSeconds}",
            initialDelayString = "${scheduler.paywall.initialDelayIntervalMilliSeconds}")
    @SchedulerLock(name = "paywallScheduler")
    public void saveNewsFeed() throws URISyntaxException, IOException {
        if(enabled){
            log.info("Paywall Scheduler Running!");
            Page<Article> articleList = articleRepository.findByIsAccessibleUpdatedFalse(PageRequest.of(0, pageSize));
            for (Article article : articleList.toList()) {
                articleService.update(article);
            }
        }
    }


}
