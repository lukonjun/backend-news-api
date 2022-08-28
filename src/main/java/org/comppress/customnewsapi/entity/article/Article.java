package org.comppress.customnewsapi.entity.article;

public interface Article {
    Long getId();
    String getAuthor();
    String getTitle();
    String getDescription();
    String getUrl();
    String getUrlToImage();
    String getPublishedAt();
    Integer getCountRatings();
    Integer getCountComment();
    Boolean getIsAccessible();
    Boolean getScaleImage();
    Boolean getIsRated();
}
