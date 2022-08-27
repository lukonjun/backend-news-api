package org.comppress.customnewsapi.service.home;

import lombok.RequiredArgsConstructor;
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
import org.comppress.customnewsapi.repository.article.CustomRatedArticle;
import org.comppress.customnewsapi.service.BaseSpecification;
import org.comppress.customnewsapi.service.twitter.TwitterService;
import org.comppress.customnewsapi.utils.DateUtils;
import org.comppress.customnewsapi.utils.PageHolderUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class HomeService implements BaseSpecification {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PublisherRepository publisherRepository;
    private final TwitterService twitterService;

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

    public ResponseEntity<GenericPage> getUserPreference(int page,int size,String lang, List<Long> categoryIds,
                                                               List<Long> publisherIds, String fromDate, String toDate, Boolean isAccessible) {
        // if Date not set, retrieve results for last 24 hours
        if(fromDate == null && toDate == null){
            Instant instant = Instant.now().minus(24, ChronoUnit.HOURS);
            Timestamp timestamp = Timestamp.from(instant);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            fromDate = timestamp.toLocalDateTime().format(formatter);
            log.info(fromDate);
        }

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
        CustomRatedArticle article = articleRepository.retrieveArticlesByCategoryIdsAndPublisherIdsAndLanguageAndLimit(categoryId,publisherIds,lang,fromDate,toDate,isAccessible);
        CustomCategoryDto customCategoryDto = new CustomCategoryDto();
        if(article != null){
            CustomRatedArticleDto customRatedArticleDto = new CustomRatedArticleDto();
            twitterService.setReplyCount(customRatedArticleDto);
            BeanUtils.copyProperties(article, customRatedArticleDto);
            if (article.getCountComment() == null) {
                customRatedArticleDto.setCount_comment(0);
            }
            customCategoryDto.setArticle(customRatedArticleDto);
        } else {
            CustomRatedArticle article2 = articleRepository.nQSelectLatestArticle(categoryId);
            if(article2 == null) {
                customCategoryDto.setArticle(null);
            }else{
                // Use Builder method, write custom in the DTO Object, this way it should work?!
                // What is happening here? TwitterService.setReplyCount(customRatedArticleDto);
                // TODO Use Mapper Class here maybe? Will Reduce the Code by a lot
                CustomRatedArticleDto customRatedArticleDto = CustomRatedArticleDto.builder()
                        .author(article2.getAuthor())
                        .title(article2.getTitle())
                        .description(article2.getDescription())
                        .url(article2.getUrl())
                        .article_id(article2.getArticleId())
                        .url_to_image(article2.getUrlToImage())
                        .published_at(article2.getPublishedAt())
                        .is_accessible(article2.getIsAccessible())
                        .publisher_name(article2.getPublisherName())
                        .publisher_id(article2.getPublisherId())
                        .count_comment(article2.getCountComment())
                        .category_id(article2.getCategoryId())
                        .category_name(article2.getCategoryName())
                        .isRated(false)
                        .scale_image(article2.getScaleImage())
                        .build();

                customCategoryDto.setArticle(customRatedArticleDto);
            }
        }

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
