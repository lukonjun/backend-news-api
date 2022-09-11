package org.comppress.customnewsapi.mapper;

import org.comppress.customnewsapi.dto.*;
import org.comppress.customnewsapi.dto.article.CustomArticleDto;
import org.comppress.customnewsapi.dto.article.CustomRatedArticleDto;
import org.comppress.customnewsapi.dto.xml.ItemDto;
import org.comppress.customnewsapi.entity.*;
import org.comppress.customnewsapi.entity.article.CustomArticle;
import org.comppress.customnewsapi.entity.article.CustomRatedArticle;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapstructMapper {

    @Mapping(source = "link", target = "url")
    @Mapping(source = "enclosure.url", target = "urlToImage")
    @Mapping(source = "encoded", target = "content")
    @Mapping(source = "pubDate", target = "publishedAt")
    ArticleEntity itemDtoToArticle(ItemDto itemDto);

    RatingEntity submitRatingDtoToRating(SubmitRatingDto submitRatingDto);
    RatingDto ratingToRatingDto(RatingEntity rating);

    CriteriaDto criteriaToCriteriaDto(CriteriaEntity criteria);
    CategoryDto categoryToCategoryDto(CategoryEntity category);
    PublisherDto publisherToPublisherDto(PublisherEntity publisher);

    @Mapping(source = "publishedAt", target = "publishedAt", dateFormat = "yyyy-MM-dd HH:mm:ss.S")
    CustomRatedArticleDto customRatedArticleToCustomRatedArticleDto(CustomRatedArticle customRatedArticle);

    @Mapping(source = "publishedAt", target = "publishedAt", dateFormat = "yyyy-MM-dd HH:mm:ss.S")
    CustomArticleDto customArticleToCustomArticleDto(CustomArticle s);

    CustomCategoryDto categoryEntityToCustomCategoryDto(CategoryEntity category);


}
