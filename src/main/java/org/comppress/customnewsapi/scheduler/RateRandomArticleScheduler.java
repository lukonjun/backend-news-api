package org.comppress.customnewsapi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.comppress.customnewsapi.dto.CriteriaRatingDto;
import org.comppress.customnewsapi.dto.SubmitRatingDto;
import org.comppress.customnewsapi.entity.ArticleEntity;
import org.comppress.customnewsapi.entity.CriteriaEntity;
import org.comppress.customnewsapi.repository.ArticleRepository;
import org.comppress.customnewsapi.repository.CriteriaRepository;
import org.comppress.customnewsapi.service.rating.RatingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class RateRandomArticleScheduler {

    @Value("${scheduler.rate-random-article.enabled}")
    private boolean enabled;

    private final ArticleRepository articleRepository;
    private final RatingService ratingService;
    private final CriteriaRepository criteriaRepository;

    @Scheduled(fixedDelayString = "${scheduler.rate-random-article.triggeringIntervalMilliSeconds}",
            initialDelayString = "${scheduler.rate-random-article.initialDelayIntervalMilliSeconds}")
    @SchedulerLock(name = "rateRandomArticleScheduler")
    public void saveNewsFeed(){
        try {
            if(enabled){
                log.info("Random Article Scheduler Running!");
                ArticleEntity article = articleRepository.retrieveOneRandomArticleIntervalOneDay();
                if(article == null) return;
                String guid = "RandomArticleSchedulerGuid";
                Random random = new Random();

                List<CriteriaEntity> criteriaList = criteriaRepository.findAll();
                SubmitRatingDto submitRatingDto = new SubmitRatingDto();
                List<CriteriaRatingDto> criteriaRatingDtoList = new ArrayList<>();
                for (CriteriaEntity criteria : criteriaList) {
                    CriteriaRatingDto criteriaRatingDto = new CriteriaRatingDto();
                    criteriaRatingDto.setRating(random.nextInt(5) + 1);
                    criteriaRatingDto.setCriteriaId(criteria.getId());
                    criteriaRatingDtoList.add(criteriaRatingDto);
                }
                submitRatingDto.setRatings(criteriaRatingDtoList);
                submitRatingDto.setArticleId(article.getId());
                ratingService.submitRating(submitRatingDto,guid);
                log.info("Random Article Scheduler Running: Rating article with id {}, date_created {}", article.getId(),article.getDateCreated());
            }
        } catch (Throwable e){
            log.error("Scheduled Task error", e);
        }
    }

}