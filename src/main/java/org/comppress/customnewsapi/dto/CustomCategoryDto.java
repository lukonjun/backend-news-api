package org.comppress.customnewsapi.dto;

import lombok.Data;
import org.comppress.customnewsapi.dto.article.ArticleDto;

@Data
public class CustomCategoryDto {

    private String name;
    private Long id;
    private ArticleDto article;

}
