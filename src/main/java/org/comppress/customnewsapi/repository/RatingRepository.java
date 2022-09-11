package org.comppress.customnewsapi.repository;

import org.comppress.customnewsapi.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<RatingEntity,Long> {


    @Query(value = "SELECT * From rating rt " +
            "JOIN article a on a.id = rt.article_id " +
            "JOIN user u on rt.user_id " +
            "WHERE u.name LIKE :username AND a.id LIKE :articleId ;",nativeQuery = true)
    RatingEntity retrieveByUserNameAndArticleId(@Param("username") String username, @Param("articleId") String articleId);

    RatingEntity findByUserIdAndArticleIdAndCriteriaId(Long userId, Long articleId, Long criteriaId);

    RatingEntity findByGuidAndArticleIdAndCriteriaId(String guid, Long articleId, Long criteriaId);

    List<RatingEntity> findByArticleId(Long articleId);

    List<RatingEntity> findByUserIdAndArticleId(Long userId, Long articleId);

    List<RatingEntity> findByGuidAndArticleId(String guid, Long articleId);

    @Query(value = "SELECT AVG(rating) FROM rating WHERE article_id = :articleId AND criteria_id = :criteriaId ", nativeQuery = true)
    Double retrieveAverageRatingOfArticleForCriteria(@Param("articleId") Long articleId,
                                                     @Param("criteriaId") Long criteriaId);
}
