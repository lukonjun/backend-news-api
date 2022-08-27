package org.comppress.customnewsapi.repository.article;

public interface Article {
    Long getId();
    String getAuthor();
    String getTitle();
    String getDescription();
    String getUrl();
    String getUrlToImage();
    String getPublishedAt();
    Integer getCountRatings();
    Boolean getIsAccessible();
}
