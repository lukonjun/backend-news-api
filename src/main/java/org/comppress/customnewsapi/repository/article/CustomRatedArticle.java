package org.comppress.customnewsapi.repository.article;

public interface CustomRatedArticle extends Article{
    Long getPublisherId();
    Integer getCountComment();
    String getPublisherName();
    Long getArticleId();
    Boolean getScaleImage();
    Double getAverageRatingCriteria1();
    Double getAverageRatingCriteria2();
    Double getAverageRatingCriteria3();
    Double getTotalAverageRating();
    Long getCategoryId();
    String getCategoryName();
}
