package org.comppress.customnewsapi.entity.article;

public interface CustomRatedArticle extends CustomArticle{
    Double getAverageRatingCriteria1();
    Double getAverageRatingCriteria2();
    Double getAverageRatingCriteria3();
    Double getTotalAverageRating();
}
