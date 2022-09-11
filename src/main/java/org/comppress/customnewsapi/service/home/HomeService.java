package org.comppress.customnewsapi.service.home;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.comppress.customnewsapi.dto.CustomCategoryDto;
import org.comppress.customnewsapi.dto.article.CustomArticleDto;
import org.comppress.customnewsapi.dto.article.CustomRatedArticleDto;
import org.comppress.customnewsapi.dto.GenericPage;
import org.comppress.customnewsapi.entity.AbstractEntity;
import org.comppress.customnewsapi.entity.CategoryEntity;
import org.comppress.customnewsapi.entity.PublisherEntity;
import org.comppress.customnewsapi.entity.UserEntity;
import org.comppress.customnewsapi.entity.article.CustomArticle;
import org.comppress.customnewsapi.mapper.MapstructMapper;
import org.comppress.customnewsapi.repository.ArticleRepository;
import org.comppress.customnewsapi.repository.CategoryRepository;
import org.comppress.customnewsapi.repository.PublisherRepository;
import org.comppress.customnewsapi.repository.UserRepository;
import org.comppress.customnewsapi.entity.article.CustomRatedArticle;
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
    private final MapstructMapper mapstructMapper;

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

    public ResponseEntity<GenericPage> getHome(int page, int size, String lang, List<Long> categoryIds,
                                               List<Long> publisherIds, String fromDate, String toDate, Boolean filterOutPaywallArticles) {
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
                finalPubIds, DateUtils.stringToLocalDateTime(finalFromDate), DateUtils.stringToLocalDateTime(toDate), filterOutPaywallArticles)).collect(Collectors.toList());
        return PageHolderUtils.getResponseEntityGenericPage(page,size,customCategoryDtos);
    }

    private CustomCategoryDto setArticles(CategoryEntity category, String lang,
                                          List<Long> publisherIds, LocalDateTime fromDate, LocalDateTime toDate, Boolean filterOutPaywallArticles) {
        // TODO Limit 1, Publishers included, Rated
        if (publisherIds == null || publisherIds.isEmpty()) {
            publisherIds = publisherRepository.findByLang(lang).stream().map(AbstractEntity::getId).collect(Collectors.toList());
        }
        Long categoryId = category.getId();
        CustomRatedArticle article = articleRepository.retrieveOneRatedArticleByCategoryIdsAndPublisherIdsAndLanguageAndLimit(categoryId,publisherIds,lang,fromDate,toDate,filterOutPaywallArticles);
        CustomCategoryDto customCategoryDto = new CustomCategoryDto();
        if(article != null){
            CustomRatedArticleDto customRatedArticleDto = mapstructMapper.customRatedArticleToCustomRatedArticleDto(article);
            customCategoryDto.setArticle(customRatedArticleDto);
            twitterService.setReplyCount(customRatedArticleDto);
        } else {
            CustomArticle customArticle = articleRepository.retrieveLatestArticleOfCategory(categoryId, filterOutPaywallArticles, publisherIds);
            CustomArticleDto customArticleDto = mapstructMapper.customArticleToCustomArticleDto(customArticle);
            customCategoryDto.setArticle(customArticleDto);
        }

        BeanUtils.copyProperties(category, customCategoryDto);
        return customCategoryDto;
    }

    private boolean doesNotContainAnyCategoriesFromLang(List<Long> listCategoryIds, String lang) {
        for(CategoryEntity category:categoryRepository.findByLang(lang)){
            if(listCategoryIds.contains(category.getId())) return false;
        }
        return true;
    }

    private boolean doesNotContainAnyPublishersFromLang(List<Long> listPublisherIds, String lang) {
        for (PublisherEntity publisher : publisherRepository.findByLang(lang)) {
            if (listPublisherIds.contains(publisher.getId())) return false;
        }
        return true;
    }

}
