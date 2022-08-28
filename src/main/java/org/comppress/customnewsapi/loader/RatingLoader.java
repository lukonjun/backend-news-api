package org.comppress.customnewsapi.loader;

import lombok.extern.slf4j.Slf4j;
import org.comppress.customnewsapi.repository.ArticleRepository;
import org.comppress.customnewsapi.repository.RatingRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RatingLoader implements ApplicationRunner {

    private final RatingRepository ratingRepository;
    private final ArticleRepository articleRepository;

    public RatingLoader(RatingRepository ratingRepository, ArticleRepository articleRepository) {
        this.ratingRepository = ratingRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        /*
        List<Article> articleList;
        if(articleRepository.findAll() == null || articleRepository.findAll().isEmpty()){
            log.info("No Articles in the Database, exiting RatingLoader now");
            return;
        } else {
            articleList = articleRepository.findAll();
        }
        int numberRandomRatings = 1000;
        Random random = new Random();
        for (int i = 0; i < numberRandomRatings; i++) {
            Article article = articleList.get(random.nextInt(articleList.size()));
            // 3 Rating Criteria
            for(long j = 0; j < 3; j++){

                // TODO Implement Randomness, should be sometimes not filled, 0 or null, but one needs to be definitly set
                Rating rating = Rating.builder()
                        .userId(-1L)
                        .criteriaId(j)
                        .articleId(article.getId())
                        .rating(random.nextInt(5) + 1)
                        .build();
                ratingRepository.save(rating);
            }
        }
        */
    }

}
