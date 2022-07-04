package org.comppress.customnewsapi.service.home;

import lombok.extern.slf4j.Slf4j;
import org.comppress.customnewsapi.dto.CustomCategoryDto;
import org.comppress.customnewsapi.dto.CustomRatedArticleDto;
import org.comppress.customnewsapi.dto.GenericPage;
import org.comppress.customnewsapi.entity.AbstractEntity;
import org.comppress.customnewsapi.entity.Category;
import org.comppress.customnewsapi.entity.Publisher;
import org.comppress.customnewsapi.entity.UserEntity;
import org.comppress.customnewsapi.repository.ArticleRepository;
import org.comppress.customnewsapi.repository.CategoryRepository;
import org.comppress.customnewsapi.repository.PublisherRepository;
import org.comppress.customnewsapi.repository.UserRepository;
import org.comppress.customnewsapi.service.BaseSpecification;
import org.comppress.customnewsapi.service.twitter.TwitterService;
import org.comppress.customnewsapi.utils.DateUtils;
import org.comppress.customnewsapi.utils.PageHolderUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class HomeServiceImpl implements HomeService, BaseSpecification {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PublisherRepository publisherRepository;
    private final TwitterService twitterService;

    @Autowired
    public HomeServiceImpl(ArticleRepository articleRepository, CategoryRepository categoryRepository, UserRepository userRepository, PublisherRepository publisherRepository, TwitterService twitterService) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.publisherRepository = publisherRepository;
        this.twitterService = twitterService;
    }

    public List<Long> getPublisher(List<Long> publisherIds, String lang){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findByUsernameAndDeletedFalse(authentication.getName());

        if(userEntity != null){
            if(userEntity.getListPublisherIds() != null){
                publisherIds = Stream.of(userEntity.getListPublisherIds().split(",")).map(Long::parseLong).collect(Collectors.toList());
            }
        }
        if (publisherIds == null || publisherIds.isEmpty() || doesNotContainAnyPublishersFromLang(publisherIds,lang)) {
            publisherIds = publisherRepository.findByLang(lang).stream().map(publisher -> publisher.getId()).collect(Collectors.toList());
        }
        return publisherIds;
    }

    public List<Long> getCategory(List<Long> categoryIds, String lang){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findByUsernameAndDeletedFalse(authentication.getName());

        if(userEntity != null){
            if(userEntity.getListCategoryIds() != null){
                categoryIds = Stream.of(userEntity.getListCategoryIds().split(",")).map(Long::parseLong).collect(Collectors.toList());
            }
        }

        if (categoryIds == null || categoryIds.isEmpty() || doesNotContainAnyCategoriesFromLang(categoryIds,lang)) {
            categoryIds = categoryRepository.findByLang(lang).stream().map(category -> category.getId()).collect(Collectors.toList());
        }
        return categoryIds;
    }

    @Override
    public ResponseEntity<GenericPage> getUserPreference(int page,int size,String lang, List<Long> categoryIds,
                                                               List<Long> publisherIds, String fromDate, String toDate, Boolean isAccessible) {
        /*
        // if Date not set, retrieve results for last 24 hours
        if(fromDate == null && toDate == null){
            Instant instant = Instant.now().minus(24, ChronoUnit.HOURS);
            Timestamp timestamp = Timestamp.from(instant);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            fromDate = timestamp.toLocalDateTime().format(formatter);
            log.info(fromDate);
        }
         */

        final List<Long> finalPubIds = getPublisher(publisherIds, lang);
        categoryIds = getCategory(categoryIds,lang);

        String finalFromDate = fromDate;
        List<CustomCategoryDto> customCategoryDtos = categoryRepository.
                findByCategoryIds(categoryIds).stream().map(s -> setArticles(s, lang,
                finalPubIds, DateUtils.stringToLocalDateTime(finalFromDate), DateUtils.stringToLocalDateTime(toDate), isAccessible)).collect(Collectors.toList());
        return PageHolderUtils.getResponseEntityGenericPage(page,size,customCategoryDtos);
    }

    private CustomCategoryDto setArticles(Category category, String lang,
                                          List<Long> publisherIds, LocalDateTime fromDate, LocalDateTime toDate, Boolean isAccessible) {
        // TODO Limit 1, Publishers included, Rated
        if (publisherIds == null || publisherIds.isEmpty()) {
            publisherIds = publisherRepository.findByLang(lang).stream().map(AbstractEntity::getId).collect(Collectors.toList());
        }
        Long categoryId = category.getId();
        ArticleRepository.CustomRatedArticle article = articleRepository.retrieveArticlesByCategoryIdsAndPublisherIdsAndLanguageAndLimit(categoryId,publisherIds,lang,fromDate,toDate,isAccessible);
        CustomCategoryDto customCategoryDto = new CustomCategoryDto();
        if(article != null){
            CustomRatedArticleDto customRatedArticleDto = new CustomRatedArticleDto();
            twitterService.setReplyCount(customRatedArticleDto);
            BeanUtils.copyProperties(article, customRatedArticleDto);
            if (article.getCount_comment() == null) {
                customRatedArticleDto.setCount_comment(0);
            }
            customCategoryDto.setArticle(customRatedArticleDto);
        }
        /*
        else {
            ArticleRepository.CustomRatedArticle article2 = articleRepository.nQSelectLatestArticle(categoryId);
            if(article2 == null) {
                customCategoryDto.setArticle(null);
            }else{
                // Use Builder method, write custom in the DTO Object, this way it should work?!
                // What is happening here? TwitterService.setReplyCount(customRatedArticleDto);
                CustomRatedArticleDto customRatedArticleDto = CustomRatedArticleDto.builder()
                        .author(article2.getAuthor())
                        .title(article2.getTitle())
                        .description(article2.getDescription())
                        .url(article2.getUrl())
                        .article_id(article2.getArticle_id())
                        .url_to_image(article2.getUrl_to_image())
                        .published_at(article2.getPublished_at())
                        .is_accessible(article2.getIs_accessible())
                        .publisher_name(article2.getPublisher_name())
                        .publisher_id(article2.getPublisher_id())
                        .count_comment(article2.getCount_comment())
                        .category_id(article2.getCategory_id())
                        .category_name(article2.getCategory_name())
                        .isRated(false)
                        .scale_image(article2.getScale_image())
                        .build();

                customCategoryDto.setArticle(customRatedArticleDto);
            }
        }
         */
        BeanUtils.copyProperties(category, customCategoryDto);
        return customCategoryDto;
    }

    private boolean doesNotContainAnyCategoriesFromLang(List<Long> listCategoryIds, String lang) {
        for(Category category:categoryRepository.findByLang(lang)){
            if(listCategoryIds.contains(category.getId())) return false;
        }
        return true;
    }

    private boolean doesNotContainAnyPublishersFromLang(List<Long> listPublisherIds, String lang) {
        for (Publisher publisher : publisherRepository.findByLang(lang)) {
            if (listPublisherIds.contains(publisher.getId())) return false;
        }
        return true;
    }

}
