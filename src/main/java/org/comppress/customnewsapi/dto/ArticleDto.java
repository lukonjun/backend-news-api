package org.comppress.customnewsapi.dto;

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
    @JsonProperty(value = "is_accessible")
    private boolean isAccessible;

}