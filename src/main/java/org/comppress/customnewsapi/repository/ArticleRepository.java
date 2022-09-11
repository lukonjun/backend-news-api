package org.comppress.customnewsapi.repository;

import org.comppress.customnewsapi.entity.ArticleEntity;
import org.comppress.customnewsapi.entity.article.CustomArticle;
import org.comppress.customnewsapi.entity.article.CustomRatedArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {

    Page<ArticleEntity> findByIsAccessibleUpdatedFalse(Pageable pageable);

    boolean existsById(Long id);

    Optional<ArticleEntity> findByGuid(String guid);

    @Query(value = "SELECT * FROM article ORDER BY RAND() LIMIT :numberArticles ", nativeQuery = true)
    List<ArticleEntity> retrieveRandomArticles(@Param("numberArticles") Integer numberArticles);

    @Query(value = """
            Select a.id, a.author, a.title, a.description, a.url, a.url_to_image as urlToImage, a.published_at as publishedAt,
                   a.count_ratings as countRatings, a.count_comment as countComment, a.is_accessible as isAccessible, a.scale_image as scaleImage,
                   rf.category_id as categoryId, rf.publisher_id as publisherId, 
                   c.name as categoryName, 
                   p.name as publisherName
            from article a
                JOIN rss_feed rf on rf.id = a.rss_feed_id
                JOIN category c on c.id = rf.category_id
                JOIN publisher p on p.id = rf.publisher_id
            WHERE rf.category_id = :categoryId
              AND a.published_at is not null Order by a.published_at DESC Limit 1
            """, nativeQuery = true)
    CustomRatedArticle retrieveLatestArticleOfCategory(@Param("categoryId") Long categoryId);

    @Query(value = """
            Select a.id, a.author, a.title, a.description, a.url, a.url_to_image as urlToImage, a.published_at as publishedAt,
                   a.count_ratings as countRatings, a.count_comment as countComment, a.is_accessible as isAccessible, a.scale_image as scaleImage,
                   rf.category_id as categoryId, rf.publisher_id as publisherId,
                   c.name as categoryName,
                   p.name as publisherName
            from article a
                     JOIN rss_feed rf on rf.id = a.rss_feed_id
                     JOIN category c on c.id = rf.category_id
                     JOIN publisher p on p.id = rf.publisher_id
            WHERE (:category is null or :category = '' or c.name LIKE %:category%) 
              AND (:publisherName is null or :publisherName = '' or p.name LIKE %:publisherName%)
              AND (:title is null or :title = '' or a.title LIKE %:title%)
              AND (:language is null or :language = '' or rf.lang LIKE :language)
              AND (:isAccessible =  0 or :isAccessible = a.is_accessible)
              AND a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now())
            """,
            countQuery = """
            Select count(*) from article a
                     JOIN rss_feed rf on rf.id = a.rss_feed_id
                     JOIN category c on c.id = rf.category_id
                     JOIN publisher p on p.id = rf.publisher_id
            WHERE (:category is null or :category = '' or c.name LIKE %:category%)
              AND (:publisherName is null or :publisherName = '' or p.name LIKE %:publisherName%)
              AND (:title is null or :title = '' or a.title LIKE %:title%)
              AND (:language is null or :language = '' or rf.lang LIKE :language)
              AND (:isAccessible =  0 or :isAccessible = a.is_accessible)
              AND a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now())
            """, nativeQuery = true)
    Page<CustomArticle> retrieveByCategoryOrPublisherNameToCustomArticle(
            @Param("category") String category,
            @Param("publisherName") String publisherName,
            @Param("title") String title,
            @Param("language") String language,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("isAccessible") Boolean isAccessible,
            Pageable pageable);

    @Query(value = """
            select a.id, a.author, a.title, a.description, a.url, a.url_to_image as urlToImage, a.published_at as publishedAt,
                   a.count_ratings as countRatings, a.count_comment as countComment, a.is_accessible as isAccessible, a.scale_image as scaleImage,
                   rf.category_id as categoryId, rf.publisher_id as publisherId,
                   c.name as categoryName,
                   p.name as publisherName,
                   t.average_rating_criteria_1 as averageRatingCriteria1, t.average_rating_criteria_2 as averageRatingCriteria2, t.average_rating_criteria_3 as averageRatingCriteria3,
                   sum(t.average_rating_criteria_1 + t.average_rating_criteria_2 + t.average_rating_criteria_3)/
                   (CASE WHEN t.average_rating_criteria_1 IS NULL THEN 0 ELSE 1 END + 
                   CASE WHEN t.average_rating_criteria_2 IS NULL THEN 0 ELSE 1 END + 
                   CASE WHEN t.average_rating_criteria_3 IS NULL THEN 0 ELSE 1 END)
                       AS totalAverageRating from (
                           SELECT distinct r.article_id,
                                           (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1) as average_rating_criteria_1,
                                           (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2) as average_rating_criteria_2,
                                           (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3) as average_rating_criteria_3
                           FROM rating r group by r.article_id
                       )as t
                           INNER JOIN article a ON a.id= t.article_id
                           INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id
                           INNER JOIN category c ON c.id = rf.category_id
                           INNER JOIN publisher p ON p.id = rf.publisher_id
            WHERE rf.category_id = :categoryId
              AND rf.publisher_id in (:publisherIds)
              AND (:language is null or :language = '' or rf.lang LIKE :language)
              AND (:isAccessible =  0 or :isAccessible = a.is_accessible)
              AND a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now())
            group by t.article_id order by totalAverageRating DESC
            """, nativeQuery = true)
    List<CustomRatedArticle> retrieveAllRatedArticlesInDescOrder(
            @Param("categoryId") Long categoryId,
            @Param("publisherIds") List<Long> publisherIds,
            @Param("language") String language,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("isAccessible") Boolean isAccessible);


    @Query(value = """
select a.id, a.author, a.title, a.description, a.url, a.url_to_image as urlToImage, a.published_at as publishedAt,
       a.count_ratings as countRatings, a.count_comment as countComment, a.is_accessible as isAccessible, a.scale_image as scaleImage,
       rf.category_id as categoryId, rf.publisher_id as publisherId,
       c.name as categoryName,
       p.name as publisherName,
t.average_rating_criteria_1 as averageRatingCriteria1, t.average_rating_criteria_2 as averageRatingCriteria2, t.average_rating_criteria_3 as averageRatingCriteria3,
       sum(t.average_rating_criteria_1 + t.average_rating_criteria_2 + t.average_rating_criteria_3)/
       (CASE WHEN t.average_rating_criteria_1 IS NULL THEN 0 ELSE 1 END +
        CASE WHEN t.average_rating_criteria_2 IS NULL THEN 0 ELSE 1 END +
        CASE WHEN t.average_rating_criteria_3 IS NULL THEN 0 ELSE 1 END)
            AS totalAverageRating from (
                SELECT distinct r.article_id,
                              (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1) as average_rating_criteria_1,
                              (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2) as average_rating_criteria_2,
                              (select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3) as average_rating_criteria_3
                FROM rating r group by r.article_id
            )as t
                INNER JOIN article a ON a.id= t.article_id
                INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id
                INNER JOIN category c ON c.id = rf.category_id
                INNER JOIN publisher p ON p.id = rf.publisher_id
           WHERE ( rf.lang = :lang )
             AND c.id = :categoryId
             AND p.id in :publisherIds
             AND (:isAccessible =  0 or :isAccessible = a.is_accessible)
             AND a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now())
            group by t.article_id order by totalAverageRating DESC LIMIT 1
            """, nativeQuery = true)
    CustomRatedArticle retrieveOneRatedArticleByCategoryIdsAndPublisherIdsAndLanguageAndLimit(
            @Param("categoryId") Long categoryId,
            @Param("publisherIds") List<Long> publisherIds,
            @Param("lang") String lang,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("isAccessible") Boolean isAccessible
    );

    @Query(value = "SELECT a.id as id, a.author as author, a.title as title, a.description as description, " +
            "a.url as url, a.url_to_image as urlToImage, a.published_at as publishedAt, a.count_ratings as countRatings, " +
            "a.is_accessible as isAccessible, a.scale_image as scaleImage, p.id as publisherId, p.name as publisherName, " +
            "0 as countComment, c.id as categoryId, c.name as categoryName " +
            "FROM article a JOIN rss_feed rf on rf.id = a.rss_feed_id " +
            "JOIN publisher p on p.id = rf.publisher_id " +
            "JOIN category c on c.id = rf.category_id " +
            "WHERE a.count_ratings = 0 AND " +
            "rf.category_id = :categoryId AND " +
            "rf.publisher_id in (:publisherIds) AND " +
            "rf.lang = :lang AND " +
            "(:isAccessible =  0 or :isAccessible = a.is_accessible) AND " +
            "a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now()) " +
            "ORDER BY a.published_at DESC",
            countQuery = "SELECT count(*) " +
                    "FROM article a JOIN rss_feed rf on rf.id = a.rss_feed_id " +
                    "JOIN publisher p on p.id = rf.publisher_id " +
                    "JOIN category c on c.id = rf.category_id " +
                    "WHERE a.count_ratings = 0 AND " +
                    "rf.category_id = :categoryId AND " +
                    "rf.publisher_id in (:publisherIds) AND " +
                    "rf.lang = :lang AND " +
                    "(:isAccessible =  0 or :isAccessible = a.is_accessible) AND " +
                    "a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now())"
            , nativeQuery = true)
    Page<CustomArticle> retrieveUnratedArticlesByCategoryIdAndPublisherIdsAndLanguage(
            @Param("categoryId") Long categoryId,
            @Param("publisherIds") List<Long> publisherIds,
            @Param("lang") String lang,
            @Param("isAccessible") Boolean isAccessible,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query(value = "select c.id as category_id, c.name as category_name, 0 as count_comment, t.article_id, a.author, a.title, a.description, a.url, a.url_to_image, a.guid, a.published_at, a.content, a.count_ratings, a.is_accessible, a.scale_image, p.id as publisher_id, p.name as publisher_name,t.average_rating_criteria_1, t.average_rating_criteria_2, t.average_rating_criteria_3, sum(t.average_rating_criteria_1 + t.average_rating_criteria_2 + t.average_rating_criteria_3)/" +
            "(CASE WHEN  t.average_rating_criteria_1 IS NULL THEN 0 ELSE 1 END + CASE WHEN t.average_rating_criteria_2 IS NULL THEN 0 ELSE 1 END + CASE WHEN t.average_rating_criteria_3 IS NULL THEN 0 ELSE 1 END) AS total_average_rating " +
            "from (SELECT distinct r.article_id, r.user_id, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1) as average_rating_criteria_1, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2) as average_rating_criteria_2, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3) as average_rating_criteria_3 " +
            "FROM rating r group by r.article_id, r.user_id) as t " +
            "INNER JOIN article a ON a.id= t.article_id " +
            "INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id " +
            "INNER JOIN category c ON c.id = rf.category_id " +
            "INNER JOIN publisher p ON p.id = rf.publisher_id " +
            "WHERE count_ratings > 0 AND " +
            "t.user_id = :userId AND " +
            "a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now()) " +
            "group by t.article_id order by total_average_rating DESC;",
            nativeQuery = true)
    List<CustomRatedArticle> getRatedArticleFromUser(
            @Param("userId") Long userId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query(value = "select c.id as category_id, c.name as category_name, 0 as count_comment, t.article_id, a.author, a.title, a.description, a.url, a.url_to_image, a.guid, a.published_at, a.content, a.count_ratings, a.is_accessible, a.scale_image, p.id as publisher_id, p.name as publisher_name,t.average_rating_criteria_1, t.average_rating_criteria_2, t.average_rating_criteria_3, sum(t.average_rating_criteria_1 + t.average_rating_criteria_2 + t.average_rating_criteria_3)/" +
            "(CASE WHEN  t.average_rating_criteria_1 IS NULL THEN 0 ELSE 1 END + CASE WHEN t.average_rating_criteria_2 IS NULL THEN 0 ELSE 1 END + CASE WHEN t.average_rating_criteria_3 IS NULL THEN 0 ELSE 1 END) AS total_average_rating " +
            "from (SELECT distinct r.article_id, r.user_id, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=1) as average_rating_criteria_1, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=2) as average_rating_criteria_2, " +
            "(select avg(r1.rating) from rating r1 where r1.article_id = r.article_id AND r1.criteria_id=3) as average_rating_criteria_3 " +
            "FROM rating r group by r.article_id, r.user_id) as t " +
            "INNER JOIN article a ON a.id= t.article_id " +
            "INNER JOIN rss_feed rf ON rf.id = a.rss_feed_id " +
            "INNER JOIN category c ON c.id = rf.category_id " +
            "INNER JOIN publisher p ON p.id = rf.publisher_id " +
            " WHERE " +
            "t.user_id = :userId AND " +
            "a.published_at BETWEEN IFNULL(:fromDate, '1900-01-01 00:00:00') AND IFNULL(:toDate,now()) " +
            "group by t.article_id order by total_average_rating DESC;"
            , nativeQuery = true)
    List<CustomRatedArticle> retrieveAllPersonalRatedArticlesInDescOrder(
            @Param("userId") Long userId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);


}
