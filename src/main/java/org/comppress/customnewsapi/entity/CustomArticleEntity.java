package org.comppress.customnewsapi.entity;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class CustomArticleEntity extends Article {

    private String publisherName;
    private Long publisherId;
    private Long countComment;
    private Long categoryId;
    private String categoryName;
    private Boolean isRated;
    private Boolean scaleImage;

}