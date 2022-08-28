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
    @JsonProperty(value = "is_accessible")
    private boolean isAccessible;
    @JsonProperty(value = "scale_image")
    private boolean scaleImage;
    @JsonProperty("is_rated")
    private Boolean isRated;

}