package org.comppress.customnewsapi.repository.article;

public interface CustomArticle extends Article{
    String getPublisherName();
    Long getPublisherId();
    Integer getCountComment();
    Long getCategoryId();
    String getCategoryName();
    Boolean getIsRated();
    Boolean getScaleImage();
}
