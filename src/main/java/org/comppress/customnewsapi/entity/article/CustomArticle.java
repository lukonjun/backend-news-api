package org.comppress.customnewsapi.entity.article;

public interface CustomArticle extends Article{
    String getPublisherName();
    Long getPublisherId();
    Long getCategoryId();
    String getCategoryName();
}
