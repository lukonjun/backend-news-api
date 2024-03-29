package org.comppress.customnewsapi.dto.article;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleDto {

    private Long id;
    private String author;
    private String title;
    private String description;
    private String url;
    @JsonProperty(value = "url_to_image")
    private String urlToImage;
    @JsonProperty(value = "published_at")
    private LocalDateTime publishedAt;
    @JsonProperty(value = "count_ratings")
    private Integer countRatings;
    @JsonProperty(value = "count_comment")
    private Integer countComment;
    @JsonProperty(value = "paywall_article")
    private Boolean paywallArticle;
    @JsonProperty(value = "scale_image")
    private Boolean scaleImage;
    @JsonProperty("is_rated_by_user")
    private Boolean isRatedByUser;

}