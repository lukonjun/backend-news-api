package org.comppress.customnewsapi.dto.article;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomArticleDto extends ArticleDto{

    @JsonProperty(value = "publisher_name")
    private String publisherName;
    @JsonProperty(value = "publisher_id")
    private Long publisherId;
    @JsonProperty(value = "category_id")
    private Long categoryId;
    @JsonProperty(value = "category_name")
    private String categoryName;

}
